package com.ruoyi.binZhou.xwq.service.impl;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.servlet.http.HttpServletResponse;

import com.ruoyi.binZhou.xwq.domain.AiImage;
import com.ruoyi.binZhou.xwq.domain.AiPest;
import com.ruoyi.binZhou.xwq.mapper.AiImageMapper;
import com.ruoyi.binZhou.xwq.service.IAiImageService;
import com.ruoyi.binZhou.xwq.service.IAiPestService;
import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 图片信息Service业务层处理
 */
@Service
public class AiImageServiceImpl implements IAiImageService
{
    private static final Logger log = LoggerFactory.getLogger(AiImageServiceImpl.class);

    @Autowired
    private AiImageMapper aiImageMapper;

    @Autowired
    private IAiPestService aiPestService;

    /**
     * 查询图片信息
     */
    @Override
    public AiImage selectAiImageByImageId(Long imageId)
    {
        return aiImageMapper.selectAiImageByImageId(imageId);
    }

    /**
     * 查询图片信息列表
     */
    @Override
    public List<AiImage> selectAiImageList(AiImage aiImage)
    {
        return aiImageMapper.selectAiImageList(aiImage);
    }

    /**
     * 新增图片信息
     */
    @Override
    public int insertAiImage(AiImage aiImage)
    {
        aiImage.setCreateBy(getCurrentUsername());
        aiImage.setCreateTime(DateUtils.getNowDate());
        return aiImageMapper.insertAiImage(aiImage);
    }

    /**
     * 修改图片信息
     */
    @Override
    public int updateAiImage(AiImage aiImage)
    {
        aiImage.setUpdateBy(getCurrentUsername());
        aiImage.setUpdateTime(DateUtils.getNowDate());
        return aiImageMapper.updateAiImage(aiImage);
    }

    /**
     * 批量删除图片信息
     */
    @Override
    public int deleteAiImageByImageIds(Long[] imageIds)
    {
        return aiImageMapper.deleteAiImageByImageIds(imageIds);
    }

    /**
     * 删除图片信息
     */
    @Override
    public int deleteAiImageByImageId(Long imageId)
    {
        return aiImageMapper.deleteAiImageByImageId(imageId);
    }

