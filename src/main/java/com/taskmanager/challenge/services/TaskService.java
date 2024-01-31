package com.taskmanager.challenge.services;

import com.taskmanager.challenge.enums.TaskStatus;
import com.taskmanager.challenge.exceptions.NotFoundException;
import com.taskmanager.challenge.mapper.CountingTaskMapper;
import com.taskmanager.challenge.model.CountingTask;
import com.taskmanager.challenge.model.request.CountingTaskRequest;
import com.taskmanager.challenge.repositories.CountingTaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskService {

    private final CountingTaskRepository countingTaskRepository;

    private final FileService fileService;

    private final CountingTaskMapper taskMapper;

    private final RedisTemplate<String, CountingTask> redisTemplate;


    public List<CountingTask> listTasks() {
        return countingTaskRepository.findAll();
    }

    public CountingTask createTask(CountingTaskRequest countingTaskRequest) {
        CountingTask task = taskMapper.mapRequest(countingTaskRequest);
        return countingTaskRepository.save(task);
    }

    public CountingTask getTask(String taskId) {
        return getLatestTask(taskId);
    }

    public CountingTask updateTask(String taskId, CountingTaskRequest countingTask) {
        CountingTask existing = getLatestTask(taskId);
        CountingTask task = taskMapper.mapRequest(countingTask);
        if(null!= existing){
            existing.setEnd(task.getEnd());
            return countingTaskRepository.save(existing);
        }
       throw new NotFoundException();
    }

    public void deleteTask(String taskId) throws InterruptedException {
        CountingTask existing = getLatestTask(taskId);
        if(null!= existing){
            if(TaskStatus.IN_PROGRESS == existing.getStatus()){
                stopRunningTask(existing);
                TimeUnit.SECONDS.sleep(3);
            }
            countingTaskRepository.deleteById(taskId);
            redisTemplate.delete(taskId);
            log.debug("Task "+existing.getId()+" is deleted successfully");
        }else{
          throw new NotFoundException();
        }
    }

    public void executeTask(String taskId) {
        CountingTask existing = getLatestTask(taskId);
        if(null != existing){
           TaskExecutorService taskService = new TaskExecutorService(fileService,existing,redisTemplate);
           Thread countingThread = new Thread(taskService);
           countingThread.start();
        }
    }

    public void cancelTask(String taskId) {
        CountingTask task = getLatestTask(taskId);

        if (null != task && (TaskStatus.IN_PROGRESS == task.getStatus()
                || TaskStatus.CREATED == task.getStatus())) {
            if(TaskStatus.IN_PROGRESS == task.getStatus()){
                stopRunningTask(task);
            }
            task.setStatus(TaskStatus.CANCELLED);
            countingTaskRepository.save(task);
            log.debug("Task "+task.getId()+" is cancelled");
        }
    }

    private void stopRunningTask(CountingTask task){
        CountingTask runningTask = redisTemplate.opsForValue().get(task.getId());
        runningTask.setExit(true);
        redisTemplate.opsForValue().set(runningTask.getId(),runningTask);
        log.warn("stopping the task "+runningTask.getId()+" , set EXIT : " +runningTask.isExit());
    }

    @Transactional
    public void bulkUpdateProgress(List<CountingTask> tasks){
        countingTaskRepository.saveAll(tasks);
    }

    @Transactional
    public void deleteUnexecutedTasks(TaskStatus status, LocalDate createdDate){
        countingTaskRepository.deleteByStatusAndCreatedDateLessThan(status,createdDate);
    }

    private Optional<CountingTask> findTask(String taskId) {
        return countingTaskRepository.findById(taskId);
    }

    private CountingTask getLatestTask(String taskId){
        CountingTask task;
        if(Boolean.TRUE.equals(redisTemplate.hasKey(taskId))){
            task =  redisTemplate.opsForValue().get(taskId);
        }else{
            Optional<CountingTask> tempTask = findTask(taskId);
            task = tempTask.orElse(null);
        }
        return task;
    }

}
