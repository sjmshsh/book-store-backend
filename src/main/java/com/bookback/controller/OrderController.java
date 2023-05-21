package com.bookback.controller;


import com.alipay.api.AlipayApiException;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bookback.exception.ConditionException;
import com.bookback.model.*;
import com.bookback.service.*;
import com.bookback.support.UserSupport;
import com.bookback.utils.JsonResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author pluto
 * @since 2022-11-22
 */
@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    UserService userService;

    @Autowired
    ShopCartService shopCartService;

    @Autowired
    BookService bookService;

    @Autowired
    OrderService orderService;

    @Autowired
    PayService payService;

    @RequestMapping(value = "/create", produces = {"text/html;charset=UTF-8"})
    @ResponseBody
    @Transactional(rollbackFor = Exception.class)
    public JsonResponse<HashMap<String, Object>> createOrder() throws AlipayApiException {
        OrderDO dbOrder = new OrderDO();
        //1.获取用户Id
        int userId = UserSupport.getCurrentUserId();
        HashMap<String, Object> result = new HashMap<>();
        // 2.获取用户信息
        UserDO userInfo = userService.getById(userId);
        if (StringUtils.isEmpty(userInfo.getPhone())) {
            throw new ConditionException("用户手机号为空,补全用户信息");
        }
        if (StringUtils.isEmpty(userInfo.getAddress())) {
            throw new ConditionException("用户地址为空,请补全用户信息");
        }
        result.put("user", userInfo);
        dbOrder.setPhone(userInfo.getPhone());
        dbOrder.setAddress(userInfo.getAddress());
        //3.获取用户购物车内内容
        QueryWrapper<ShopCartDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        queryWrapper.eq("order_id", 0);
        queryWrapper.eq("deleted", 0);
        List<ShopCartDO> shopCartDOList = shopCartService.list(queryWrapper);
        if (shopCartDOList.isEmpty()) {
            throw new ConditionException("用户购物车为空");
        }
        result.put("list", shopCartDOList);

        //3.计算金额数目综合
        int total = 0;
        for (ShopCartDO oneShopCart :
                shopCartDOList) {
            total += oneShopCart.getTotal();
        }
        result.put("total", total);
        Date now = new Date();
        result.put("createTime", now);
        //4.创建订单
        dbOrder.setUserId(userId);
        dbOrder.setTotal(total);
        dbOrder.setCreateAt(new Date());
        boolean save = orderService.save(dbOrder);
        if (!save) {
            throw new ConditionException("创建订单失败");
        }
        // 5. 购物车
        for (ShopCartDO oneShopCart :
                shopCartDOList) {
            oneShopCart.setOrderId(dbOrder.getId());
            boolean update = shopCartService.updateById(oneShopCart);
//            boolean remove = shopCartService.removeById(oneShopCart);
            if (!update) {
                throw new ConditionException("创建订单失败");
            }
        }

        AlipayDo alipayBean = new AlipayDo();
        alipayBean.setOut_trade_no(UUID.randomUUID().toString());
        alipayBean.setSubject("书城");
        alipayBean.setTotal_amount(total + "");
        alipayBean.setBody("这是一件很神奇得商品");
        result.put("content", payService.aliPay(alipayBean));
        return new JsonResponse<>(result);
    }

    @RequestMapping("/success")
    @ResponseBody
    public String success() {
        return "交易成功！";
    }

    @GetMapping("/list")
    @ResponseBody
    @Transactional(rollbackFor = Exception.class)
    public JsonResponse<HashMap<String, Object>> list(
            @RequestParam String phone,
            @RequestParam int pageNo,
            @RequestParam int pageSize
    ) {
        QueryWrapper<OrderDO> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotEmpty(phone)) {
            queryWrapper.like("phone", phone);
        }
        queryWrapper.orderByAsc("id");
        // 设置分页数据
        IPage<OrderDO> page = new Page<>();
        page.setCurrent(pageNo);
        page.setSize(pageSize);
        IPage<OrderDO> orderDOIPage = orderService.page(page, queryWrapper);

        HashMap<String, Object> result = new HashMap<>();
        List<HashMap<String, Object>> list = new ArrayList<>();
        // 遍历每一条订单
        for (OrderDO orderInfo :
                orderDOIPage.getRecords()) {
            HashMap<String, Object> detailInfo = new HashMap<>();
            // 1. 获取用户信息
            UserDO dbUserInfo = userService.getById(orderInfo.getUserId());
            detailInfo.put("userInfo", dbUserInfo);
            // 2.获取用户购物车信息
            List<ShopCartDO> shopCartDOList = shopCartService.list(new QueryWrapper<ShopCartDO>().eq("order_id", orderInfo.getId()));
            detailInfo.put("shopInfo", shopCartDOList);
            // 3.订单信息
            detailInfo.put("orderInfo", orderInfo);
            list.add(detailInfo);
        }

        result.put("list", list);
        result.put("total", orderDOIPage.getTotal());

        return new JsonResponse<>(result);
    }

    @GetMapping("/user_order")
    @ResponseBody
    public JsonResponse<HashMap<String, Object>> getUserOrder() {
        // 0.制定返回体
        HashMap<String, Object> result = new HashMap<>();

        // 1.获取用户Id和用户信息
        int userId = UserSupport.getCurrentUserId();
        UserDO dbUser = userService.getById(userId);
        if (dbUser == null) {
            throw new ConditionException("用户不存在");
        }
        result.put("userInfo", dbUser);
        // 2.根据用户Id查询用户订单
        QueryWrapper<OrderDO> orderQuery = new QueryWrapper<>();
        // 1.查询用户所有
        List<OrderDO> orderDOList = orderService.list(orderQuery.eq("user_id", dbUser.getId()));
        for (OrderDO oneOrder :
                orderDOList) {
            QueryWrapper<ShopCartDO> shopCartQuery = new QueryWrapper<>();
            shopCartQuery.eq("order_id", oneOrder.getId());
            List<ShopCartDO> shopCartList = shopCartService.list(shopCartQuery);
            oneOrder.setShopCartInfoList(shopCartList);
            for (ShopCartDO oneShopCart :
                    shopCartList) {
                BookDO bookInfo = bookService.getById(oneShopCart.getBookId());
                oneShopCart.setBookInfo(bookInfo);
            }
        }
        result.put("total", orderDOList.size());
        result.put("list", orderDOList);
        return new JsonResponse<>(result);
    }

    @GetMapping("/user_order_item")
    @ResponseBody
    public JsonResponse<HashMap<String, Object>> getUserOrderItemById(@RequestParam int orderId) {
        // 0.制定返回结构体
        HashMap<String, Object> result = new HashMap<>();
        // 1.查询orderId是否存在
        OrderDO dbOrder = orderService.getById(orderId);
        if (dbOrder == null) {
            throw new ConditionException("订单信息不存在");
        }
        QueryWrapper<ShopCartDO> shopCartQuery = new QueryWrapper<>();
        List<ShopCartDO> shopCartList = shopCartService.list(shopCartQuery.eq("order_id", orderId));
        for (ShopCartDO oneShopCart :
                shopCartList) {
            BookDO bookInfo = bookService.getById(oneShopCart.getBookId());
            oneShopCart.setBookInfo(bookInfo);
        }
        result.put("total", shopCartList.size());
        result.put("list", shopCartList);
        return new JsonResponse<>(result);
    }
}

