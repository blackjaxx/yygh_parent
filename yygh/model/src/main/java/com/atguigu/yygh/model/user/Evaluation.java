package com.atguigu.yygh.model.user;

import com.atguigu.yygh.model.base.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "就诊评价")
@TableName("evaluation")
public class Evaluation extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "用户id")
    @TableField("user_id")
    private Long userId;

    @ApiModelProperty(value = "用户昵称（脱敏展示）")
    @TableField("user_name")
    private String userName;

    @ApiModelProperty(value = "医院编号")
    @TableField("hoscode")
    private String hoscode;

    @ApiModelProperty(value = "医院名称")
    @TableField("hosname")
    private String hosname;

    @ApiModelProperty(value = "科室名称")
    @TableField("depname")
    private String depname;

    @ApiModelProperty(value = "医生职称")
    @TableField("title")
    private String title;

    @ApiModelProperty(value = "订单id")
    @TableField("order_id")
    private Long orderId;

    @ApiModelProperty(value = "评分(1-5星)")
    @TableField("rating")
    private Integer rating;

    @ApiModelProperty(value = "评价内容")
    @TableField("content")
    private String content;
}