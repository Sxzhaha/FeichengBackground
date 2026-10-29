package com.ruoyi.binZhou.sxz.controller;

import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.binZhou.sxz.domain.DiseaseImageQuery;
import com.ruoyi.binZhou.sxz.domain.DiseaseImageVO;
import com.ruoyi.binZhou.sxz.service.IDiseaseImageService;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.page.TableDataInfo;

@RestController
@RequestMapping("/sxz/disease-images")
public class DiseaseImageController extends BaseController
{
    private final IDiseaseImageService service;

    public DiseaseImageController(IDiseaseImageService service)
    {
        this.service = service;
    }

    @GetMapping("/list")
    public TableDataInfo list(DiseaseImageQuery query)
    {
        startPage();
        List<DiseaseImageVO> list = service.selectDiseaseImageList(query);
        return getDataTable(list);
    }
}
