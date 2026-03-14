package com.sky.dto;

import com.sky.query.PageQuery;
import lombok.Data;

import java.io.Serializable;

@Data
public class DishPageQueryDTO extends PageQuery implements Serializable  {

    private String name;
    //分类id
    private Integer categoryId;

    //状态 0表示禁用 1表示启用
    private Integer status;

}
