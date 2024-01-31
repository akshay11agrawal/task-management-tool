package com.taskmanager.challenge.repositories;

import com.taskmanager.challenge.enums.TaskStatus;
import com.taskmanager.challenge.model.CountingTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface CountingTaskRepository extends JpaRepository<CountingTask, String> {

    void deleteByStatusAndCreatedDateLessThan(TaskStatus status, LocalDate createdDate);

}
