package com.ruoyi.binZhou.sxz.service.impl;

import java.util.List;
import org.springframework.stereotype.Service;
import com.ruoyi.binZhou.sxz.domain.DiseaseImageQuery;
import com.ruoyi.binZhou.sxz.domain.DiseaseImageVO;
import com.ruoyi.binZhou.sxz.mapper.DiseaseImageMapper;
import com.ruoyi.binZhou.sxz.service.IDiseaseImageService;

@Service
public class DiseaseImageServiceImpl implements IDiseaseImageService
{
    private final DiseaseImageMapper mapper;

    public DiseaseImageServiceImpl(DiseaseImageMapper mapper)
    {
        this.mapper = mapper;
    }

    @Override
    public List<DiseaseImageVO> selectDiseaseImageList(DiseaseImageQuery query)
    {
        return mapper.selectDiseaseImageList(query);
    }
}
