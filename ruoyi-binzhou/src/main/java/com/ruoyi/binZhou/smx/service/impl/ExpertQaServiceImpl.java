package com.ruoyi.binZhou.smx.service.impl;

import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.TimeZone;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import java.util.Set;
import java.util.HashSet;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.binZhou.smx.domain.ExpertAnswer;
import com.ruoyi.binZhou.smx.domain.ExpertCrop;
import com.ruoyi.binZhou.smx.domain.ExpertQuestion;
import com.ruoyi.binZhou.smx.mapper.ExpertAnswerMapper;
import com.ruoyi.binZhou.smx.mapper.ExpertCropMapper;
import com.ruoyi.binZhou.smx.mapper.ExpertLikeMapper;
import com.ruoyi.binZhou.smx.mapper.ExpertQuestionMapper;
import com.ruoyi.binZhou.smx.service.ExpertQuestionAiService;
import com.ruoyi.binZhou.smx.service.IExpertQaService;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;

/**
 * 专家问答Service业务层处理
 *
 * @author smx
 */
@Service
public class ExpertQaServiceImpl implements IExpertQaService
{
    private final ExpertQuestionMapper questionMapper;
    private final ExpertAnswerMapper answerMapper;
    private final ExpertCropMapper cropMapper;
    private final ExpertLikeMapper likeMapper;
    private final ExpertQuestionAiService aiService;

    public ExpertQaServiceImpl(ExpertQuestionMapper questionMapper, ExpertAnswerMapper answerMapper, ExpertCropMapper cropMapper,
            ExpertLikeMapper likeMapper, ExpertQuestionAiService aiService)
    {
        this.questionMapper = questionMapper;
        this.answerMapper = answerMapper;
        this.cropMapper = cropMapper;
        this.likeMapper = likeMapper;
        this.aiService = aiService;
    }

    @Override
    public List<ExpertQuestion> selectQuestionList(ExpertQuestion query)
    {
        // 搜索问答：关键词去空格后转义 like 通配符，按字面量模糊匹配，避免 "%%" 之类输入放大匹配范围
        query.setKeyword(escapeLikeKeyword(StringUtils.trim(query.getKeyword())));
        List<ExpertQuestion> list = questionMapper.selectQuestionList(query);
        list.forEach(this::fillDisplayFields);
        return list;
    }

    @Override
    public ExpertQuestion selectQuestionById(Long questionId)
    {
        return selectQuestionById(questionId, true);
    }

    @Override
    public ExpertQuestion selectQuestionById(Long questionId, boolean countView)
    {
        ExpertQuestion question = questionMapper.selectQuestionById(questionId);
        if (question != null)
        {
            if (countView)
            {
                questionMapper.increaseViewCount(questionId);
            }
            fillDisplayFields(question);
            // 兼容服务重启前遗留的"识别中"数据，以及历史失败（如视觉模型不可用）数据：
            // 详情页访问时自动补跑识别，成功后状态翻转为已完成
            if ("0".equals(question.getAiStatus()) || "2".equals(question.getAiStatus()))
            {
                aiService.retryIfPending(question.getQuestionId(), question.getPhotos(), question.getCrop(), question.getTitle());
            }
        }
        return question;
    }

    @Override
    public int insertQuestion(ExpertQuestion question)
    {
        String crop = StringUtils.trim(question.getCrop());
        String title = StringUtils.trim(question.getTitle());
        String content = StringUtils.trim(question.getContent());
        if (StringUtils.isEmpty(title) || title.length() > 128)
        {
            throw new ServiceException("问题标题不能为空且不能超过128个字符");
        }
        if (StringUtils.isEmpty(content) || content.length() > 1024)
        {
            throw new ServiceException("问题描述不能为空且不能超过1024个字符");
        }
        if (crop != null && crop.length() > 32)
        {
            throw new ServiceException("作物名称不能超过32个字符");
        }
        if (question.getPhotos() != null && question.getPhotos().length > 3)
        {
            throw new ServiceException("最多上传3张图片");
        }
        question.setCrop(crop);
        question.setTitle(title);
        question.setContent(content);
        if (!StringUtils.isEmpty(crop) && cropMapper.selectByCrop(crop) == null)
        {
            ExpertCrop newCrop = new ExpertCrop();
            newCrop.setCategory("其他");
            newCrop.setCrop(crop);
            cropMapper.insertIfAbsent(newCrop);
        }
        SysUser user = SecurityUtils.getLoginUser().getUser();
        question.setUserId(user.getUserId());
        question.setUserName(user.getNickName());
        question.setUserAvatar(user.getAvatar());
        if (question.getPhotos() != null && question.getPhotos().length > 0)
        {
            question.setImages(JSON.toJSONString(question.getPhotos()));
            question.setAiStatus("0"); // 带图：识别中
        }
        else
        {
            question.setAiStatus("2"); // 无图：不触发AI识别
        }
        int rows = questionMapper.insertQuestion(question);
        // 带图提问时异步触发AI病害识别，结果落库供详情页展示；无图不触发AI推理
        if (question.getPhotos() != null && question.getPhotos().length > 0)
        {
            aiService.recognizeAsync(question.getQuestionId(), question.getPhotos(), question.getCrop(), question.getTitle());
        }
        return rows;
    }

