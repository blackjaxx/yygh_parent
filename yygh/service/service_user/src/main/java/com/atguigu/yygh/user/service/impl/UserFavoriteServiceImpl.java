package com.atguigu.yygh.user.service.impl;

import com.atguigu.yygh.model.user.UserFavorite;
import com.atguigu.yygh.user.mapper.UserFavoriteMapper;
import com.atguigu.yygh.user.service.UserFavoriteService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserFavoriteServiceImpl extends ServiceImpl<UserFavoriteMapper, UserFavorite>
        implements UserFavoriteService {

    @Override
    public List<UserFavorite> findAllByUserId(Long userId) {
        QueryWrapper<UserFavorite> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        wrapper.orderByDesc("create_time");
        return baseMapper.selectList(wrapper);
    }

    @Override
    public boolean isFavorite(Long userId, String hoscode) {
        QueryWrapper<UserFavorite> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        wrapper.eq("hoscode", hoscode);
        return baseMapper.selectCount(wrapper) > 0;
    }

    @Override
    public void addFavorite(Long userId, String hoscode, String hosname) {
        if (isFavorite(userId, hoscode)) {
            return;
        }
        UserFavorite favorite = new UserFavorite();
        favorite.setUserId(userId);
        favorite.setHoscode(hoscode);
        favorite.setHosname(hosname);
        baseMapper.insert(favorite);
    }

    @Override
    public void cancelFavorite(Long userId, String hoscode) {
        QueryWrapper<UserFavorite> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        wrapper.eq("hoscode", hoscode);
        baseMapper.delete(wrapper);
    }
}