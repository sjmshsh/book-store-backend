package com.bookback.service.impl;

import com.bookback.model.BookTypeDO;
import com.bookback.mapper.BookTypeMapper;
import com.bookback.service.BookTypeService;
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
public class BookTypeServiceImpl extends ServiceImpl<BookTypeMapper, BookTypeDO> implements BookTypeService {

}
