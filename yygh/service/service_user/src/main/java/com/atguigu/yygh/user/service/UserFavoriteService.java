package com.atguigu.yygh.user.service;

import com.atguigu.yygh.model.user.UserFavorite;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface UserFavoriteService extends IService<UserFavorite> {

    List<UserFavorite> findAllByUserId(Long userId);

    boolean isFavorite(Long userId, String hoscode);

    void addFavorite(Long userId, String hoscode, String hosname);

    void cancelFavorite(Long userId, String hoscode);
}