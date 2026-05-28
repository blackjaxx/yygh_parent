package com.atguigu.yygh.user.controller;

import com.atguigu.yygh.common.result.Result;
import com.atguigu.yygh.common.util.AuthContextHolder;
import com.atguigu.yygh.model.user.Evaluation;
import com.atguigu.yygh.user.service.EvaluationService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user/evaluation")
public class EvaluationApiController {

    @Autowired
    private EvaluationService evaluationService;

    @ApiOperation("提交评价")
    @PostMapping("auth/submit")
    public Result submit(@RequestBody Evaluation evaluation, HttpServletRequest request) {
        Long userId = AuthContextHolder.getUserId(request);
        evaluationService.submitEvaluation(userId, evaluation);
        return Result.ok();
    }

    @ApiOperation("获取医院评价列表")
    @GetMapping("list/{hoscode}/{page}/{limit}")
    public Result list(@PathVariable String hoscode,
                       @PathVariable int page,
                       @PathVariable int limit) {
        List<Evaluation> list = evaluationService.findByHoscode(hoscode, page, limit);
        return Result.ok(list);
    }

    @ApiOperation("获取医院评分统计")
    @GetMapping("rating/{hoscode}")
    public Result rating(@PathVariable String hoscode) {
        Map<String, Object> rating = evaluationService.getHospitalRating(hoscode);
        return Result.ok(rating);
    }

    @ApiOperation("检查订单是否已评价")
    @GetMapping("auth/check/{orderId}")
    public Result check(@PathVariable Long orderId, HttpServletRequest request) {
        Long userId = AuthContextHolder.getUserId(request);
        boolean hasEvaluated = evaluationService.hasEvaluated(userId, orderId);
        return Result.ok(hasEvaluated);
    }
}