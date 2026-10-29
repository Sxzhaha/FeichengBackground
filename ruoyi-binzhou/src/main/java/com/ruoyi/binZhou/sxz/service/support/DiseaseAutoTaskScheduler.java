package com.ruoyi.binZhou.sxz.service.support;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import com.ruoyi.binZhou.sxz.service.IDiseaseAutoTaskService;

@Component
public class DiseaseAutoTaskScheduler
{
    private static final Logger log = LoggerFactory.getLogger(DiseaseAutoTaskScheduler.class);
    private final AtomicBoolean running = new AtomicBoolean(false);
    private final IDiseaseAutoTaskService service;
    private ScheduledExecutorService executor;

    public DiseaseAutoTaskScheduler(IDiseaseAutoTaskService service)
    {
        this.service = service;
    }

    @PostConstruct
    public void start()
    {
        ThreadFactory factory = task ->
        {
            Thread thread = new Thread(task, "sxz-disease-auto-scan");
            thread.setDaemon(true);
            thread.setUncaughtExceptionHandler((current, error) ->
                log.error("自动识别调度线程异常，thread={}", current.getName(), error));
            return thread;
        };
        executor = Executors.newSingleThreadScheduledExecutor(factory);
        executor.scheduleWithFixedDelay(this::scanDueTasks, 30, 60, TimeUnit.SECONDS);
    }

    public void scanDueTasks()
    {
        if (!running.compareAndSet(false, true))
        {
            return;
        }
        try
        {
            service.executeDueTasks();
        }
        catch (Exception e)
        {
            log.warn("病虫害自动识别任务扫描失败，原因={}", e.getMessage(), e);
        }
        finally
        {
            running.set(false);
        }
    }

    @PreDestroy
    public void stop()
    {
        if (executor != null)
        {
            executor.shutdownNow();
        }
    }
}
