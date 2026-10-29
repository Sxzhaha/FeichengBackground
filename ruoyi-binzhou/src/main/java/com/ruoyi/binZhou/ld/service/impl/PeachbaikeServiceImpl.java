package com.ruoyi.binZhou.ld.service.impl;

import java.util.List;

import com.ruoyi.binZhou.ld.domain.Peachbaike;
import com.ruoyi.binZhou.ld.domain.PeachbaikeSimpleVO;
import com.ruoyi.binZhou.ld.mapper.PeachbaikeMapper;
import com.ruoyi.binZhou.ld.service.IPeachbaikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 【请填写功能名称】Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-04-18
 */
@Service
public class PeachbaikeServiceImpl implements IPeachbaikeService
{
    @Autowired
    private PeachbaikeMapper peachbaikeMapper;

    /**
     * 查询【请填写功能名称】
     * 
     * @param id 【请填写功能名称】主键
     * @return 【请填写功能名称】
     */
    @Override
    public Peachbaike selectPeachbaikeById(Long id)
    {
        return peachbaikeMapper.selectPeachbaikeById(id);
    }

    /**
     * 查询【请填写功能名称】列表
     * 
     * @param peachbaike 【请填写功能名称】
     * @return 【请填写功能名称】
     */
    @Override
    public List<Peachbaike> selectPeachbaikeList(Peachbaike peachbaike)
    {
        return peachbaikeMapper.selectPeachbaikeList(peachbaike);
    }

    /**
     * 新增【请填写功能名称】
     * 
     * @param peachbaike 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int insertPeachbaike(Peachbaike peachbaike)
    {
        return peachbaikeMapper.insertPeachbaike(peachbaike);
    }

    /**
     * 修改【请填写功能名称】
     * 
     * @param peachbaike 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int updatePeachbaike(Peachbaike peachbaike)
    {
        return peachbaikeMapper.updatePeachbaike(peachbaike);
    }

    /**
     * 批量删除【请填写功能名称】
     * 
     * @param ids 需要删除的【请填写功能名称】主键
     * @return 结果
     */
    @Override
    public int deletePeachbaikeByIds(Long[] ids)
    {
        return peachbaikeMapper.deletePeachbaikeByIds(ids);
    }

    /**
     * 删除【请填写功能名称】信息
     * 
     * @param id 【请填写功能名称】主键
     * @return 结果
     */
    @Override
    public int deletePeachbaikeById(Long id)
    {
        return peachbaikeMapper.deletePeachbaikeById(id);
    }

    @Override
    public List<PeachbaikeSimpleVO> selectSimpleList() {
        return peachbaikeMapper.selectSimplePeachbaikeList();
    }
}
