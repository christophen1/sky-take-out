package com.sky.query;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "分页查询实体")
public class PageQuery {
    @ApiModelProperty( "当前页码")
    private Integer page = 1;
    @ApiModelProperty( "每页显示记录数")
    private Integer pageSize = 10;
    @ApiModelProperty( "排序字段")
    private String sortBy = "create_time";
    @ApiModelProperty( "排序方式 true:升序，false:降序")
    private Boolean isAsc =  true;
}
