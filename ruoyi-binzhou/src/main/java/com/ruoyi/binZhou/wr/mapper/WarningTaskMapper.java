package com.ruoyi.binZhou.wr.mapper;

import java.util.List;
import com.ruoyi.binZhou.wr.domain.WarningTask;

/**
 * 预警任务Mapper接口
 * 
 * @author ruoyi
 * @date 2026-06-26
 */
public interface WarningTaskMapper 
{
    /**
     * 查询预警任务
     * 
     * @param id 预警任务主键
     * @return 预警任务
     */
    public WarningTask selectWarningTaskById(Long id);

    /**
     * 查询预警任务列表
     * 
     * @param warningTask 预警任务
     * @return 预警任务集合
     */
    public List<WarningTask> selectWarningTaskList(WarningTask warningTask);

    /**
     * 新增预警任务
     * 
     * @param warningTask 预警任务
     * @return 结果
     */
    public int insertWarningTask(WarningTask warningTask);

    /**
     * 修改预警任务
     * 
     * @param warningTask 预警任务
     * @return 结果
     */
    public int updateWarningTask(WarningTask warningTask);

    /**
     * 删除预警任务
     * 
     * @param id 预警任务主键
     * @return 结果
     */
    public int deleteWarningTaskById(Long id);

    /**
     * 批量删除预警任务
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteWarningTaskByIds(Long[] ids);
}
