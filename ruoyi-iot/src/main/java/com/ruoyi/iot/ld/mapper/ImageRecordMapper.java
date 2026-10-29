package com.ruoyi.iot.ld.mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;
import com.ruoyi.iot.ld.domain.ImageRecord;

/**
 * 抓拍图片记录Mapper接口
 * 
 * @author ruoyi
 * @date 2025-12-22
 */
public interface ImageRecordMapper 
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
     * 删除抓拍图片记录
     * 
     * @param id 抓拍图片记录主键
     * @return 结果
     */
    public int deleteImageRecordById(Long id);

    /**
     * 批量删除抓拍图片记录
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteImageRecordByIds(Long[] ids);

    /**
     * 查询尚未同步到 xwq_ai_image 的摄像头抓拍图片
     * 只读 image_record，不修改原表
     */
    public List<Long> selectUnsyncedDiseaseImageRecordIds(@Param("limit") Integer limit);
}
