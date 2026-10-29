package com.ruoyi.binZhou.ld.service;

import java.util.List;

import com.ruoyi.binZhou.ld.domain.Peachbaike;
import com.ruoyi.binZhou.ld.domain.PeachbaikeSimpleVO;

/**
 * 【请填写功能名称】Service接口
 * 
 * @author ruoyi
 * @date 2026-04-18
 */
public interface IPeachbaikeService 
{
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
     * 批量删除【请填写功能名称】
     * 
     * @param ids 需要删除的【请填写功能名称】主键集合
     * @return 结果
     */
    public int deletePeachbaikeByIds(Long[] ids);

    /**
     * 删除【请填写功能名称】信息
     * 
     * @param id 【请填写功能名称】主键
     * @return 结果
     */
    public int deletePeachbaikeById(Long id);

    List<PeachbaikeSimpleVO> selectSimpleList();
}
