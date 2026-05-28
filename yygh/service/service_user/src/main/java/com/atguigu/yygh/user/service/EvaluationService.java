package com.atguigu.yygh.user.service;

import com.atguigu.yygh.model.user.Evaluation;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

public interface EvaluationService extends IService<Evaluation> {

    void submitEvaluation(Long userId, Evaluation evaluation);

    List<Evaluation> findByHoscode(String hoscode, int page, int limit);

    Map<String, Object> getHospitalRating(String hoscode);

    boolean hasEvaluated(Long userId, Long orderId);
}