package com.taskmanager.challenge.services;

import com.taskmanager.challenge.enums.TaskStatus;
import com.taskmanager.challenge.enums.TaskType;
import com.taskmanager.challenge.mapper.CountingTaskMapper;
import com.taskmanager.challenge.model.CountingTask;
import com.taskmanager.challenge.repositories.CountingTaskRepository;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.doReturn;

@RunWith(MockitoJUnitRunner.class)
public class TaskServiceTest {

    @Mock
    private CountingTaskRepository countingTaskRepository;

    @Mock
    private CountingTaskMapper taskMapper;

    @Mock
    private RedisTemplate<String, CountingTask> redisTemplate;

    @InjectMocks
    private TaskService taskService;


    @Test
    public void testListTasks(){
        doReturn(List.of(getSampleCountingTask())).when(countingTaskRepository).findAll();
        List<CountingTask> countingTasks = taskService.listTasks();

        Assertions.assertNotNull(countingTasks);
        Assertions.assertEquals(countingTasks.size(),1);
        Assertions.assertEquals(countingTasks.get(0).getId(),"45bc222a-b338-4359-b99b-0a185b48b7ab");
        Assertions.assertEquals(countingTasks.get(0).getStart(),1);
        Assertions.assertEquals(countingTasks.get(0).getStatus(), TaskStatus.CREATED);
    }

    @Test
    public void testCreateTask(){
        doReturn(getSampleCountingTask()).when(taskMapper).mapRequest(Mockito.any());
        doReturn(getSampleCountingTask()).when(countingTaskRepository).save(Mockito.any());
        CountingTask countingTasks = taskService.createTask(Mockito.any());
        Assertions.assertNotNull(countingTasks);
        Assertions.assertEquals(countingTasks.getStart(),1);
        Assertions.assertEquals(countingTasks.getId(),"45bc222a-b338-4359-b99b-0a185b48b7ab");
        Assertions.assertEquals(countingTasks.getEnd(),90);
        Assertions.assertEquals(countingTasks.getStatus(),TaskStatus.CREATED);
    }

    @Test
    public void testGetTask() {
        doReturn(false).when(redisTemplate).hasKey(Mockito.any());
        doReturn(Optional.of(getSampleCountingTask())).when(countingTaskRepository).findById(Mockito.any());
        CountingTask countingTasks = taskService.getTask(Mockito.any());

        Assertions.assertNotNull(countingTasks);
        Assertions.assertEquals(1, countingTasks.getStart());
        Assertions.assertEquals("45bc222a-b338-4359-b99b-0a185b48b7ab", countingTasks.getId());
        Assertions.assertEquals(90, countingTasks.getEnd());
        Assertions.assertEquals(TaskStatus.CREATED, countingTasks.getStatus());
    }

    @Test
    public void testUpdateTask(){
        doReturn(false).when(redisTemplate).hasKey(Mockito.any());
        doReturn(Optional.of(getSampleCountingTask())).when(countingTaskRepository).findById(Mockito.any());
        doReturn(getSampleCountingTask()).when(taskMapper).mapRequest(Mockito.any());
        doReturn(getSampleCountingTask()).when(countingTaskRepository).save(Mockito.any());
        CountingTask countingTasks = taskService.updateTask("45bc222a-b338-4359-b99b-0a185b48b7ab",Mockito.any());

        Assertions.assertNotNull(countingTasks);
        Assertions.assertEquals(1, countingTasks.getStart());
        Assertions.assertEquals("45bc222a-b338-4359-b99b-0a185b48b7ab", countingTasks.getId());
        Assertions.assertEquals(90, countingTasks.getEnd());
        Assertions.assertEquals(TaskStatus.CREATED, countingTasks.getStatus());
    }

    @Test
    public void testDeleteTask() throws InterruptedException {
        doReturn(false).when(redisTemplate).hasKey(Mockito.any());
        doReturn(Optional.of(getSampleCountingTask())).when(countingTaskRepository).findById(Mockito.any());
        taskService.deleteTask(Mockito.any());
    }

    @Test
    public void testExecuteTask(){
        doReturn(false).when(redisTemplate).hasKey(Mockito.any());
        doReturn(Optional.of(getSampleCountingTask())).when(countingTaskRepository).findById(Mockito.any());
        taskService.executeTask(Mockito.any());
    }

    @Test
    public void testCancelTask(){
        doReturn(false).when(redisTemplate).hasKey(Mockito.any());
        doReturn(Optional.of(getSampleCountingTask())).when(countingTaskRepository).findById(Mockito.any());
        doReturn(getSampleCountingTask()).when(countingTaskRepository).save(Mockito.any());
        taskService.cancelTask(Mockito.any());
    }

    private CountingTask getSampleCountingTask(){
        CountingTask task = new CountingTask();
        task.setId("45bc222a-b338-4359-b99b-0a185b48b7ab");
        task.setName(TaskType.COUNTING_TASK);
        task.setStart(1);
        task.setEnd(90);
        task.setStatus(TaskStatus.CREATED);
        task.setCreatedDate(LocalDate.of(2024,1,28));
        return task;
    }

}
