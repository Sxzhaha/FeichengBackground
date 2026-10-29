package com.ruoyi.binZhou.smx.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import com.ruoyi.binZhou.smx.domain.ExpertCrop;

/**
 * 专家问答-作物圈子Mapper接口
 *
 * @author smx
 */
@Mapper
public interface ExpertCropMapper
{
    /**
     * 按作物名称查询分类
     */
    ExpertCrop selectByCrop(String crop);

    /**
     * 登记新作物，重复作物不报错
     */
    int insertIfAbsent(ExpertCrop crop);

    /**
     * 查询作物圈子列表（含问答总数）
     */
    List<ExpertCrop> selectCropList();
}
