package com.ruoyi.binZhou.sxz.mapper;

import java.util.List;
import com.ruoyi.binZhou.sxz.domain.DiseaseAutoItemQuery;
import com.ruoyi.binZhou.sxz.domain.DiseaseAutoManageItemRow;
import com.ruoyi.binZhou.sxz.domain.DiseaseAutoManageRunRow;
import com.ruoyi.binZhou.sxz.domain.DiseaseAutoManageSummary;
import com.ruoyi.binZhou.sxz.domain.DiseaseAutoManageTaskRow;
import com.ruoyi.binZhou.sxz.domain.DiseaseAutoRunQuery;
import com.ruoyi.binZhou.sxz.domain.DiseaseAutoTaskQuery;

public interface DiseaseAutoManageMapper
{
    DiseaseAutoManageSummary selectSummary();

    List<DiseaseAutoManageTaskRow> selectTaskRows(DiseaseAutoTaskQuery query);

    List<DiseaseAutoManageRunRow> selectRunRows(DiseaseAutoRunQuery query);

    List<DiseaseAutoManageItemRow> selectItemRows(DiseaseAutoItemQuery query);
}
