package com.ruoyi.binZhou.sxz.service.client;

/**
 * 模型调用结果，包含最终响应和实际尝试次数。
 */
public final class DiseaseModelCallResult
{
    private final String responseBody;
    private final int attemptCount;

    public DiseaseModelCallResult(String responseBody, int attemptCount)
    {
        this.responseBody = responseBody;
        this.attemptCount = attemptCount;
    }

    public String getResponseBody()
    {
        return responseBody;
    }

    public int getAttemptCount()
    {
        return attemptCount;
    }
}
