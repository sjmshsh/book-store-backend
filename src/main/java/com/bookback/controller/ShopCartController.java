package com.bookback.controller;


import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bookback.exception.ConditionException;
import com.bookback.model.BookDO;
import com.bookback.model.ShopCartDO;
import com.bookback.service.BookService;
import com.bookback.service.ShopCartService;
import com.bookback.service.UserService;
import com.bookback.support.UserSupport;
import com.bookback.utils.JsonResponse;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.management.Query;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author pluto
 * @since 2022-11-21
 */
@RestController
@RequestMapping("/shop_cart")
public class ShopCartController {

    @Autowired
    BookService bookService;

    @Autowired
    UserService userService;

    @Autowired
    ShopCartService shopCartService;

    @PostMapping("/add")
    @ResponseBody
    public JsonResponse<String> addShopCart(@RequestBody ShopCartDO shopCartDO) {
        int userId = UserSupport.getCurrentUserId();
        // 1.查看用户是已经加入了该书籍
        QueryWrapper<ShopCartDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        queryWrapper.eq("book_id", shopCartDO.getBookId());
        queryWrapper.eq("order_id",0);
        ShopCartDO dbShopCart = shopCartService.getOne(queryWrapper);
        if (dbShopCart != null) { // 购物项已存在
            // 1.查询书籍是否存在
            BookDO dbBook = bookService.getById(shopCartDO.getBookId());
            if (dbBook == null) {
                throw new ConditionException("书籍不存在");
            }
            // 2.判断num是否存在
            if (shopCartDO.getNum() == 0) {
                throw new ConditionException("购买数量不能为0");
            }
            dbShopCart.setBookId(dbBook.getId());
            dbShopCart.setNum(shopCartDO.getNum());
            int bookPrice = dbBook.getPrice();
            int total = bookPrice * shopCartDO.getNum();
            dbShopCart.setTotal(total);
            dbShopCart.setUpdateAt(new Date());
            boolean update = shopCartService.updateById(dbShopCart);
            if (!update) {
                throw new ConditionException("购物车添加失败");
            }
        } else {
            BookDO dbBook = bookService.getById(shopCartDO.getBookId());
            if (dbBook == null) {
                throw new ConditionException("书籍不存在");
            }
            // 2.判断num是否存在
            if (shopCartDO.getNum() == 0) {
                throw new ConditionException("购买数量不能为0");
            }
            int bookPrice = dbBook.getPrice();
            int total = bookPrice * shopCartDO.getNum();
            shopCartDO.setTotal(total);
            shopCartDO.setCreateAt(new Date());
            shopCartDO.setUserId(userId);
            boolean save = shopCartService.save(shopCartDO);
            if (!save) {
                throw new ConditionException("购物车添加失败");
            }
        }

        return JsonResponse.success();
    }

    @PutMapping("/update")
    @ResponseBody
    public JsonResponse<String> updateShopCart(@RequestParam int cartId, @RequestBody ShopCartDO shopCartDO) {
        // 1.判断Id是否存在
        ShopCartDO dbShopCart = shopCartService.getById(cartId);
        if (dbShopCart == null) {
            throw new ConditionException("该项目不存在");
        }
        // 2.
        int newNum = shopCartDO.getNum();
        dbShopCart.setNum(newNum);
        // 3.计算价格
        int bookId = dbShopCart.getBookId();
        BookDO dbBook = bookService.getById(bookId);
        if (dbBook == null) {
            throw new ConditionException("书籍不存在");
        }
        int price = dbBook.getPrice();
        dbShopCart.setTotal(price * newNum);
        dbShopCart.setUpdateAt(new Date());
        boolean save = shopCartService.updateById(dbShopCart);
        if (!save) {
            throw new ConditionException("购物车更新失败");
        }
        return JsonResponse.success();
    }

    @DeleteMapping("/delete")
    @ResponseBody
    public JsonResponse<String> deleteShopCart(@RequestParam int cartId) {
        // 1.判断Id是否存在
        ShopCartDO dbShopCart = shopCartService.getById(cartId);
        if (dbShopCart == null) {
            throw new ConditionException("该项目不存在");
        }
        // 2.
        boolean update = shopCartService.removeById(dbShopCart);
        if (!update) {
            throw new ConditionException("删除购物车失败");
        }
        return JsonResponse.success();
    }

    @GetMapping("/user_list")
    @ResponseBody
    @Transactional(rollbackFor = Exception.class)
    public JsonResponse<HashMap<String, Object>> getUserShopCartList(@RequestParam int pageNo, @RequestParam int pageSize) {
        int userId = UserSupport.getCurrentUserId();
        // 获取该用户的全部全部购物车
        QueryWrapper<ShopCartDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        queryWrapper.eq("order_id",0);
        Page<ShopCartDO> page = new Page<>();
        page.setCurrent(pageNo);
        page.setSize(pageSize);
        Page<ShopCartDO> shopCartDOPage = shopCartService.page(page, queryWrapper);
        List<ShopCartDO> shopCartList = shopCartDOPage.getRecords();

        // 查询对应ID的书籍信息
        for (ShopCartDO temp :
                shopCartList) {
            Integer bookId = temp.getBookId();
            BookDO dbBook = bookService.getById(bookId);
            if (dbBook == null) {
                throw new ConditionException("书籍不存在");
            }
            temp.setBookInfo(dbBook);
        }

        HashMap<String, Object> result = new HashMap<>();
        result.put("total", shopCartDOPage.getTotal());
        result.put("list", shopCartList);
        return new JsonResponse<>(result);
    }

    @GetMapping("/all_list")
    @ResponseBody
    public JsonResponse<HashMap<String, Object>> getAllShopCart(@RequestParam int pageNo, @RequestParam int pageSize) {
        QueryWrapper<ShopCartDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByAsc("id");
        Page<ShopCartDO> page = new Page<>();
        page.setCurrent(pageNo);
        page.setSize(pageSize);
        Page<ShopCartDO> shopCartDOPage = shopCartService.page(page, queryWrapper);
        List<ShopCartDO> shopCartList = shopCartDOPage.getRecords();
        // 查询对应ID的书籍信息
        for (ShopCartDO temp :
                shopCartList) {
            Integer bookId = temp.getBookId();
            BookDO dbBook = bookService.getById(bookId);
            if (dbBook == null) {
                throw new ConditionException("书籍不存在");
            }
            temp.setBookInfo(dbBook);
        }

        HashMap<String, Object> result = new HashMap<>();
        result.put("total", shopCartDOPage.getTotal());
        result.put("list", shopCartList);
        return new JsonResponse<>(result);
    }
}

