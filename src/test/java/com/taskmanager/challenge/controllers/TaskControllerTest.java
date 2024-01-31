package com.taskmanager.challenge.controllers;

import com.taskmanager.challenge.enums.TaskStatus;
import com.taskmanager.challenge.enums.TaskType;
import com.taskmanager.challenge.mapper.CountingTaskMapper;
import com.taskmanager.challenge.model.CountingTask;
import com.taskmanager.challenge.model.request.CountingTaskRequest;
import com.taskmanager.challenge.model.response.CountingTaskResponse;
import com.taskmanager.challenge.services.FileService;
import com.taskmanager.challenge.services.TaskService;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;

import static java.util.Objects.requireNonNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(MockitoJUnitRunner.class)
public class TaskControllerTest {

    private MockMvc mockMvc;

    @Mock
    private TaskService taskService;

    @Mock
    private FileService fileService;

    @Mock
    private CountingTaskMapper taskMapper;

    @InjectMocks
    private TaskController taskController;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(taskController).build();
    }

    @Test
    public void testListTasks() throws Exception {

        List<CountingTask> mockTaskList = List.of(getSampleCountingTask1(),getSampleCountingTask2());
        when(taskService.listTasks()).thenReturn(mockTaskList);

        when(taskMapper.mapResponse(any(CountingTask.class))).thenReturn(getSampleCountingTaskResponse
                (getSampleCountingTask1()),getSampleCountingTaskResponse(getSampleCountingTask2()));

        mockMvc.perform(get("/api/tasks/"))
                .andExpect(status().isOk())
                .andExpect(content().string(expectedResponse("task-list-all.json")));
    }

    @Test
    public void testCreateTask() throws Exception {

        when(taskService.createTask(Mockito.any(CountingTaskRequest.class))).thenReturn(getSampleCountingTask1());

        when(taskMapper.mapResponse(Mockito.any())).thenReturn
                (getSampleCountingTaskResponse(getSampleCountingTask1()));

        mockMvc.perform(post("/api/tasks/")
                        .content("{\"start\":\"1\",\"end\":\"90\"}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(expectedResponse("task-create.json")));

    }

    @Test
    public void testGetTask() throws Exception {
        when(taskService.getTask(Mockito.any())).thenReturn(getSampleCountingTask1());

        when(taskMapper.mapResponse(any(CountingTask.class))).thenReturn(getSampleCountingTaskResponse
                (getSampleCountingTask1()));

        mockMvc.perform(get("/api/tasks/{taskId}", "45bc222a-b338-4359-b99b-0a185b48b7ab"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(expectedResponse("task-get-id.json")));
    }

    @Test
    public void testUpdateTask() throws Exception {
        CountingTask countingTask = getSampleCountingTask1();
        countingTask.setEnd(95);
        when(taskService.updateTask(Mockito.any(), Mockito.any())).thenReturn(countingTask);

        when(taskMapper.mapResponse(Mockito.any())).thenReturn(getSampleCountingTaskResponse(countingTask));

        mockMvc.perform(put("/api/tasks/{taskId}", "45bc222a-b338-4359-b99b-0a185b48b7ab")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"end\":\"95\"}"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(expectedResponse("task-update.json")));
    }

    @Test
    public void testDeleteTask() throws Exception {
        mockMvc.perform(delete("/api/tasks/45bc222a-b338-4359-b99b-0a185b48b7ab"))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testExecuteTask() throws Exception {
        mockMvc.perform(post("/api/tasks/45bc222a-b338-4359-b99b-0a185b48b7ab/execute"))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testCancelTask() throws Exception {
        mockMvc.perform(get("/api/tasks/45bc222a-b338-4359-b99b-0a185b48b7ab/cancel"))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testGetResult() throws Exception {
        File tempFile = File.createTempFile("temp", ".txt");
        doReturn(ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "form-data; name=\"attachment\"; filename=\""
                        + tempFile.getName() + "\"")
                .body(new FileSystemResource(tempFile)))
                .when(fileService).getTaskResult("45bc222a-b338-4359-b99b-0a185b48b7ab");

        mockMvc.perform(get("/api/tasks/45bc222a-b338-4359-b99b-0a185b48b7ab/result"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_OCTET_STREAM))
                .andExpect(header().stringValues("Content-Disposition",
                        "form-data; name=\"attachment\"; filename=\"" + tempFile.getName() + "\""));
    }

    private CountingTask getSampleCountingTask1(){
        CountingTask task = new CountingTask();
        task.setId("45bc222a-b338-4359-b99b-0a185b48b7ab");
        task.setName(TaskType.COUNTING_TASK);
        task.setStart(1);
        task.setEnd(90);
        task.setStatus(TaskStatus.CREATED);
        task.setCreatedDate(LocalDate.of(2024,1,28));
        return task;
    }
    private CountingTask getSampleCountingTask2(){
        CountingTask task = new CountingTask();
        task.setId("45bc222a-b338-4359-b99b-0a185b48b8bc");
        task.setName(TaskType.COUNTING_TASK);
        task.setStart(1);
        task.setEnd(50);
        task.setStatus(TaskStatus.CREATED);
        task.setCreatedDate(LocalDate.of(2024,1,28));
        return task;
    }

    private CountingTaskResponse getSampleCountingTaskResponse(CountingTask countingTask){
        CountingTaskMapper mapper = new CountingTaskMapper();
        return mapper.mapResponse(countingTask);
    }

    private String expectedResponse(String file) throws IOException {
        String resourcePath = "response/"+file;
        return IOUtils.toString(requireNonNull(getClass().getClassLoader().getResource(resourcePath)),
                StandardCharsets.UTF_8);
    }

}

