package com.taskmanager.challenge.model;

import com.taskmanager.challenge.enums.TaskStatus;
import com.taskmanager.challenge.enums.TaskType;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalDate;

@Entity
@Data
public class CountingTask {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;

    private TaskType name;

    private Integer start;

    private Integer end;

    private Integer currCount;

    private LocalDate createdDate;

    private TaskStatus status;

    private boolean exit;

    private String storageLocation;

}
