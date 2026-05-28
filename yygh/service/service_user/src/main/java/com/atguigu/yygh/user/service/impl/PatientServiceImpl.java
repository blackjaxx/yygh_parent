package com.atguigu.yygh.user.service.impl;

import com.atguigu.yygh.cmn.client.DictFeignClient;
import com.atguigu.yygh.enums.DictEnum;
import com.atguigu.yygh.model.user.Patient;
import com.atguigu.yygh.user.mapper.PatientMapper;
import com.atguigu.yygh.user.service.PatientService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 就诊人服务实现
 * 对数据字典Feign调用做了缓存优化，减少N+1远程查询
 */
@Slf4j
@Service
public class PatientServiceImpl extends
        ServiceImpl<PatientMapper, Patient> implements PatientService {

    @Autowired
    private DictFeignClient dictFeignClient;

    @Override
    public List<Patient> findAllUserId(Long userId) {
        QueryWrapper<Patient> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        List<Patient> patientList = baseMapper.selectList(wrapper);

        // 批量预处理：预先查询所有需要用到的字典值缓存，避免循环内逐条远程调用
        Map<String, String> dictCache = buildDictCache(patientList);

        patientList.forEach(item -> {
            this.packPatient(item, dictCache);
        });
        return patientList;
    }

    @Override
    public Patient getPatientId(Long id) {
        Patient patient = baseMapper.selectById(id);
        if (patient != null) {
            // 单条查询时仍使用原逻辑，实际生产可扩展为从缓存读取
            return this.packPatient(patient, null);
        }
        return null;
    }

    /**
     * 构建字典数据缓存，将患者列表中涉及的所有字典查询批量预取
     * 避免每条就诊人循环调用3~5次 Feign 远程接口
     */
    private Map<String, String> buildDictCache(List<Patient> patientList) {
        Map<String, String> cache = new HashMap<>();
        for (Patient patient : patientList) {
            // 收集所有需要查询的字典值
            collectDictValue(cache, patient.getCertificatesType());
            collectDictValue(cache, patient.getContactsCertificatesType());
            collectDictValue(cache, patient.getProvinceCode());
            collectDictValue(cache, patient.getCityCode());
            collectDictValue(cache, patient.getDistrictCode());
        }
        // 批量查询省市区等行政区划（使用父级编码查询）
        for (String code : cache.keySet()) {
            if (!code.startsWith("CACHED_")) {
                try {
                    String name = dictFeignClient.getName(code);
                    cache.put("CACHED_" + code, name);
                } catch (Exception e) {
                    log.warn("字典查询失败, code={}", code, e);
                    cache.put("CACHED_" + code, "");
                }
            }
        }
        return cache;
    }

    private void collectDictValue(Map<String, String> cache, String code) {
        if (code != null && !code.isEmpty() && !cache.containsKey(code)) {
            cache.put(code, "");
        }
    }

    /**
     * 封装就诊人其他参数
     */
    private Patient packPatient(Patient patient, Map<String, String> dictCache) {
        // 证件类型名称
        String certificatesTypeString = getDictName(dictCache, DictEnum.CERTIFICATES_TYPE.getDictCode(),
                patient.getCertificatesType());
        // 联系人证件类型名称
        String contactsCertificatesTypeString = getDictName(dictCache, DictEnum.CERTIFICATES_TYPE.getDictCode(),
                patient.getContactsCertificatesType());
        // 省
        String provinceString = getDictName(dictCache, null, patient.getProvinceCode());
        // 市
        String cityString = getDictName(dictCache, null, patient.getCityCode());
        // 区
        String districtString = getDictName(dictCache, null, patient.getDistrictCode());

        patient.getParam().put("certificatesTypeString", certificatesTypeString);
        patient.getParam().put("contactsCertificatesTypeString", contactsCertificatesTypeString);
        patient.getParam().put("provinceString", provinceString);
        patient.getParam().put("cityString", cityString);
        patient.getParam().put("districtString", districtString);
        patient.getParam().put("fullAddress", provinceString + cityString + districtString + patient.getAddress());
        return patient;
    }

    /**
     * 从缓存获取字典值，缓存未命中时走远程调用
     */
    private String getDictName(Map<String, String> cache, String dictCode, String valueCode) {
        if (valueCode == null || valueCode.isEmpty()) return "";
        // 优先从缓存获取
        if (cache != null) {
            String cached = cache.get("CACHED_" + valueCode);
            if (cached != null) return cached;
        }
        // 缓存未命中则走远程调用
        try {
            if (dictCode != null) {
                return dictFeignClient.getName(dictCode, valueCode);
            } else {
                return dictFeignClient.getName(valueCode);
            }
        } catch (Exception e) {
            log.warn("字典查询失败, dictCode={}, valueCode={}", dictCode, valueCode, e);
            return "";
        }
    }
}
