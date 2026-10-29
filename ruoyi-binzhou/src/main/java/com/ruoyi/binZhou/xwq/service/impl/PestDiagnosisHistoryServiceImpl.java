package com.ruoyi.binZhou.xwq.service.impl;

import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.binZhou.xwq.domain.PestDiagnosisHistory;
import com.ruoyi.binZhou.xwq.mapper.PestDiagnosisHistoryMapper;
import com.ruoyi.binZhou.xwq.service.IPestDiagnosisHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 农业病虫害历史诊断记录 Service 实现
 */
@Service
public class PestDiagnosisHistoryServiceImpl implements IPestDiagnosisHistoryService {

    @Autowired
    private PestDiagnosisHistoryMapper mapper;

    @Override
    public List<PestDiagnosisHistory> selectList(PestDiagnosisHistory query) {
        if (query.getUserId() == null) {
            try {
                query.setUserId(SecurityUtils.getUserId());
            } catch (Exception e) {
            }
        }
        return mapper.selectList(query);
    }

    @Override
    public PestDiagnosisHistory selectById(Long id) {
        return mapper.selectById(id);
    }

    @Override
    public int insert(PestDiagnosisHistory history) {
        try {
            history.setUserId(SecurityUtils.getUserId());
            history.setCreateBy(SecurityUtils.getUsername());
        } catch (Exception e) {
        }
        return mapper.insert(history);
    }

    @Override
    public int batchInsert(List<PestDiagnosisHistory> list) {
        if (list == null || list.isEmpty()) {
            return 0;
        }
        Long userId = null;
        String username = null;
        try {
            userId = SecurityUtils.getUserId();
            username = SecurityUtils.getUsername();
        } catch (Exception e) {
        }
        final Long finalUserId = userId;
        final String finalUsername = username;
        list.forEach(h -> {
            h.setUserId(finalUserId);
            h.setCreateBy(finalUsername);
        });
        return mapper.batchInsert(list);
    }

    @Override
    public int update(PestDiagnosisHistory history) {
        try {
            history.setUpdateBy(SecurityUtils.getUsername());
        } catch (Exception e) {
        }
        return mapper.update(history);
    }

    @Override
    public int deleteByIds(Long[] ids) {
        return mapper.deleteByIds(ids);
    }

    @Override
    public Map<String, Object> getStatistics(PestDiagnosisHistory query) {
        if (query.getUserId() == null) {
            try {
                query.setUserId(SecurityUtils.getUserId());
            } catch (Exception e) {
            }
        }
        List<PestDiagnosisHistory> all = mapper.selectList(query);
        int total = all.size();
        double avgConf = all.stream()
                .filter(h -> h.getConfidence() != null)
                .mapToDouble(h -> h.getConfidence().doubleValue())
                .average()
                .orElse(0.0);

        Map<String, Object> result = new HashMap<>();
        result.put("total", total);
        result.put("avgConfidence",
                BigDecimal.valueOf(avgConf).setScale(1, RoundingMode.HALF_UP));
        return result;
    }
}
