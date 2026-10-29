package com.ruoyi.iot.ld.service;

import java.util.List;
import com.ruoyi.iot.ld.domain.ImageRecord;

/**
 * 抓拍图片记录Service接口
 * 
 * @author ruoyi
 * @date 2025-12-22
 */
public interface IImageRecordService 
{
    /**
     * 查询抓拍图片记录
     * 
     * @param id 抓拍图片记录主键
     * @return 抓拍图片记录
     */
    public ImageRecord selectImageRecordById(Long id);

    /**
     * 查询抓拍图片记录列表
     * 
     * @param imageRecord 抓拍图片记录
     * @return 抓拍图片记录集合
     */
    public List<ImageRecord> selectImageRecordList(ImageRecord imageRecord);

    /**
     * 新增抓拍图片记录
     * 
     * @param imageRecord 抓拍图片记录
     * @return 结果
     */
    public int insertImageRecord(ImageRecord imageRecord);

    /**
     * 修改抓拍图片记录
     * 
     * @param imageRecord 抓拍图片记录
     * @return 结果
     */
    public int updateImageRecord(ImageRecord imageRecord);

    /**
     * 批量删除抓拍图片记录
     * 
     * @param ids 需要删除的抓拍图片记录主键集合
     * @return 结果
     */
    public int deleteImageRecordByIds(Long[] ids);

    /**
     * 删除抓拍图片记录信息
     * 
     * @param id 抓拍图片记录主键
     * @return 结果
     */
    public int deleteImageRecordById(Long id);
}
