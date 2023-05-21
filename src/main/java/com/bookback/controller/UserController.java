package com.bookback.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bookback.exception.ConditionException;
import com.bookback.model.UserDO;
import com.bookback.service.impl.UserServiceImpl;
import com.bookback.support.UserSupport;
import com.bookback.utils.JsonResponse;
import com.bookback.utils.MD5Util;
import com.bookback.utils.TokenUtil;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author pluto
 * @since 2022-11-19
 */
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    UserServiceImpl userService;

    @ApiOperation(value = "注册用户", notes = "根据手机号和密码创建用户")
    @PostMapping("/register")
    @ResponseBody
    public JsonResponse<String> register(@RequestBody UserDO userDO) {
        // 1.校验是否传入手机号或密码
        if (!StringUtils.isNotEmpty(userDO.getPhone()) || !StringUtils.isNotEmpty(userDO.getPassword())) {
            throw new ConditionException("注册信息不全!");
        }
        String phone = userDO.getPhone();
        String password = userDO.getPassword();
        // 2.检验手机号是否已注册
        UserDO dbUser = userService.getOne(new QueryWrapper<UserDO>().eq("phone", phone));
        if (dbUser != null) {
            throw new ConditionException("手机号已注册");
        }
        // 2.手机号作为盐 对用户密码进行加密
        String encryptPassword = MD5Util.sign(password, phone, "UTF-8");
        userDO.setPassword(encryptPassword);
        // 3.用户存入数据库
        userDO.setCreateAt(new Date());
        userDO.setNickName(userDO.getNickName());
        boolean save = userService.save(userDO);
        if (!save) {
            throw new ConditionException("用户注册失败");
        }
        return JsonResponse.success();
    }

    @ApiOperation(value = "用户登录", notes = "通过手机号和密码进行登录")
    @PostMapping("/login")
    @ResponseBody
    public JsonResponse<HashMap<String, String>> login(@RequestBody UserDO userDO) {
        // 1.检验是否输入手机号
        if (!StringUtils.isNotEmpty(userDO.getPhone()) || !StringUtils.isNotEmpty(userDO.getPassword())) {
            throw new ConditionException("登录信息!");
        }
        String phone = userDO.getPhone();
        String password = userDO.getPassword();
        // 2.根据手机号从数据库中查找 是否有这个人
        UserDO dbUser = userService.getOne(new QueryWrapper<UserDO>().eq("phone", phone));
        if (dbUser == null) {
            throw new ConditionException("用户不存在!");
        }
        // 3. 对用户输入的密码进行加密
        String encryptPassword = MD5Util.sign(password, phone, "UTF-8");
        if (!encryptPassword.equals(dbUser.getPassword())) {
            throw new ConditionException("密码错误");
        }
        String token;
        try {
            token = TokenUtil.generateToken(dbUser.getId());
        } catch (Exception e) {
            throw new ConditionException("token生成失败");
        }
        HashMap<String, String> result = new HashMap<>();
        result.put("token", token);
        result.put("name",dbUser.getNickName());
        result.put("address",dbUser.getAddress());
        return new JsonResponse<>(result);
    }

    @ApiOperation(value = "更新用户信息", notes = "根据token以及传入的json更新用户")
    @PutMapping("/update")
    @ResponseBody
    public JsonResponse<String> update(@RequestBody UserDO userDO) {
        int id = UserSupport.getCurrentUserId();
        UserDO dbUser = userService.getById(id);
        String phone = dbUser.getPhone();
        if (StringUtils.isNotEmpty(userDO.getPassword())) {
            String newPassword = MD5Util.sign(userDO.getPassword(), phone, "UTF-8");
            dbUser.setPassword(newPassword);
        }
        if (StringUtils.isNotEmpty(userDO.getNickName())) {
            dbUser.setNickName(userDO.getNickName());
        }
        if (StringUtils.isNotEmpty(userDO.getAddress())) {
            dbUser.setAddress(userDO.getAddress());
        }
        userService.updateById(dbUser);
        return JsonResponse.success();
    }

    @ApiOperation(value = "更新用户信息", notes = "根据token以删除用户")
    @DeleteMapping("/delete")
    @ResponseBody
    public JsonResponse<String> delete(@RequestParam int id) {
        UserDO dbUser = userService.getById(id);
        if (dbUser == null) {
            throw new ConditionException("用户不存在");
        }
        userService.removeById(id);
        return JsonResponse.success();
    }

    @GetMapping("/info")
    @ResponseBody
    public JsonResponse<UserDO> getUserInfo() {
        int userId = UserSupport.getCurrentUserId();
        UserDO userInfo = userService.getById(userId);
        return new JsonResponse<>(userInfo);
    }

    @GetMapping("/list")
    @ResponseBody
    @Transactional(rollbackFor = Exception.class)
    public JsonResponse<HashMap<String, Object>> list(@RequestParam String phone, @RequestParam int pageNo, @RequestParam int pageSize) {
        QueryWrapper<UserDO> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotEmpty(phone)) {
            queryWrapper.like("phone", phone);
        }
        queryWrapper.orderByAsc("id");
        // 设置分页数据
        IPage<UserDO> page = new Page<>();
        page.setCurrent(pageNo);
        page.setSize(pageSize);
        IPage<UserDO> orderDOIPage = userService.page(page, queryWrapper);
        HashMap<String, Object> result = new HashMap<>();
        result.put("total", orderDOIPage.getTotal());
        result.put("list", orderDOIPage.getRecords());
        return new JsonResponse<>(result);
    }
}

