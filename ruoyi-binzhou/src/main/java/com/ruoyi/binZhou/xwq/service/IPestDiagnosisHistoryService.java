package com.ruoyi.binZhou.xwq.service;

import com.ruoyi.binZhou.xwq.domain.PestDiagnosisHistory;

import java.util.List;
import java.util.Map;

/**
 * 农业病虫害历史诊断记录 Service 接口
 */
public interface IPestDiagnosisHistoryService {

    List<PestDiagnosisHistory> selectList(PestDiagnosisHistory query);

    PestDiagnosisHistory selectById(Long id);

    int insert(PestDiagnosisHistory history);

    int batchInsert(List<PestDiagnosisHistory> list);

    int update(PestDiagnosisHistory history);

    int deleteByIds(Long[] ids);

    /** 获取统计数据（总数、平均置信度） */
    Map<String, Object> getStatistics(PestDiagnosisHistory query);
}
