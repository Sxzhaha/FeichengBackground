package com.ruoyi.binZhou.xwq.domain;

import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 农业病虫害历史诊断记录实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PestDiagnosisHistory extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Long id;

    /** 用户ID */
    private Long userId;

    /** 图片存储URL */
    private String imageUrl;

    /** 源文件路径 */
    @Excel(name = "源文件路径")
    private String filePath;

    /** 诊断结果 */
    @Excel(name = "诊断结果")
    private String diseaseName;

    /** 模型置信度 (0-100) */
    @Excel(name = "置信度(%)")
    private BigDecimal confidence;

    /** 综合防治建议 */
    @Excel(name = "防治建议")
    private String prevention;

    /** 说明 */
    @Excel(name = "说明")
    private String briefDesc;

    /** 删除标志 */
    private String delFlag;
}
