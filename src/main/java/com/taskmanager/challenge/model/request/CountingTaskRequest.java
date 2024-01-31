package com.taskmanager.challenge.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CountingTaskRequest{
    private Integer start;
    private Integer end;

}
