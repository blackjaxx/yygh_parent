package com.atguigu.yygh.model.user;

import com.atguigu.yygh.model.base.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "用户收藏的医院")
@TableName("user_favorite")
public class UserFavorite extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "用户id")
    @TableField("user_id")
    private Long userId;

    @ApiModelProperty(value = "医院编号")
    @TableField("hoscode")
    private String hoscode;

    @ApiModelProperty(value = "医院名称（冗余字段，方便展示）")
    @TableField("hosname")
    private String hosname;
}