    @Override
    public List<ExpertAnswer> selectAnswersByQuestionId(Long questionId)
    {
        List<ExpertAnswer> answers = answerMapper.selectAnswersByQuestionId(questionId, SecurityUtils.getUserId());
        // 仅回答者本人或管理员可见单条回答的删除入口；非归属人即使绕过前端也会在删除接口再次拦截
        boolean admin = SecurityUtils.isAdmin();
        Long userId = SecurityUtils.getUserId();
        answers.forEach(answer -> answer.setCanDelete(admin || Objects.equals(answer.getAnswerUserId(), userId)));
        return answers;
    }

    @Override
    @Transactional
    public int insertAnswer(Long questionId, ExpertAnswer answer)
    {
        if (questionId == null || questionMapper.selectQuestionById(questionId) == null)
        {
            throw new ServiceException("问题不存在或已删除");
        }
        String content = StringUtils.trim(answer.getContent());
        if (StringUtils.isEmpty(content) || content.length() > 2048)
        {
            throw new ServiceException("回答内容不能为空且不能超过2048个字符");
        }
        SysUser user = SecurityUtils.getLoginUser().getUser();
        answer.setQuestionId(questionId);
        answer.setContent(content);
        answer.setAnswerUserId(user.getUserId());
        // 回答配图：移动端传 photos 数组，序列化为 JSON 存入 images 列（最多3张）
        if (answer.getPhotos() != null && !answer.getPhotos().isEmpty())
        {
            if (answer.getPhotos().size() > 3)
            {
                throw new ServiceException("回答图片最多3张");
            }
            answer.setImages(JSON.toJSONString(answer.getPhotos()));
        }
        if (StringUtils.isEmpty(answer.getExpertName()))
        {
            answer.setExpertName(user.getNickName());
        }
        if (StringUtils.isEmpty(answer.getExpertOrg()) && user.getDept() != null)
        {
            answer.setExpertOrg(user.getDept().getDeptName());
        }
        answer.setExpertAvatar(user.getAvatar());
        // 职称取用户主岗位名（管理端岗位管理维护），无岗位则不展示职称
        if (StringUtils.isEmpty(answer.getExpertTitle()))
        {
            answer.setExpertTitle(answerMapper.selectPostNameByUserId(user.getUserId()));
        }
        if (StringUtils.isEmpty(answer.getIsAdoption()))
        {
            answer.setIsAdoption("0");
        }
        int rows = answerMapper.insertAnswer(answer);
        questionMapper.increaseReplyCount(questionId);
        return rows;
    }

    @Override
    @Transactional
    public int deleteQuestion(Long questionId)
    {
        if (questionId == null)
        {
            throw new ServiceException("问题ID不能为空");
        }
        if (questionMapper.existsQuestionById(questionId) == 0)
        {
            throw new ServiceException("问题不存在或已删除");
        }
        // 仅提问者本人或管理员可删除；非归属人即使绕过前端也在此拦截
        if (!SecurityUtils.isAdmin() && !Objects.equals(questionMapper.selectQuestionUserIdById(questionId), SecurityUtils.getUserId()))
        {
            throw new ServiceException("仅提问者本人可以删除该问答");
        }
        String images = questionMapper.selectQuestionImagesById(questionId);
        Set<String> photos = parsePhotoSet(images);
        // 级联清理点赞记录（问题本身+其所有回答），避免遗留孤儿点赞导致用户永远无法取消赞
        likeMapper.deleteLikesByQuestion(questionId);
        answerMapper.deleteAnswersByQuestionId(questionId);
        int rows = questionMapper.deleteQuestionById(questionId);
        photos.forEach(this::deleteQuestionPhoto);
        return rows;
    }

    @Override
    @Transactional
    public int deleteAnswer(Long answerId)
    {
        if (answerId == null)
        {
            throw new ServiceException("回答ID不能为空");
        }
        ExpertAnswer answer = answerMapper.selectAnswerMetaById(answerId);
        if (answer == null)
        {
            throw new ServiceException("回答不存在或已删除");
        }
        // 仅回答者本人或管理员可删除；非归属人即使绕过前端也在此拦截
        if (!SecurityUtils.isAdmin() && !Objects.equals(answer.getAnswerUserId(), SecurityUtils.getUserId()))
        {
            throw new ServiceException("仅回答者本人可以删除该回答");
        }
        // 级联清理该回答的点赞记录，避免遗留孤儿点赞
        likeMapper.deleteLikesByAnswer(answerId);
        int rows = answerMapper.deleteAnswerById(answerId);
        questionMapper.decreaseReplyCount(answer.getQuestionId());
        // 回答删光后问题状态回退为待回答
        questionMapper.refreshQuestionAnswerStatus(answer.getQuestionId());
        return rows;
    }

