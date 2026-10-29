package com.ruoyi.binZhou.xwq.mapper;

import java.util.List;

import com.ruoyi.binZhou.xwq.domain.AiImage;
import org.apache.ibatis.annotations.Param;

/**
 * 图片信息Mapper接口
 *
 * @author ruoyi
 * @date 2026-06-24
 */
public interface AiImageMapper
{
    /**
     * 查询图片信息
     *
     * @param imageId 图片信息主键
     * @return 图片信息
     */
    public AiImage selectAiImageByImageId(Long imageId);

    /**
     * 查询图片信息列表
     *
     * @param aiImage 图片信息
     * @return 图片信息集合
     */
    public List<AiImage> selectAiImageList(AiImage aiImage);

    /**
     * 新增图片信息
     *
     * @param aiImage 图片信息
     * @return 结果
     */
    public int insertAiImage(AiImage aiImage);

    /**
     * 修改图片信息
     *
     * @param aiImage 图片信息
     * @return 结果
     */
    public int updateAiImage(AiImage aiImage);

    /**
     * 删除图片信息
     *
     * @param imageId 图片信息主键
     * @return 结果
     */
    public int deleteAiImageByImageId(Long imageId);

    /**
     * 批量删除图片信息
     *
     * @param imageIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteAiImageByImageIds(Long[] imageIds);

    /**
     * 查询待自动识别图片
     */
    public List<AiImage> selectPendingAutoRecognizeImages(@Param("limit") Integer limit);

    /**
     * 抢占待识别图片：image_status 0 -> 1
     */
    public int claimImageForRecognize(@Param("imageId") Long imageId);

    /**
     * 更新图片识别状态
     */
    public int updateImageStatusById(@Param("imageId") Long imageId,
                                     @Param("imageStatus") Long imageStatus);


    /**
     * 将 image_record 中的摄像头图片同步为病虫害待识别图片
     */
    public int insertDiseaseImageFromImageRecord(@Param("recordId") Long recordId,
                                                 @Param("cropType") String cropType);
}