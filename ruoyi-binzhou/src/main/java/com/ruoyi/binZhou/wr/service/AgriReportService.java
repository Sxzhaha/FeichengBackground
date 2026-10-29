package com.ruoyi.binZhou.wr.service;

import com.ruoyi.binZhou.wr.domain.vo.AgriReportDTO;

public interface AgriReportService {
    AgriReportDTO buildAgriReport(Long parkId, String beginTime, String endTime);
}
