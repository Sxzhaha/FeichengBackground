package com.ruoyi.binZhou.xwq.mapper;

import com.ruoyi.binZhou.xwq.domain.PestDiagnosisHistory;

import java.util.List;

/**
 * 农业病虫害历史诊断记录 Mapper 接口
 */
public interface PestDiagnosisHistoryMapper {

    /** 查询列表 */
    List<PestDiagnosisHistory> selectList(PestDiagnosisHistory query);

    /** 根据ID查询 */
    PestDiagnosisHistory selectById(Long id);

    /** 新增 */
    int insert(PestDiagnosisHistory history);

    /** 批量新增 */
    int batchInsert(List<PestDiagnosisHistory> list);

    /** 更新 */
    int update(PestDiagnosisHistory history);

    /** 逻辑删除（单条） */
    int deleteById(Long id);

    /** 逻辑删除（批量） */
    int deleteByIds(Long[] ids);

    /** 查询平均置信度 */
    Double selectAvgConfidence();
}
