package com.bookback.service.impl;

import com.bookback.model.BookDO;
import com.bookback.mapper.BookMapper;
import com.bookback.service.BookService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author pluto
 * @since 2022-11-20
 */
@Service
public class BookServiceImpl extends ServiceImpl<BookMapper, BookDO> implements BookService {

}
