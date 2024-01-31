package com.taskmanager.challenge.model.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public class CountingTaskResponse {

    private String id;

    private Integer start;

    private Integer end;

    private String name;

    private String status;

    private Integer progress;

    @JsonProperty("created_date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate createdDate;

    @JsonProperty("error_message")
    private String errorMessage;

}
