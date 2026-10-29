package com.ruoyi.binZhou.xwq.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Service;
import com.ruoyi.binZhou.xwq.domain.WorkspaceHiddenItem;
import com.ruoyi.binZhou.xwq.service.IAiImageService;
import com.ruoyi.binZhou.xwq.service.IAiRecordService;
import com.ruoyi.binZhou.xwq.service.IAiWorkspaceHiddenService;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.exception.ServiceException;

@Service
@SuppressWarnings({ "rawtypes", "unchecked" })
public class AiWorkspaceHiddenServiceImpl implements IAiWorkspaceHiddenService
{
    private static final String CACHE_PREFIX = "xwq:workspace:hidden:";
    private final RedisCache redisCache;
    private final IAiImageService imageService;
    private final IAiRecordService recordService;

    public AiWorkspaceHiddenServiceImpl(RedisCache redisCache,
            IAiImageService imageService, IAiRecordService recordService)
    {
        this.redisCache = redisCache;
        this.imageService = imageService;
        this.recordService = recordService;
    }

    @Override
    public List<WorkspaceHiddenItem> selectHiddenList(Long userId, String moduleType)
    {
        if (userId == null) return Collections.emptyList();
        List<WorkspaceHiddenItem> items = new ArrayList<>();
        appendItems(items, userId, moduleType, "image");
        appendItems(items, userId, moduleType, "record");
        return items;
    }

    @Override
    public void hide(Long userId, String moduleType, String targetType, Long targetId)
    {
        validate(userId, targetType, targetId);
        redisCache.redisTemplate.opsForSet().add(
                buildKey(userId, moduleType, targetType), targetId.toString());
    }

    @Override
    public void hideBatch(Long userId, String moduleType, String targetType, List<Long> targetIds)
    {
        if (targetIds == null || targetIds.isEmpty())
        {
            throw new ServiceException("隐藏目标不能为空");
        }
        for (Long targetId : targetIds)
        {
            hide(userId, moduleType, targetType, targetId);
        }
    }

    @Override
    public void restore(Long userId, String moduleType, String targetType, Long targetId)
    {
        if (userId == null) throw new ServiceException("用户信息不能为空");
        validateType(targetType);
        redisCache.redisTemplate.opsForSet().remove(
                buildKey(userId, moduleType, targetType), targetId.toString());
    }

    private void validate(Long userId, String targetType, Long targetId)
    {
        if (userId == null) throw new ServiceException("用户信息不能为空");
        if (targetId == null || targetId <= 0) throw new ServiceException("隐藏目标不能为空");
        validateType(targetType);
        boolean exists = "image".equals(targetType)
                ? imageService.selectAiImageByImageId(targetId) != null
                : recordService.selectAiRecordByRecordId(targetId) != null;
        if (!exists) throw new ServiceException("隐藏目标不存在");
    }

    private void validateType(String targetType)
    {
        if (!"image".equals(targetType) && !"record".equals(targetType))
        {
            throw new ServiceException("隐藏目标类型不正确");
        }
    }

    @SuppressWarnings("unchecked")
    private void appendItems(List<WorkspaceHiddenItem> items, Long userId,
            String moduleType, String targetType)
    {
        Set<Object> ids = redisCache.redisTemplate.opsForSet().members(
                buildKey(userId, moduleType, targetType));
        if (ids == null) return;
        for (Object id : ids)
        {
            items.add(new WorkspaceHiddenItem(
                    targetType, Long.valueOf(String.valueOf(id))));
        }
    }

    private String buildKey(Long userId, String moduleType, String targetType)
    {
        return CACHE_PREFIX + userId + ":" + moduleType + ":" + targetType;
    }
}
