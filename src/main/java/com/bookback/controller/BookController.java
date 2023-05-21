package com.bookback.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bookback.exception.ConditionException;
import com.bookback.model.BookDO;
import com.bookback.model.BookTypeDO;
import com.bookback.service.BookService;
import com.bookback.service.BookTypeService;
import com.bookback.utils.JsonResponse;
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
 * @since 2022-11-20
 */
@RestController
@RequestMapping("/book")
public class BookController {
    @Autowired
    BookService bookService;

    @Autowired
    BookTypeService bookTypeService;

    @ApiOperation(value = "添加书籍", notes = "添加书籍")

    @PostMapping("/add")
    @ResponseBody
    @Transactional(rollbackFor = Exception.class)
    public JsonResponse<String> addBook(@RequestBody BookDO bookDO) {
        // 1.检测传入参数
        if (!StringUtils.isNotEmpty(bookDO.getName())) {
            throw new ConditionException("书名不能为空");
        }
        if (bookDO.getPrice() == 0) {
            throw new ConditionException("书籍价格不能为0");
        }
        if (!StringUtils.isNotEmpty(bookDO.getType())) {
            throw new ConditionException("书籍需要分类");
        }
        if (!StringUtils.isNotEmpty(bookDO.getImageUrl())) {
            throw new ConditionException("书籍需要封面图片");
        }
        // 书籍是否已经存在
        BookDO dbBook = bookService.getOne(new QueryWrapper<BookDO>().eq("name", bookDO.getName()));
        if (dbBook != null) {
            throw new ConditionException("书籍已存在");
        }

        // 2.查找是否含有该书籍分类
        BookTypeDO bookTypeDO = bookTypeService.getOne(new QueryWrapper<BookTypeDO>().eq("name", bookDO.getType()));
        if (bookTypeDO == null) { // 书籍分类不存在
            // 1.创建该分类
            BookTypeDO tempBookTypeDO = new BookTypeDO();
            tempBookTypeDO.setName(bookDO.getType());
            boolean save = bookTypeService.save(tempBookTypeDO);
            if (!save) {
                throw new ConditionException("书籍分类创建失败");
            }
        }

        // 3. 创建
        BookDO dbBookDo = new BookDO();
        dbBookDo.setName(bookDO.getName());
        dbBookDo.setPrice(bookDO.getPrice());
        dbBookDo.setType(bookDO.getType());
        dbBookDo.setInfo(bookDO.getInfo());
        dbBookDo.setCreateAt(new Date());
        dbBookDo.setImageUrl(bookDO.getImageUrl());
        dbBookDo.setAuthor(bookDO.getAuthor());
        dbBookDo.setPublishTime(bookDO.getPublishTime());
        dbBookDo.setScore(bookDO.getScore());
        // 4. 存入数据库
        boolean save = bookService.save(dbBookDo);
        if (!save) {
            throw new ConditionException("书籍创建失败");
        }
        return JsonResponse.success();
    }

    @ApiOperation(value = "查询", notes = "书名分页查询书籍")
    @GetMapping("/find")
    @ResponseBody
    public JsonResponse<HashMap<String, Object>> find(@RequestParam String bookName,
                                                      @RequestParam int pageNo,
                                                      @RequestParam int pageSize
    ) {
        QueryWrapper<BookDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.like("name", bookName);
        queryWrapper.orderByAsc("id");
        // 设置分页数据
        IPage<BookDO> page = new Page<>();
        page.setCurrent(pageNo);
        page.setSize(pageSize);
        IPage<BookDO> bookDOIPage = bookService.page(page, queryWrapper);
        HashMap<String, Object> result = new HashMap<>();
        result.put("total", bookDOIPage.getTotal());
        result.put("list", bookDOIPage.getRecords());
        return new JsonResponse<>(result);
    }

    @ApiOperation(value = "更新", notes = "更新书籍信息")
    @PutMapping("/update")
    @ResponseBody
    public JsonResponse<String> update(@RequestParam int id, @RequestBody BookDO bookDO) {
        // 1. 根据Id检查书籍是否存在
        BookDO dbBook = bookService.getById(id);
        if (dbBook == null) {
            throw new ConditionException("书籍不存在");
        }
        // 2.更新
        //  书名
        if (StringUtils.isNotEmpty(bookDO.getName())) {
            dbBook.setName(bookDO.getName());
        }
        if (bookDO.getPrice() != 0) {
            dbBook.setPrice(bookDO.getPrice());
        }
        if (StringUtils.isNotEmpty(bookDO.getInfo())) {
            dbBook.setInfo(bookDO.getInfo());
        }
        if (StringUtils.isNotEmpty(bookDO.getImageUrl())) {
            dbBook.setImageUrl(bookDO.getImageUrl());
        }

        if (StringUtils.isNotEmpty(bookDO.getType())) {
            // 2.查找是否含有该书籍分类
            BookTypeDO bookTypeDO = bookTypeService.getOne(new QueryWrapper<BookTypeDO>().eq("name", bookDO.getType()));
            if (bookTypeDO == null) { // 书籍分类不存在
                // 1.创建该分类
                BookTypeDO tempBookTypeDO = new BookTypeDO();
                tempBookTypeDO.setName(bookDO.getType());
                boolean save = bookTypeService.save(tempBookTypeDO);
                if (!save) {
                    throw new ConditionException("书籍分类创建失败");
                }
            }
        }

        dbBook.setUpdateAt(new Date());
        boolean update = bookService.updateById(dbBook);
        if (!update) {
            throw new ConditionException("更新失败");
        }
        return JsonResponse.success();
    }


    @ApiOperation(value = "删除", notes = "删除书籍")
    @DeleteMapping("/delete")
    @ResponseBody
    public JsonResponse<String> delete(@RequestParam int id) {
        // 1.查找书籍是否存在
        BookDO dbBook = bookService.getById(id);
        if (dbBook == null) {
            throw new ConditionException("书籍不存在");
        }
        boolean remove = bookService.removeById(id);
        if (!remove) {
            throw new ConditionException("删除书籍失败");
        }
        return JsonResponse.success();
    }
}

