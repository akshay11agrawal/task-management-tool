package com.taskmanager.challenge.mapper;

import com.taskmanager.challenge.enums.TaskStatus;
import com.taskmanager.challenge.enums.TaskType;
import com.taskmanager.challenge.model.CountingTask;
import com.taskmanager.challenge.model.request.CountingTaskRequest;
import com.taskmanager.challenge.model.response.CountingTaskResponse;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class CountingTaskMapper {

    public CountingTask mapRequest(CountingTaskRequest taskRequest){
        CountingTask taskEntity = new CountingTask();
        taskEntity.setName(TaskType.COUNTING_TASK);
        taskEntity.setStart(taskRequest.getStart());
        taskEntity.setEnd(taskRequest.getEnd());
        taskEntity.setStatus(TaskStatus.CREATED);
        taskEntity.setCreatedDate(LocalDate.now());
        return taskEntity;
    }

    public CountingTaskResponse mapResponse(CountingTask savedTask){
        CountingTaskResponse taskResponse = new CountingTaskResponse();
        if(null!= savedTask){
            taskResponse.setId(savedTask.getId());
            taskResponse.setName(String.valueOf(savedTask.getName()));
            taskResponse.setStart(savedTask.getStart());
            taskResponse.setEnd(savedTask.getEnd());
            taskResponse.setStatus(String.valueOf(savedTask.getStatus()));
            taskResponse.setCreatedDate(savedTask.getCreatedDate());
            taskResponse.setProgress(savedTask.getCurrCount());
        }
        return taskResponse;
    }
}
