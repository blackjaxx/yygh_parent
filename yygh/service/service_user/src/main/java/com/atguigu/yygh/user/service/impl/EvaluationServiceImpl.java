package com.atguigu.yygh.user.service.impl;

import com.atguigu.yygh.model.user.Evaluation;
import com.atguigu.yygh.user.mapper.EvaluationMapper;
import com.atguigu.yygh.user.service.EvaluationService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class EvaluationServiceImpl extends ServiceImpl<EvaluationMapper, Evaluation>
        implements EvaluationService {

    @Override
    @Transactional
    public void submitEvaluation(Long userId, Evaluation evaluation) {
        evaluation.setUserId(userId);
        String rawName = evaluation.getUserName();
        if (rawName != null && rawName.length() > 1) {
            evaluation.setUserName(rawName.charAt(0) + "**");
        }
        baseMapper.insert(evaluation);
    }

    @Override
    public List<Evaluation> findByHoscode(String hoscode, int page, int limit) {
        QueryWrapper<Evaluation> wrapper = new QueryWrapper<>();
        wrapper.eq("hoscode", hoscode);
        wrapper.orderByDesc("create_time");
        Page<Evaluation> pageParam = new Page<>(page, limit);
        Page<Evaluation> result = baseMapper.selectPage(pageParam, wrapper);
        return result.getRecords();
    }

    @Override
    public Map<String, Object> getHospitalRating(String hoscode) {
        QueryWrapper<Evaluation> wrapper = new QueryWrapper<>();
        wrapper.eq("hoscode", hoscode);
        Long count = baseMapper.selectCount(wrapper);
        Map<String, Object> result = new HashMap<>();
        result.put("total", count);
        if (count > 0) {
            List<Evaluation> list = baseMapper.selectList(wrapper);
            double avg = list.stream().mapToInt(Evaluation::getRating).average().orElse(0);
            result.put("average", Math.round(avg * 10.0) / 10.0);
        } else {
            result.put("average", 0.0);
        }
        return result;
    }

    @Override
    public boolean hasEvaluated(Long userId, Long orderId) {
        QueryWrapper<Evaluation> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        wrapper.eq("order_id", orderId);
        return baseMapper.selectCount(wrapper) > 0;
    }
}