package com.taskmanager.challenge.services;

import com.taskmanager.challenge.enums.TaskStatus;
import com.taskmanager.challenge.constants.AppConstants;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;


@Service
@EnableScheduling
@AllArgsConstructor
@Slf4j
public class CleanupService {

    private final TaskService taskService;

    @Async("asyncTaskExecutor")
    @Scheduled(cron = "0 0 0 * * 0")
    public void cleanupTasks(){
       taskService.deleteUnexecutedTasks(TaskStatus.CREATED, LocalDate.now().minusDays(AppConstants.CLEANUP_DAYS));
       log.debug("Cleanup completed : "+Thread.currentThread().getName());
    }
}
