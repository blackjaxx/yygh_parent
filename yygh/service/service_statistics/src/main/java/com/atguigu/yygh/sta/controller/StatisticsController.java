package com.atguigu.yygh.sta.controller;

import com.atguigu.yygh.common.result.Result;
import com.atguigu.yygh.order.client.OrderFeignClient;
import com.atguigu.yygh.vo.order.OrderCountQueryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * 统计管理接口 - 提供多维度数据统计
 */
@Api(tags = "统计管理接口")
@RestController
@RequestMapping("/admin/statistics")
public class StatisticsController {

    @Autowired
    private OrderFeignClient orderFeignClient;

    @ApiOperation(value = "获取订单统计数据（按日期）")
    @GetMapping("getCountMap")
    public Result getCountMap(OrderCountQueryVo orderCountQueryVo) {
        return Result.ok(orderFeignClient.getCountMap(orderCountQueryVo));
    }

    @ApiOperation(value = "获取平台概览数据")
    @GetMapping("getOverview")
    public Result getOverview() {
        Map<String, Object> map = new HashMap<>();
        map.put("hospitalCount", 56);
        map.put("orderToday", 128);
        map.put("orderWeek", 896);
        map.put("orderMonth", 3652);
        map.put("userTotal", 15860);
        map.put("patientTotal", 23450);
        return Result.ok(map);
    }

    @ApiOperation(value = "获取科室预约排行（模拟数据）")
    @GetMapping("getDeptRanking")
    public Result getDeptRanking() {
        return Result.ok(Arrays.asList(
                deptRank("儿科", 328),
                deptRank("骨科", 256),
                deptRank("心内科", 210),
                deptRank("消化内科", 185),
                deptRank("皮肤科", 152),
                deptRank("中医科", 128),
                deptRank("眼科", 96),
                deptRank("妇科", 72)
        ));
    }

    @ApiOperation(value = "获取医院预约排行（模拟数据）")
    @GetMapping("getHospRanking")
    public Result getHospRanking() {
        return Result.ok(Arrays.asList(
                hospRank("北京协和医院", 456),
                hospRank("上海瑞金医院", 389),
                hospRank("广州中山一院", 312),
                hospRank("深圳人民医院", 245),
                hospRank("杭州邵逸夫医院", 178)
        ));
    }

    private Map<String, Object> deptRank(String name, Integer count) {
        Map<String, Object> m = new HashMap<>();
        m.put("name", name);
        m.put("count", count);
        return m;
    }

    private Map<String, Object> hospRank(String name, Integer count) {
        Map<String, Object> m = new HashMap<>();
        m.put("name", name);
        m.put("count", count);
        return m;
    }
}
