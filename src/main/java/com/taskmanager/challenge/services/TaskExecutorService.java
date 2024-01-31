package com.taskmanager.challenge.services;

import com.taskmanager.challenge.enums.TaskStatus;
import com.taskmanager.challenge.model.CountingTask;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Slf4j
public class TaskExecutorService implements Runnable{

    private final FileService fileService;

    private final CountingTask countingTask;

    private final RedisTemplate<String, CountingTask> redisTemplate;

    @Override
    public void run() {
        ExecutorService countingExecutor = Executors.newSingleThreadExecutor();

        countingExecutor.execute(()-> {
            try {
                countingTask.setStatus(TaskStatus.IN_PROGRESS);
                for (int i = countingTask.getStart(); i <= countingTask.getEnd(); i++) {
                    try {
                        // if task is cancelled, interrupt the thread
                        if (null != getCurrentTask(countingTask) && getCurrentTask(countingTask).isExit()) {
                            CountingTask currentTask = getCurrentTask(countingTask);
                            currentTask.setStatus(TaskStatus.CANCELLED);
                            setTaskProgress(currentTask);
                            Thread.currentThread().interrupt();
                        }else{
                        // thread still running, continue with loop
                            countingTask.setCurrCount(i);
                            setTaskProgress(countingTask);
                            TimeUnit.SECONDS.sleep(1);
                        }
                    } catch (InterruptedException e) {
                        log.warn("Running task with ID"+countingTask.getId()+" was interrupted ");
                        Thread.currentThread().interrupt();
                    }
                }
                completeTask(countingTask);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                countingExecutor.shutdown();
            }
        });
    }

    private void setTaskProgress(CountingTask countingTask){
        log.debug("Updating count "+countingTask.getCurrCount()+" for taskID : "+countingTask.getId()+ " to redis");
        String key = countingTask.getId();
        redisTemplate.opsForValue().set(key,countingTask);
    }

    private CountingTask getCurrentTask(CountingTask task){
        return redisTemplate.opsForValue().get(task.getId());
    }

    private void completeTask(CountingTask countingTask) throws IOException {
        countingTask.setStatus(TaskStatus.COMPLETED);
        setTaskProgress(countingTask);
        fileService.storeResult(countingTask.getId());
        log.debug("Task completed, taskID : "+countingTask.getId());
    }
}
