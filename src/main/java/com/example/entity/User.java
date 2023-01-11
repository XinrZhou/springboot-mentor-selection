package com.example.entity;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;

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

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long teacherId;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String teacherName;

    private LocalDateTime insertTime;

    private LocalDateTime selectTime;
}
