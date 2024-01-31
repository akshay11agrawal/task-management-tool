package com.taskmanager.challenge.controllers;

import com.taskmanager.challenge.mapper.CountingTaskMapper;
import com.taskmanager.challenge.model.CountingTask;
import com.taskmanager.challenge.model.request.CountingTaskRequest;
import com.taskmanager.challenge.model.response.CountingTaskResponse;
import com.taskmanager.challenge.services.FileService;
import com.taskmanager.challenge.services.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    private final FileService fileService;

    private final CountingTaskMapper taskMapper;

    @GetMapping("/")
    public ResponseEntity<List<CountingTaskResponse>> listTasks() {
        List<CountingTask> taskList =  taskService.listTasks();
        List<CountingTaskResponse> responseList = taskList.stream().map(taskMapper::mapResponse)
                .collect(Collectors.toList());
        return new ResponseEntity<>(responseList, HttpStatus.OK);
    }

    @PostMapping("/")
    public ResponseEntity<CountingTaskResponse> createTask(@RequestBody @Valid CountingTaskRequest taskRequest) {
        CountingTask createdTask = taskService.createTask(taskRequest);
        if(null==createdTask){
            return new ResponseEntity<>(CountingTaskResponse.builder().errorMessage("Internal server error occurred")
                    .build(),HttpStatus.INTERNAL_SERVER_ERROR);
        }

        CountingTaskResponse response =  taskMapper.mapResponse(createdTask);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{taskId}")
    public ResponseEntity<CountingTaskResponse> getTask(@PathVariable String taskId) {
        CountingTask fetchedTask = taskService.getTask(taskId);
        if(null==fetchedTask){
            return new ResponseEntity<>(CountingTaskResponse.builder().errorMessage("Given resource is not found")
                    .build(),HttpStatus.NOT_FOUND);
        }

        CountingTaskResponse response =  taskMapper.mapResponse(fetchedTask);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/{taskId}")
    public ResponseEntity<CountingTaskResponse> updateTask(@PathVariable String taskId,
                                   @RequestBody @Valid CountingTaskRequest countingTask) {
        CountingTask updatedTask = taskService.updateTask(taskId, countingTask);
        CountingTaskResponse response = taskMapper.mapResponse(updatedTask);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<CountingTaskResponse> deleteTask(@PathVariable String taskId) throws InterruptedException {
        taskService.deleteTask(taskId);
       return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/{taskId}/execute")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void executeTask(@PathVariable String taskId) {
        taskService.executeTask(taskId);
    }

    @GetMapping("/{taskId}/result")
    public ResponseEntity<?> getResult(@PathVariable String taskId){
        return fileService.getTaskResult(taskId);
    }

    @GetMapping("/{taskId}/cancel")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancelTask(@PathVariable String taskId) {
        taskService.cancelTask(taskId);
    }

}
