package com.example.mentorselection.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {
    public static final int ADMIN = 10;

    public static final int TEACHER = 5;

    public static final int STUDENT = 0;

    @Id
    @Column("id")
    @CreatedBy
    private Long id;

    private String name;

    private String number;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer total;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer count;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Integer role;

    private Long teacherId;

    private String teacherName;

    private LocalDateTime insertTime;

    private LocalDateTime selectTime;
}
