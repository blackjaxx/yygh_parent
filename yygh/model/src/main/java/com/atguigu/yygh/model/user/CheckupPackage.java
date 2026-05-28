package com.atguigu.yygh.model.user;

import com.atguigu.yygh.model.base.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
@ApiModel(description = "体检套餐")
@TableName("checkup_package")
public class CheckupPackage extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "医院编号")
    @TableField("hoscode")
    private String hoscode;

    @ApiModelProperty(value = "医院名称")
    @TableField("hosname")
    private String hosname;

    @ApiModelProperty(value = "套餐名称")
    @TableField("name")
    private String name;

    @ApiModelProperty(value = "套餐价格")
    @TableField("price")
    private BigDecimal price;

    @ApiModelProperty(value = "原价")
    @TableField("original_price")
    private BigDecimal originalPrice;

    @ApiModelProperty(value = "套餐内容")
    @TableField("content")
    private String content;

    @ApiModelProperty(value = "适用人群")
    @TableField("suitable")
    private String suitable;

    @ApiModelProperty(value = "排序")
    @TableField("sort")
    private Integer sort;
}