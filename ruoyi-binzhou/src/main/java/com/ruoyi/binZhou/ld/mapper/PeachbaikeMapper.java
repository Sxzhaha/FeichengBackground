package com.ruoyi.binZhou.ld.mapper;

import java.util.List;
import com.ruoyi.binZhou.ld.domain.Peachbaike;
import com.ruoyi.binZhou.ld.domain.PeachbaikeSimpleVO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 【请填写功能名称】Mapper接口
 * 
 * @author ruoyi
 * @date 2026-04-18
 */
@Mapper
public interface PeachbaikeMapper 
{

    /**
     * 查询桃树简易列表（卡片展示用）
     */
    List<PeachbaikeSimpleVO> selectSimplePeachbaikeList();
    /**
     * 查询【请填写功能名称】
     * 
     * @param id 【请填写功能名称】主键
     * @return 【请填写功能名称】
     */
    public Peachbaike selectPeachbaikeById(Long id);

    /**
     * 查询【请填写功能名称】列表
     * 
     * @param peachbaike 【请填写功能名称】
     * @return 【请填写功能名称】集合
     */
    public List<Peachbaike> selectPeachbaikeList(Peachbaike peachbaike);

    /**
     * 新增【请填写功能名称】
     * 
     * @param peachbaike 【请填写功能名称】
     * @return 结果
     */
    public int insertPeachbaike(Peachbaike peachbaike);

    /**
     * 修改【请填写功能名称】
     * 
     * @param peachbaike 【请填写功能名称】
     * @return 结果
     */
    public int updatePeachbaike(Peachbaike peachbaike);

    /**
     * 删除【请填写功能名称】
     * 
     * @param id 【请填写功能名称】主键
     * @return 结果
     */
    public int deletePeachbaikeById(Long id);

    /**
     * 批量删除【请填写功能名称】
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deletePeachbaikeByIds(Long[] ids);
}
