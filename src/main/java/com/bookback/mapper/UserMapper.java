package com.bookback.mapper;

import com.bookback.model.UserDO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author pluto
 * @since 2022-11-19
 */
@Mapper
public interface UserMapper extends BaseMapper<UserDO> {

}
