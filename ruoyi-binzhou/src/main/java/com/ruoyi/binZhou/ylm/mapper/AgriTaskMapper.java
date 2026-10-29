package com.ruoyi.binZhou.ylm.mapper;

import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.binZhou.ylm.domain.AgriTask;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 农事任务Mapper接口
 *
 * @author ruoyi
 * @date 2026-06-28
 */
@Mapper
public interface AgriTaskMapper extends BaseMapper<AgriTask>
{
    /**
     * 查询农事任务
     *
     * @param id 农事任务主键
     * @return 农事任务
     */
    public AgriTask selectAgriTaskById(Long id);

    /**
     * 农户端：查询当前登录操作人的单条农事任务
     */
    AgriTask selectFarmerAgriTaskById(@Param("id") Long id, @Param("operatorId") Long operatorId);

    /**
     * 查询农事任务列表（管理员-全量无过滤）
     *
     * @param agriTask 农事任务
     * @return 农事任务集合
     */
    public List<AgriTask> selectAgriTaskList(AgriTask agriTask);

    // ========== 新增农户专属查询方法 ==========
    /**
     * 农户端：仅查询当前登录操作人自己的农事数据
     * @param query 查询条件实体
     * @param operatorId 当前登录农户ID
     * @return 农户个人农事列表
     */
    List<AgriTask> selectFarmerAgriTaskList(@Param("query") AgriTask query, @Param("operatorId") Long operatorId);

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
     * 农户提交本人农事记录
     *
     * @param agriTask 农事任务
     * @param createById 当前登录用户ID
     * @return 结果
     */
    public int submitAgriTask(@Param("agriTask") AgriTask agriTask, @Param("createById") String createById);

    /**
     * 根据角落/位置ID查询所属地块ID
     *
     * @param locationId 角落/位置ID
     * @return 地块ID
     */
    Long selectAreaIdByLocationId(@Param("locationId") Long locationId);

    /**
     * 查询园区-地块-角落三级区域树原始数据
     *
     * @return 区域层级行
     */
    List<Map<String, Object>> selectRegionTree();

    /**
     * 查询指定前缀下最大的任务编号。
     *
     * @param prefix 编号前缀
     * @return 最大任务编号
     */
    List<String> selectTaskNosByPrefix(@Param("prefix") String prefix);

    /**
     * 查询字典数据，导入时用于文字反查字典值。
     *
     * @param dictType 字典类型
     * @return 字典数据
     */
    List<Map<String, Object>> selectDictDataByType(@Param("dictType") String dictType);

    /**
     * 根据任务编号统计记录数量。
     *
     * @param taskNo 任务编号
     * @param excludeId 排除的记录ID
     * @return 数量
     */
    Long selectTaskCountByTaskNo(@Param("taskNo") String taskNo, @Param("excludeId") Long excludeId);

    /**
     * 批量删除农事任务
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteAgriTaskByIds(@Param("ids") Long[] ids);

}
