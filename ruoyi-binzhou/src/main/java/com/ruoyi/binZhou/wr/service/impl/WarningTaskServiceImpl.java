package com.ruoyi.binZhou.wr.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.binZhou.wr.mapper.WarningTaskMapper;
import com.ruoyi.binZhou.wr.domain.WarningTask;
import com.ruoyi.binZhou.wr.service.IWarningTaskService;

/**
 * 预警任务Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-06-26
 */
@Service
public class WarningTaskServiceImpl implements IWarningTaskService 
{
    @Autowired
    private WarningTaskMapper warningTaskMapper;

    /**
     * 查询预警任务
     * 
     * @param id 预警任务主键
     * @return 预警任务
     */
    @Override
    public WarningTask selectWarningTaskById(Long id)
    {
        return warningTaskMapper.selectWarningTaskById(id);
    }

    /**
     * 查询预警任务列表
     * 
     * @param warningTask 预警任务
     * @return 预警任务
     */
    @Override
    public List<WarningTask> selectWarningTaskList(WarningTask warningTask)
    {
        return warningTaskMapper.selectWarningTaskList(warningTask);
    }

    /**
     * 新增预警任务
     * 
     * @param warningTask 预警任务
     * @return 结果
     */
    @Override
    public int insertWarningTask(WarningTask warningTask)
    {
        warningTask.setCreateTime(DateUtils.getNowDate());
        return warningTaskMapper.insertWarningTask(warningTask);
    }

    /**
     * 修改预警任务
     * 
     * @param warningTask 预警任务
     * @return 结果
     */
    @Override
    public int updateWarningTask(WarningTask warningTask)
    {
        warningTask.setUpdateTime(DateUtils.getNowDate());
        return warningTaskMapper.updateWarningTask(warningTask);
    }

    /**
     * 批量删除预警任务
     * 
     * @param ids 需要删除的预警任务主键
     * @return 结果
     */
    @Override
    public int deleteWarningTaskByIds(Long[] ids)
    {
        return warningTaskMapper.deleteWarningTaskByIds(ids);
    }

    /**
     * 删除预警任务信息
     * 
     * @param id 预警任务主键
     * @return 结果
     */
    @Override
    public int deleteWarningTaskById(Long id)
    {
        return warningTaskMapper.deleteWarningTaskById(id);
    }
}
