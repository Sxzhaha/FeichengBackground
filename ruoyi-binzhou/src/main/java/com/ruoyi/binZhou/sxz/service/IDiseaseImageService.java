package com.ruoyi.binZhou.sxz.service;

import java.util.List;
import com.ruoyi.binZhou.sxz.domain.DiseaseImageQuery;
import com.ruoyi.binZhou.sxz.domain.DiseaseImageVO;

public interface IDiseaseImageService
{
    List<DiseaseImageVO> selectDiseaseImageList(DiseaseImageQuery query);
}
