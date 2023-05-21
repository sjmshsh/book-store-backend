package com.bookback.controller;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.bookback.model.BookDO;
import com.bookback.model.OrderDO;
import com.bookback.service.BookService;
import com.bookback.service.BookTypeService;
import com.bookback.service.OrderService;
import com.bookback.service.UserService;
import com.bookback.utils.JsonResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("/statis")
public class StatisticsController {
    @Autowired
    BookService bookService;

    @Autowired
    UserService userService;

    @Autowired
    OrderService orderService;

    @Autowired
    BookTypeService bookTypeService;

    @GetMapping("/panel-data")
    @ResponseBody
    @Transactional(rollbackFor = Exception.class)
    public JsonResponse<HashMap<String, Object>> getPanelData() {
        HashMap<String, Object> result = new HashMap<>();

        int bookNums = bookService.count();
        int userNums = userService.count();
        int orderNums = orderService.count();
        // 遍历order表 计算总金额
        List<OrderDO> orderDOList = orderService.list();
        int total = 0;
        for (OrderDO orderInfo :
                orderDOList) {
            total += orderInfo.getTotal();
        }

        result.put("bookNum", bookNums);
        result.put("userNums", userNums);
        result.put("orderNums", orderNums);
        result.put("saleNums", total);
        return new JsonResponse<>(result);
    }

    @GetMapping("/broken-line")
    @ResponseBody
    @Transactional(rollbackFor = Exception.class)
    public JsonResponse<List<HashMap<String, Integer>>> getLast15DaysSaleCount() {

        List<HashMap<String, Integer>> list = new ArrayList<>();

        return new JsonResponse<>(list);
    }
}