    @Override
    @Transactional
    public Map<String, Object> toggleLike(String targetType, Long targetId)
    {
        if (!"1".equals(targetType) && !"2".equals(targetType))
        {
            throw new ServiceException("点赞目标类型不合法");
        }
        if (targetId == null)
        {
            throw new ServiceException("点赞目标ID不能为空");
        }
        Long userId = SecurityUtils.getUserId();
        // 原子化：insert ignore 返回0说明记录已存在（含并发场景），据此决定本次是点赞还是取消，消除竞态
        int inserted = likeMapper.insertLike(userId, targetType, targetId);
        boolean liked = inserted > 0;
        int delta = liked ? 1 : -1;
        if (!liked)
        {
            likeMapper.deleteLike(userId, targetType, targetId);
        }
        // 目标不存在时计数更新行数为0，抛错让事务回滚（撤销上面插入/删除的点赞记录）
        if (questionOrAnswerLikeCountUpdate(targetType, targetId, delta) == 0)
        {
            throw new ServiceException("点赞目标不存在或已删除");
        }
        long likeCount = "1".equals(targetType)
                ? questionMapper.selectQuestionLikeCountById(targetId)
                : answerMapper.selectAnswerLikeCountById(targetId);
        Map<String, Object> result = new HashMap<>();
        result.put("liked", liked);
        result.put("likeCount", likeCount);
        return result;
    }

    /**
     * 按目标类型更新对应表的点赞计数
     */
    private int questionOrAnswerLikeCountUpdate(String targetType, Long targetId, int delta)
    {
        return "1".equals(targetType)
                ? questionMapper.updateLikeCount(targetId, delta)
                : answerMapper.updateLikeCount(targetId, delta);
    }

    @Override
    public List<ExpertCrop> selectCropList()
    {
        return cropMapper.selectCropList();
    }

    @Override
    public List<ExpertQuestion> selectMyAnsweredQuestions()
    {
        return questionMapper.selectMyAnsweredQuestions(SecurityUtils.getUserId());
    }

    /**
     * 转义 like 通配符，使检索关键词按字面量参与匹配（% 与 _ 不再作为通配符）
     */
    private String escapeLikeKeyword(String keyword)
    {
        if (StringUtils.isEmpty(keyword))
        {
            return keyword;
        }
        return keyword.replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_");
    }

    /**
     * 组装移动端卡片展示字段
     */
    private void fillDisplayFields(ExpertQuestion question)
    {
        question.setId(question.getQuestionId());
        if (question.getCreateTime() != null)
        {
            question.setDate(formatExpertDate(question.getCreateTime()));
        }
        question.setViews(question.getViewCount());
        question.setLikes(question.getLikeCount());
        question.setReplies(question.getReplyCount());
        question.setPhotos(parsePhotos(question.getImages()));
        Long userId = SecurityUtils.getUserId();
        question.setCanDelete(SecurityUtils.isAdmin() || Objects.equals(question.getUserId(), userId));
        question.setLiked(likeMapper.existsLike(userId, "1", question.getQuestionId()) > 0);
    }

    private String formatExpertDate(java.util.Date date)
    {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        formatter.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
        return formatter.format(date);
    }

    /**
     * 解析images字段的JSON数组为图片URL数组
     */
    private String[] parsePhotos(String images)
    {
        if (StringUtils.isEmpty(images))
        {
            return new String[0];
        }
        try
        {
            JSONArray array = JSON.parseArray(images);
            String[] photos = new String[array.size()];
            for (int i = 0; i < array.size(); i++)
            {
                photos[i] = array.getString(i);
            }
            return photos;
        }
        catch (Exception e)
        {
            return new String[0];
        }
    }

    private Set<String> parsePhotoSet(String images)
    {
        Set<String> photos = new HashSet<>();
        if (StringUtils.isEmpty(images))
        {
            return photos;
        }
        try
        {
            JSONArray array = JSON.parseArray(images);
            for (int i = 0; i < array.size(); i++)
            {
                String photo = array.getString(i);
                if (StringUtils.isNotEmpty(photo))
                {
                    photos.add(photo);
                }
            }
        }
        catch (Exception e)
        {
            // 图片字段异常时不影响数据库删除，也不尝试删除不可信路径。
        }
        return photos;
    }

    private void deleteQuestionPhoto(String photo)
    {
        String marker = "/profile/";
        int index = photo.indexOf(marker);
        if (index < 0)
        {
            return;
        }
        try
        {
            Path root = Paths.get(com.ruoyi.common.config.RuoYiConfig.getProfile()).toAbsolutePath().normalize();
            Path filePath = root.resolve(photo.substring(index + marker.length())).normalize();
            if (filePath.startsWith(root))
            {
                File file = filePath.toFile();
                if (file.isFile())
                {
                    file.delete();
                }
            }
        }
        catch (Exception e)
        {
            // 文件清理失败不回滚已完成的数据库删除。
        }
    }
}
