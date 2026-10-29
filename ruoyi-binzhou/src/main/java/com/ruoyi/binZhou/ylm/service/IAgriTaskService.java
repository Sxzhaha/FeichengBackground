package com.ruoyi.binZhou.ylm.service;

import java.util.List;
import com.ruoyi.binZhou.ylm.domain.AgriTask;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.common.core.domain.AjaxResult;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;
/**
 * 农事任务Service接口
 *
 * @author ruoyi
 * @date 2026-06-28
 */
public interface IAgriTaskService extends IService<AgriTask>
{
    /**
     * 查询农事任务详情
     *
     * @param id 农事任务主键
     * @return 农事任务
     */
    public AgriTask selectAgriTaskById(Long id);

    /**
     * 农户端：查询当前登录操作人的农事任务详情
     */
    AgriTask selectFarmerAgriTaskById(Long id, Long operatorId);

    /**
     * 管理员接口：查询全量农事列表+统计
     *
     * @param agriTask 农事任务
     * @return 分页+统计封装
     */
    public Map<String, Object> selectAgriTaskList(AgriTask agriTask);

    /**
     * 查询农事任务统计，不受分页影响
     */
    Map<String, Object> selectAgriTaskStats(AgriTask agriTask);

    // ========= 新增农户独立接口 =========
    /**
     * 农户接口：仅查询当前登录人自己的农事+统计
     * @param agriTask 查询条件
     * @param operatorId 当前登录农户ID
     * @return 农户个人列表+统计
     */
    Map<String, Object> selectFarmerAgriTaskList(AgriTask agriTask, Long operatorId);

    /**
     * 农户接口：查询当前登录人自己的农事统计，不受分页影响
     */
    Map<String, Object> selectFarmerAgriTaskStats(AgriTask agriTask, Long operatorId);

    /**
     * 查询园区-地块-角落三级区域树
     *
     * @return 区域层级数据
     */
    List<Map<String, Object>> selectRegionTree();

    /**
     * 生成下一个农事任务编号。
     *
     * @return 任务编号
     */
    String generateTaskNo();

    /**
     * 校验任务编号是否唯一。
     *
     * @param taskNo 任务编号
     * @param excludeId 排除的记录ID
     * @return true 唯一，false 不唯一
     */
    boolean checkTaskNoUnique(String taskNo, Long excludeId);

    /**
     * 新增农事任务
     *
     * @param agriTask 农事任务
     * @return 结果
     */
    public int insertAgriTask(AgriTask agriTask);

    /**
     * 修改农事任务
     *
     * @param agriTask 农事任务
     * @return 结果
     */
    public int updateAgriTask(AgriTask agriTask);

    /**
     * 下载农事记录导入模板。
     */
    void downloadFarmerImportTemplate(HttpServletResponse response);

    /**
     * 导入农事记录，图片和创建信息由系统处理。
     *
     * @param file Excel文件
     * @param loginUserId 当前登录用户ID
     * @return 导入结果
     */
    AjaxResult importFarmerAgriTask(MultipartFile file, Long loginUserId);

    /**
     * 农户提交农事作业（仅更新照片、操作时间、备注）
     */
    int submitAgriTask(AgriTask agriTask);

    /**
     * 批量删除农事任务
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteAgriTaskByIds(Long[] ids);
}
