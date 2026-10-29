package com.ruoyi.iot.ld.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.iot.ld.mapper.ImageRecordMapper;
import com.ruoyi.iot.ld.domain.ImageRecord;
import com.ruoyi.iot.ld.service.IImageRecordService;

/**
 * 抓拍图片记录Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-12-22
 */
@Service
public class ImageRecordServiceImpl implements IImageRecordService 
{
    @Autowired
    private ImageRecordMapper imageRecordMapper;

    /**
     * 查询抓拍图片记录
     * 
     * @param id 抓拍图片记录主键
     * @return 抓拍图片记录
     */
    @Override
    public ImageRecord selectImageRecordById(Long id)
    {
        return imageRecordMapper.selectImageRecordById(id);
    }

    /**
     * 查询抓拍图片记录列表
     * 
     * @param imageRecord 抓拍图片记录
     * @return 抓拍图片记录
     */
    @Override
    public List<ImageRecord> selectImageRecordList(ImageRecord imageRecord)
    {
        return imageRecordMapper.selectImageRecordList(imageRecord);
    }

    /**
     * 新增抓拍图片记录
     * 
     * @param imageRecord 抓拍图片记录
     * @return 结果
     */
    @Override
    public int insertImageRecord(ImageRecord imageRecord)
    {
        imageRecord.setCreateTime(DateUtils.getNowDate());
        return imageRecordMapper.insertImageRecord(imageRecord);
    }

    /**
     * 修改抓拍图片记录
     * 
     * @param imageRecord 抓拍图片记录
     * @return 结果
     */
    @Override
    public int updateImageRecord(ImageRecord imageRecord)
    {
        return imageRecordMapper.updateImageRecord(imageRecord);
    }

    /**
     * 批量删除抓拍图片记录
     * 
     * @param ids 需要删除的抓拍图片记录主键
     * @return 结果
     */
    @Override
    public int deleteImageRecordByIds(Long[] ids)
    {
        return imageRecordMapper.deleteImageRecordByIds(ids);
    }

    /**
     * 删除抓拍图片记录信息
     * 
     * @param id 抓拍图片记录主键
     * @return 结果
     */
    @Override
    public int deleteImageRecordById(Long id)
    {
        return imageRecordMapper.deleteImageRecordById(id);
    }
}
