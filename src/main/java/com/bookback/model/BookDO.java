package com.bookback.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableId;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 *
 * </p>
 *
 * @author pluto
 * @since 2022-11-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("book")
public class BookDO implements Serializable {

    private static final long serialVersionUID = 1L;
    @ApiModelProperty(hidden=true)
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private String name;

    private Integer price;

    private String info;

    private String type;

    private String imageUrl;

    private String author;

    private String publishTime;

    private String score;

    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date createAt;

    @ApiModelProperty(hidden=true)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date updateAt;

    @ApiModelProperty(hidden=true)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Integer deleted;


}
