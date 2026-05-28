package com.atguigu.yygh.user.controller;

import com.atguigu.yygh.common.result.Result;
import com.atguigu.yygh.common.util.AuthContextHolder;
import com.atguigu.yygh.model.user.UserFavorite;
import com.atguigu.yygh.user.service.UserFavoriteService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 用户收藏接口 - 医院收藏/取消收藏
 */
@RestController
@RequestMapping("/api/user/favorite")
public class FavoriteApiController {

    @Autowired
    private UserFavoriteService userFavoriteService;

    @ApiOperation("获取用户收藏的医院列表")
    @GetMapping("auth/list")
    public Result list(HttpServletRequest request) {
        Long userId = AuthContextHolder.getUserId(request);
        List<UserFavorite> list = userFavoriteService.findAllByUserId(userId);
        return Result.ok(list);
    }

    @ApiOperation("判断是否已收藏某医院")
    @GetMapping("auth/check/{hoscode}")
    public Result check(@PathVariable String hoscode, HttpServletRequest request) {
        Long userId = AuthContextHolder.getUserId(request);
        boolean favorited = userFavoriteService.isFavorite(userId, hoscode);
        return Result.ok(favorited);
    }

    @ApiOperation("收藏医院")
    @PostMapping("auth/add/{hoscode}/{hosname}")
    public Result add(@PathVariable String hoscode, @PathVariable String hosname,
                      HttpServletRequest request) {
        Long userId = AuthContextHolder.getUserId(request);
        userFavoriteService.addFavorite(userId, hoscode, hosname);
        return Result.ok();
    }

    @ApiOperation("取消收藏")
    @DeleteMapping("auth/cancel/{hoscode}")
    public Result cancel(@PathVariable String hoscode, HttpServletRequest request) {
        Long userId = AuthContextHolder.getUserId(request);
        userFavoriteService.cancelFavorite(userId, hoscode);
        return Result.ok();
    }
}