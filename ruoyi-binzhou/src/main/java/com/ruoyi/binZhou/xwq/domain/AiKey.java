package com.ruoyi.binZhou.xwq.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 密钥管理对象 xwq_ai_key
 * 
 * @author ruoyi
 * @date 2026-06-24
 */
public class AiKey extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 唯一标识一条密钥记录 */
    private Long keyId;

    /** 密钥名称 */
    @Excel(name = "密钥名称")
    private String keyName;

    /** 服务商名称，如阿里云、百度、自建模型服务等 */
    @Excel(name = "服务商名称，如阿里云、百度、自建模型服务等")
    private String keyProviderName;

    /** API Key，加密存储 */
    @Excel(name = "API Key，加密存储")
    private String keyApiKey;

    /** Secret Key，加密存储 */
    @Excel(name = "Secret Key，加密存储")
    private String keySecret;

    /** 模型或第三方服务接口基础地址 */
    @Excel(name = "模型或第三方服务接口基础地址")
    private String keyBaseUrl;

    /** 关联模型管理表中的 model_id */
    @Excel(name = "关联模型管理表中的 model_id")
    private Long keyModelId;

    /** 备用字段1 */
    @Excel(name = "备用字段1")
    private String keyA;

    /** 备用字段2 */
    @Excel(name = "备用字段2")
    private String keyB;

    /** 备用字段3 */
    @Excel(name = "备用字段3")
    private String keyC;

    /** 备用字段4 */
    @Excel(name = "备用字段4")
    private String keyD;

    /** 备用字段5 */
    @Excel(name = "备用字段5")
    private String keyE;

    /** 备用字段6 */
    @Excel(name = "备用字段6")
    private String keyF;

    /** 备用字段7 */
    @Excel(name = "备用字段7")
    private String keyG;

    /** 备用字段8 */
    @Excel(name = "备用字段8")
    private String keyH;

    /** 备用字段9 */
    @Excel(name = "备用字段9")
    private String keyI;

    /** 备用字段10 */
    @Excel(name = "备用字段10")
    private String keyJ;

    public void setKeyId(Long keyId) 
    {
        this.keyId = keyId;
    }

    public Long getKeyId() 
    {
        return keyId;
    }

    public void setKeyName(String keyName) 
    {
        this.keyName = keyName;
    }

    public String getKeyName() 
    {
        return keyName;
    }

    public void setKeyProviderName(String keyProviderName) 
    {
        this.keyProviderName = keyProviderName;
    }

    public String getKeyProviderName() 
    {
        return keyProviderName;
    }

    public void setKeyApiKey(String keyApiKey) 
    {
        this.keyApiKey = keyApiKey;
    }

    public String getKeyApiKey() 
    {
        return keyApiKey;
    }

    public void setKeySecret(String keySecret) 
    {
        this.keySecret = keySecret;
    }

    public String getKeySecret() 
    {
        return keySecret;
    }

    public void setKeyBaseUrl(String keyBaseUrl) 
    {
        this.keyBaseUrl = keyBaseUrl;
    }

    public String getKeyBaseUrl() 
    {
        return keyBaseUrl;
    }

    public void setKeyModelId(Long keyModelId) 
    {
        this.keyModelId = keyModelId;
    }

    public Long getKeyModelId() 
    {
        return keyModelId;
    }

    public void setKeyA(String keyA) 
    {
        this.keyA = keyA;
    }

    public String getKeyA() 
    {
        return keyA;
    }

    public void setKeyB(String keyB) 
    {
        this.keyB = keyB;
    }

    public String getKeyB() 
    {
        return keyB;
    }

    public void setKeyC(String keyC) 
    {
        this.keyC = keyC;
    }

    public String getKeyC() 
    {
        return keyC;
    }

    public void setKeyD(String keyD) 
    {
        this.keyD = keyD;
    }

    public String getKeyD() 
    {
        return keyD;
    }

    public void setKeyE(String keyE) 
    {
        this.keyE = keyE;
    }

    public String getKeyE() 
    {
        return keyE;
    }

    public void setKeyF(String keyF) 
    {
        this.keyF = keyF;
    }

    public String getKeyF() 
    {
        return keyF;
    }

    public void setKeyG(String keyG) 
    {
        this.keyG = keyG;
    }

    public String getKeyG() 
    {
        return keyG;
    }

    public void setKeyH(String keyH) 
    {
        this.keyH = keyH;
    }

    public String getKeyH() 
    {
        return keyH;
    }

    public void setKeyI(String keyI) 
    {
        this.keyI = keyI;
    }

    public String getKeyI() 
    {
        return keyI;
    }

    public void setKeyJ(String keyJ) 
    {
        this.keyJ = keyJ;
    }

    public String getKeyJ() 
    {
        return keyJ;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("keyId", getKeyId())
            .append("keyName", getKeyName())
            .append("keyProviderName", getKeyProviderName())
            .append("keyApiKey", getKeyApiKey())
            .append("keySecret", getKeySecret())
            .append("keyBaseUrl", getKeyBaseUrl())
            .append("keyModelId", getKeyModelId())
            .append("keyA", getKeyA())
            .append("keyB", getKeyB())
            .append("keyC", getKeyC())
            .append("keyD", getKeyD())
            .append("keyE", getKeyE())
            .append("keyF", getKeyF())
            .append("keyG", getKeyG())
            .append("keyH", getKeyH())
            .append("keyI", getKeyI())
            .append("keyJ", getKeyJ())
            .toString();
    }
}
