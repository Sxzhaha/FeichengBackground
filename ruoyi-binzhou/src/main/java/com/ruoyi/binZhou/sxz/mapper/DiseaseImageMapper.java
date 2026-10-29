package com.ruoyi.binZhou.sxz.mapper;

import java.util.List;
import com.ruoyi.binZhou.sxz.domain.DiseaseImageQuery;
import com.ruoyi.binZhou.sxz.domain.DiseaseImageVO;

public interface DiseaseImageMapper
{
    List<DiseaseImageVO> selectDiseaseImageList(DiseaseImageQuery query);
}
