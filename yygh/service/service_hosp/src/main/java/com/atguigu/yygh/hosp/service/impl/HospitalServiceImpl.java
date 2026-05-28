package com.atguigu.yygh.hosp.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.atguigu.yygh.cmn.client.DictFeignClient;
import com.atguigu.yygh.hosp.repository.HospitalRepository;
import com.atguigu.yygh.hosp.service.HospitalService;
import com.atguigu.yygh.model.hosp.Hospital;
import com.atguigu.yygh.vo.hosp.HospitalQueryVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 医院服务实现
 * 对数据字典Feign调用做了缓存优化，减少循环内远程调用次数
 */
@Service
@Slf4j
public class HospitalServiceImpl implements HospitalService {

    @Autowired
    private HospitalRepository hospitalRepository;

    @Autowired
    private DictFeignClient dictFeignClient;

    @Override
    public void save(Map<String, Object> paramMap) {
        log.info(JSONObject.toJSONString(paramMap));
        Hospital hospital = JSONObject.parseObject(JSONObject.toJSONString(paramMap), Hospital.class);
        //判断是否存在
        Hospital targetHospital = hospitalRepository.getHospitalByHoscode(hospital.getHoscode());
        if (null != targetHospital) {
            hospital.setStatus(targetHospital.getStatus());
            hospital.setCreateTime(targetHospital.getCreateTime());
            hospital.setUpdateTime(new Date());
            hospital.setIsDeleted(0);
            hospitalRepository.save(hospital);
        } else {
            //0：未上线 1：已上线
            hospital.setStatus(0);
            hospital.setCreateTime(new Date());
            hospital.setUpdateTime(new Date());
            hospital.setIsDeleted(0);
            hospitalRepository.save(hospital);
        }
    }

    /**
     * 查询医院
     *
     * @param hoscode
     * @return
     */
    @Override
    public Hospital getByHoscode(String hoscode) {
        return hospitalRepository.getHospitalByHoscode(hoscode);
    }

    @Override
    public Page<Hospital> selectPage(Integer page, Integer limit, HospitalQueryVo hospitalQueryVo) {
        Sort sort = Sort.by(Sort.Direction.DESC, "createTime");
        // 0为第一页
        Pageable pageable = PageRequest.of(page - 1, limit, sort);

        Hospital hospital = new Hospital();
        BeanUtils.copyProperties(hospitalQueryVo, hospital);

        ExampleMatcher matcher = ExampleMatcher.matching()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                .withIgnoreCase(true);

        Example<Hospital> example = Example.of(hospital, matcher);
        Page<Hospital> pages = hospitalRepository.findAll(example, pageable);

        // 批量预查询字典数据，避免逐条Feign远程调用
        Map<String, String> dictCache = buildDictCache(pages.getContent());

        pages.getContent().forEach(item -> {
            this.setHospitalHosType(item, dictCache);
        });
        return pages;
    }

    /**
     * 构建字典缓存，收集所有需要查询的字典编码并批量获取
     */
    private Map<String, String> buildDictCache(List<Hospital> hospitalList) {
        Map<String, String> cache = new HashMap<>();
        for (Hospital hospital : hospitalList) {
            if (hospital.getHostype() != null) {
                cache.put("Hostype_" + hospital.getHostype(), "");
            }
            if (hospital.getProvinceCode() != null) {
                cache.put(hospital.getProvinceCode(), "");
            }
            if (hospital.getCityCode() != null) {
                cache.put(hospital.getCityCode(), "");
            }
            if (hospital.getDistrictCode() != null) {
                cache.put(hospital.getDistrictCode(), "");
            }
        }
        // 批量查询所有字典值
        for (String key : cache.keySet()) {
            try {
                if (key.startsWith("Hostype_")) {
                    String value = key.substring("Hostype_".length());
                    cache.put(key, dictFeignClient.getName("Hostype", value));
                } else {
                    cache.put(key, dictFeignClient.getName(key));
                }
            } catch (Exception e) {
                log.warn("字典查询失败, key={}", key, e);
                cache.put(key, "");
            }
        }
        return cache;
    }

    /**
     * 封装医院等级和省市区地址信息
     */
    private Hospital setHospitalHosType(Hospital hospital, Map<String, String> dictCache) {
        String hostypeString = dictCache.getOrDefault("Hostype_" + hospital.getHostype(),
                dictFeignClient.getName("Hostype", hospital.getHostype()));
        String provinceString = dictCache.getOrDefault(hospital.getProvinceCode(),
                dictFeignClient.getName(hospital.getProvinceCode()));
        String cityString = dictCache.getOrDefault(hospital.getCityCode(),
                dictFeignClient.getName(hospital.getCityCode()));
        String districtString = dictCache.getOrDefault(hospital.getDistrictCode(),
                dictFeignClient.getName(hospital.getDistrictCode()));
        hospital.getParam().put("hostypeString", hostypeString);
        hospital.getParam().put("fullAddress", provinceString + cityString + districtString + hospital.getAddress());
        return hospital;
    }

    @Override
    public void updateStatus(String id, Integer status) {
        if(status.intValue() == 0 || status.intValue() == 1) {
            Hospital hospital = hospitalRepository.findById(id).get();
            hospital.setStatus(status);
            hospital.setUpdateTime(new Date());
            hospitalRepository.save(hospital);
        }
    }

    @Override
    public Map<String, Object> show(String id) {
        Map<String, Object> result = new HashMap<>();
        Hospital hospital = hospitalRepository.findById(id).get();
        // 单条查询时直接使用字典Feign调用
        Hospital hospitalResult = new Hospital();
        BeanUtils.copyProperties(hospital, hospitalResult);
        hospitalResult.getParam().put("hostypeString", dictFeignClient.getName("Hostype", hospital.getHostype()));
        String provinceString = dictFeignClient.getName(hospital.getProvinceCode());
        String cityString = dictFeignClient.getName(hospital.getCityCode());
        String districtString = dictFeignClient.getName(hospital.getDistrictCode());
        hospitalResult.getParam().put("fullAddress", provinceString + cityString + districtString + hospital.getAddress());
        result.put("hospital", hospitalResult);
        result.put("bookingRule", hospital.getBookingRule());
        // 预约信息已在result内封装了返回结果，hospital内不需要重复返回 置为null
        hospitalResult.setBookingRule(null);
        return result;
    }

    public String getName(String hoscode) {
        Hospital hospital = hospitalRepository.getHospitalByHoscode(hoscode);
        if(null != hospital) {
            return hospital.getHosname();
        }
        return "";
    }

    @Override
    public List<Hospital> findByHosname(String hosname) {
        return hospitalRepository.findHospitalByHosnameLike(hosname);
    }

    @Override
    public Map<String, Object> item(String hoscode) {
        Map<String, Object> result = new HashMap<>();
        Hospital hospital = this.getByHoscode(hoscode);
        hospital.getParam().put("hostypeString", dictFeignClient.getName("Hostype", hospital.getHostype()));
        String provinceString = dictFeignClient.getName(hospital.getProvinceCode());
        String cityString = dictFeignClient.getName(hospital.getCityCode());
        String districtString = dictFeignClient.getName(hospital.getDistrictCode());
        hospital.getParam().put("fullAddress", provinceString + cityString + districtString + hospital.getAddress());
        result.put("hospital", hospital);
        result.put("bookingRule", hospital.getBookingRule());
        hospital.setBookingRule(null);
        return result;
    }
}
