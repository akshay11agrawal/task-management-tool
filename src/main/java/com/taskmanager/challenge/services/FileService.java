package com.taskmanager.challenge.services;

import com.taskmanager.challenge.exceptions.InternalException;
import com.taskmanager.challenge.exceptions.NotFoundException;
import com.taskmanager.challenge.model.CountingTask;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
@Slf4j
public class FileService {

    private final RedisTemplate<String, CountingTask> redisTemplate;

    public ResponseEntity<?> getTaskResult(String taskId) {
        CountingTask countingTask = getTask(taskId);
        if(null != countingTask && null != countingTask.getStorageLocation()) {
            File inputFile = new File(countingTask.getStorageLocation());
            if (!inputFile.exists()) {
                throw new InternalException("File not generated yet");
            }

            return new ResponseEntity<>(inputFile, HttpStatus.OK);
        }
        throw new NotFoundException();
    }

    public void storeResult(String taskId) throws IOException {
        CountingTask countingTask = getTask(taskId);
        File outputFile = new File(taskId+ ".txt");
        FileUtils.writeStringToFile(outputFile, "Task Type : "+ countingTask.getName()
                + " Start : "+countingTask.getStart()+" End : "+countingTask.getEnd(), StandardCharsets.UTF_8);
        countingTask.setStorageLocation(outputFile.getAbsolutePath());
        setTaskStorageLocation(countingTask);
        log.debug("Storage location updated for taskID "+taskId);
    }

    public CountingTask getTask(String taskId) {
        return redisTemplate.opsForValue().get(taskId);
    }

    private void setTaskStorageLocation(CountingTask countingTask){
        String key = countingTask.getId();
        redisTemplate.opsForValue().set(key,countingTask);
    }
}
