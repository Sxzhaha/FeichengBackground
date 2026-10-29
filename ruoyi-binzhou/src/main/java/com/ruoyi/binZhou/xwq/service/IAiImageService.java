package com.ruoyi.binZhou.xwq.service;

import java.util.List;
import javax.servlet.http.HttpServletResponse;
import com.ruoyi.binZhou.xwq.domain.AiImage;

/**
 * 图片信息Service接口
 *
 * @author ruoyi
 * @date 2026-06-24
 */
public interface IAiImageService
{
    /**
     * 查询图片信息
     */
    public AiImage selectAiImageByImageId(Long imageId);

    /**
     * 查询图片信息列表
     */
    public List<AiImage> selectAiImageList(AiImage aiImage);

    /**
     * 新增图片信息
     */
    public int insertAiImage(AiImage aiImage);

    /**
     * 修改图片信息
     */
    public int updateAiImage(AiImage aiImage);

    /**
     * 批量删除图片信息
     */
    public int deleteAiImageByImageIds(Long[] imageIds);

    /**
     * 删除图片信息
     */
    public int deleteAiImageByImageId(Long imageId);

    /**
     * 导出原始图片 ZIP。
     */
    public void exportOriginalImages(AiImage aiImage, HttpServletResponse response);

    /**
     * 导出虫情识别结果图 ZIP。
     */
    public void exportPestResultImages(AiImage aiImage, HttpServletResponse response);
}