    /**
     * 按当前筛选条件导出原始图片 zip。
     *
     * 注意：
     * 1. 这里导出的是 xwq_ai_image.image_url 原图；
     * 2. 不导出 image_result_url / pest_result_img_url，因为那些通常是识别后的打框图；
     * 3. 前端虫情图片页面应传 imageAiType = pest。
     */
    @Override
    public void exportOriginalImages(AiImage aiImage, HttpServletResponse response)
    {
        List<AiImage> list = aiImageMapper.selectAiImageList(aiImage);

        String zipName = "原始图片_" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + ".zip";

        try
        {
            String encodedName = URLEncoder.encode(zipName, StandardCharsets.UTF_8.name()).replaceAll("\\+", "%20");

            response.reset();
            response.setCharacterEncoding("utf-8");
            response.setContentType("application/zip");
            response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + encodedName);

            try (ZipOutputStream zipOut = new ZipOutputStream(response.getOutputStream()))
            {
                Set<String> usedNames = new HashSet<>();
                int successCount = 0;

                if (list != null)
                {
                    for (AiImage image : list)
                    {
                        if (image == null || isBlank(image.getImageUrl()))
                        {
                            continue;
                        }

                        String entryName = buildZipEntryName(image, usedNames);

                        try (InputStream inputStream = openImageInputStream(image.getImageUrl()))
                        {
                            zipOut.putNextEntry(new ZipEntry(entryName));
                            copy(inputStream, zipOut);
                            zipOut.closeEntry();
                            successCount++;
                        }
                        catch (Exception e)
                        {
                            log.warn("原始图片导出跳过：imageId={}，imageUrl={}，原因={}",
                                    image.getImageId(), image.getImageUrl(), e.getMessage());
                        }
                    }
                }

                if (successCount == 0)
                {
                    zipOut.putNextEntry(new ZipEntry("README.txt"));
                    String message = "当前筛选条件下未找到可导出的原始图片，或图片文件不存在。";
                    zipOut.write(message.getBytes(StandardCharsets.UTF_8));
                    zipOut.closeEntry();
                }

                zipOut.finish();
            }
        }
        catch (IOException e)
        {
            throw new ServiceException("导出原始图片失败：" + e.getMessage());
        }
    }

    /**
     * 按当前筛选条件导出虫情识别结果图 ZIP。
     *
     * 先使用 AiImage 查询完成图片名称、识别状态和监测范围筛选，
     * 再复用 IAiPestService 获取对应 imageId 的 pest_result_img_url。
     * 不新增 Mapper 方法，不回退导出原图。
     */
    @Override
    public void exportPestResultImages(AiImage aiImage, HttpServletResponse response)
    {
        List<AiImage> matchedImages = aiImageMapper.selectAiImageList(aiImage);
        Set<Long> matchedImageIds = new HashSet<>();

        if (matchedImages != null)
        {
            for (AiImage image : matchedImages)
            {
                if (image != null && image.getImageId() != null)
                {
                    matchedImageIds.add(image.getImageId());
                }
            }
        }

        AiPest pestQuery = new AiPest();
        pestQuery.setParkId(aiImage.getParkId());
        pestQuery.setAreaId(aiImage.getPlotId());
        pestQuery.setLocationId(aiImage.getImageB());
        pestQuery.setDeviceId(aiImage.getDeviceId());

        List<AiPest> pestList = matchedImageIds.isEmpty()
                ? null
                : aiPestService.selectAiPestList(pestQuery);

        String zipName = "虫情识别结果图_"
                + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date())
                + ".zip";

        try
        {
            String encodedName = URLEncoder.encode(
                    zipName,
                    StandardCharsets.UTF_8.name()
            ).replaceAll("\\+", "%20");

            response.reset();
            response.setCharacterEncoding("utf-8");
            response.setContentType("application/zip");
            response.setHeader(
                    "Content-Disposition",
                    "attachment; filename*=UTF-8''" + encodedName
            );

            try (ZipOutputStream zipOut = new ZipOutputStream(response.getOutputStream()))
            {
                Set<String> usedNames = new HashSet<>();
                Set<String> exportedKeys = new HashSet<>();
                int successCount = 0;

                if (pestList != null)
                {
                    for (AiPest pest : pestList)
                    {
                        if (pest == null
                                || pest.getImageId() == null
                                || !matchedImageIds.contains(pest.getImageId())
                                || isBlank(pest.getPestResultImgUrl()))
                        {
                            continue;
                        }

                        String exportKey = pest.getImageId()
                                + "|"
                                + pest.getPestResultImgUrl();

                        if (!exportedKeys.add(exportKey))
                        {
                            continue;
                        }

                        String entryName = buildPestResultZipEntryName(
                                pest,
                                usedNames
                        );

                        try (InputStream inputStream = openImageInputStream(
                                pest.getPestResultImgUrl()
                        ))
                        {
                            zipOut.putNextEntry(new ZipEntry(entryName));
                            copy(inputStream, zipOut);
                            zipOut.closeEntry();
                            successCount++;
                        }
                        catch (Exception e)
                        {
                            log.warn(
                                    "虫情结果图导出跳过：imageId={}，resultUrl={}，原因={}",
                                    pest.getImageId(),
                                    pest.getPestResultImgUrl(),
                                    e.getMessage()
                            );
                        }
                    }
                }

                if (successCount == 0)
                {
                    zipOut.putNextEntry(new ZipEntry("README.txt"));
                    String message = "当前筛选条件下未找到可导出的虫情识别结果图，"
                            + "或结果图片文件不存在。";
                    zipOut.write(message.getBytes(StandardCharsets.UTF_8));
                    zipOut.closeEntry();
                }

                zipOut.finish();
            }
        }
        catch (IOException e)
        {
            throw new ServiceException(
                    "导出虫情识别结果图失败：" + e.getMessage()
            );
        }
    }

    private String buildPestResultZipEntryName(
            AiPest pest,
            Set<String> usedNames
    )
    {
        String name = extractFileName(pest.getPestResultImgUrl());

        if (isBlank(name))
        {
            name = "pest_result_" + pest.getImageId() + ".jpg";
        }

        name = sanitizeFileName(name);

        String prefix = pest.getImageId() == null
                ? "result"
                : String.valueOf(pest.getImageId());

        String entryName = prefix + "_" + name;
        String uniqueName = entryName;
        int index = 1;

        while (usedNames.contains(uniqueName))
        {
            uniqueName = prefix + "_" + index + "_" + name;
            index++;
        }

        usedNames.add(uniqueName);
        return uniqueName;
    }

    /**
     * 打开图片输入流。
     * 支持：
     * 1. http / https 图片地址；
     * 2. /profile/upload/xxx.jpg 这种若依本地资源路径；
     * 3. 服务器绝对路径。
     */
    private InputStream openImageInputStream(String imageUrl) throws IOException
    {
        String url = imageUrl == null ? "" : imageUrl.trim();

        if (url.startsWith("http://") || url.startsWith("https://"))
        {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(15000);
            connection.setRequestProperty("User-Agent", "Mozilla/5.0");
            return connection.getInputStream();
        }

        String localPath = toLocalFilePath(url);
        File file = new File(localPath);

        if (!file.exists() || !file.isFile())
        {
            throw new FileNotFoundException("图片文件不存在：" + localPath);
        }

        return new BufferedInputStream(new FileInputStream(file));
    }

    /**
     * 将数据库中的图片 URL 转成本地文件路径。
     *
     * 若依通常把 /profile/** 映射到 RuoYiConfig.getProfile()。
     */
    private String toLocalFilePath(String imageUrl)
    {
        String cleanUrl = imageUrl.split("\\?")[0].split("#")[0];

        int profileIndex = cleanUrl.indexOf("/profile/");
        if (profileIndex >= 0)
        {
            String relativePath = cleanUrl.substring(profileIndex + "/profile".length());
            return RuoYiConfig.getProfile() + relativePath.replace("/", File.separator);
        }

        if (cleanUrl.startsWith("profile/"))
        {
            String relativePath = cleanUrl.substring("profile".length());
            return RuoYiConfig.getProfile() + relativePath.replace("/", File.separator);
        }

        if (cleanUrl.startsWith(File.separator) || cleanUrl.matches("^[A-Za-z]:.*"))
        {
            File file = new File(cleanUrl);
            if (file.exists())
            {
                return cleanUrl;
            }
        }

        if (cleanUrl.startsWith("/"))
        {
            return RuoYiConfig.getProfile() + cleanUrl.replace("/", File.separator);
        }

        return RuoYiConfig.getProfile() + File.separator + cleanUrl.replace("/", File.separator);
    }

    private String buildZipEntryName(AiImage image, Set<String> usedNames)
    {
        String name = image.getImageName();

        if (isBlank(name))
        {
            name = extractFileName(image.getImageUrl());
        }

        if (isBlank(name))
        {
            name = "image_" + image.getImageId();
        }

        name = sanitizeFileName(name);

        if (!name.contains("."))
        {
            String suffix = isBlank(image.getImageFormat()) ? "jpg" : image.getImageFormat().replace(".", "");
            name = name + "." + suffix;
        }

        String prefix = image.getImageId() == null ? "image" : String.valueOf(image.getImageId());
        String entryName = prefix + "_" + name;

        int index = 1;
        String uniqueName = entryName;
        while (usedNames.contains(uniqueName))
        {
            uniqueName = prefix + "_" + index + "_" + name;
            index++;
        }

        usedNames.add(uniqueName);
        return uniqueName;
    }

    private String extractFileName(String imageUrl)
    {
        if (imageUrl == null)
        {
            return "";
        }

        String cleanUrl = imageUrl.split("\\?")[0].split("#")[0];
        int index = cleanUrl.lastIndexOf("/");
        return index >= 0 ? cleanUrl.substring(index + 1) : cleanUrl;
    }

    private String sanitizeFileName(String name)
    {
        return name == null ? "" : name.replaceAll("[\\\\/:*?\"<>|]", "_");
    }

    private boolean isBlank(String value)
    {
        return value == null || value.trim().isEmpty();
    }

    private void copy(InputStream inputStream, ZipOutputStream zipOut) throws IOException
    {
        byte[] buffer = new byte[8192];
        int len;
        while ((len = inputStream.read(buffer)) != -1)
        {
            zipOut.write(buffer, 0, len);
        }
    }

    /**
     * 获取当前操作人。
     * 登录请求使用当前用户名；自动任务或无登录上下文时使用 system。
     *
     * @return 操作人用户名
     */
    private String getCurrentUsername()
    {
        try
        {
            String username = SecurityUtils.getUsername();
            return username == null || username.trim().isEmpty()
                    ? "system"
                    : username;
        }
        catch (Exception e)
        {
            return "system";
        }
    }
}
