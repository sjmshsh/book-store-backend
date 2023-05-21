package com.bookback.service.impl;

import com.bookback.model.OrderDO;
import com.bookback.mapper.OrderMapper;
import com.bookback.service.OrderService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author pluto
 * @since 2022-11-22
 */
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, OrderDO> implements OrderService {

}
