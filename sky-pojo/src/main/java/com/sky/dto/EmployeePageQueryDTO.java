package com.sky.dto;

import com.sky.query.PageQuery;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

@Data
@ToString(callSuper = true)  // 包含父类的 toString
public class EmployeePageQueryDTO extends PageQuery implements Serializable {

    //员工姓名
    private String name;


}
