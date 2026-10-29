package com.ruoyi.binZhou.sxz.service.client;

/**
 * 模型调用异常，保留实际尝试次数和异常是否具备重试价值。
 */
public final class DiseaseModelCallException extends RuntimeException
{
    private static final long serialVersionUID = 1L;

    private final int attemptCount;
    private final boolean retryable;

    public DiseaseModelCallException(String message, int attemptCount, boolean retryable, Throwable cause)
    {
        super(message, cause);
        this.attemptCount = attemptCount;
        this.retryable = retryable;
    }

    public int getAttemptCount()
    {
        return attemptCount;
    }

    public boolean isRetryable()
    {
        return retryable;
    }
}
