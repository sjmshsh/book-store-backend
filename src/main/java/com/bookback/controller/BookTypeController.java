package com.bookback.controller;


import com.bookback.model.BookTypeDO;
import com.bookback.service.BookTypeService;
import com.bookback.utils.JsonResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author pluto
 * @since 2022-11-20
 */
@RestController
@RequestMapping("/type")
public class BookTypeController {

    @Autowired
    BookTypeService bookTypeService;

    @GetMapping("list")
    @ResponseBody
    public JsonResponse<List<BookTypeDO>> list() {
        List<BookTypeDO> list = bookTypeService.list();
        return new JsonResponse<>(list);
    }
}

