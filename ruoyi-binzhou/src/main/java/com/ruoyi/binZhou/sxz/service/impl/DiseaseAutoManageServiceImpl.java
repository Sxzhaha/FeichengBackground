package com.ruoyi.binZhou.sxz.service.impl;

import java.util.List;
import org.springframework.stereotype.Service;
import com.ruoyi.binZhou.sxz.domain.DiseaseAutoItemQuery;
import com.ruoyi.binZhou.sxz.domain.DiseaseAutoManageItemRow;
import com.ruoyi.binZhou.sxz.domain.DiseaseAutoManageRunRow;
import com.ruoyi.binZhou.sxz.domain.DiseaseAutoManageSummary;
import com.ruoyi.binZhou.sxz.domain.DiseaseAutoManageTaskRow;
import com.ruoyi.binZhou.sxz.domain.DiseaseAutoRunQuery;
import com.ruoyi.binZhou.sxz.domain.DiseaseAutoTaskQuery;
import com.ruoyi.binZhou.sxz.mapper.DiseaseAutoManageMapper;
import com.ruoyi.binZhou.sxz.service.IDiseaseAutoManageService;

@Service
public class DiseaseAutoManageServiceImpl implements IDiseaseAutoManageService
{
    private final DiseaseAutoManageMapper mapper;

    public DiseaseAutoManageServiceImpl(DiseaseAutoManageMapper mapper)
    {
        this.mapper = mapper;
    }

    @Override
    public DiseaseAutoManageSummary selectSummary()
    {
        return mapper.selectSummary();
    }

    @Override
    public List<DiseaseAutoManageTaskRow> selectTaskRows(DiseaseAutoTaskQuery query)
    {
        return mapper.selectTaskRows(query == null ? new DiseaseAutoTaskQuery() : query);
    }

    @Override
    public List<DiseaseAutoManageRunRow> selectRunRows(DiseaseAutoRunQuery query)
    {
        return mapper.selectRunRows(query == null ? new DiseaseAutoRunQuery() : query);
    }

    @Override
    public List<DiseaseAutoManageItemRow> selectItemRows(DiseaseAutoItemQuery query)
    {
        return mapper.selectItemRows(query == null ? new DiseaseAutoItemQuery() : query);
    }
}
