package com.example.entity;

import lombok.*;
import org.springframework.data.annotation.Id;

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
    private Long id;

    private String name;

    private String number;

    private String password;

    private Integer total;

    private Integer count;

    private Integer role;

    private Long teacherId;

    private String teacherName;

    private LocalDateTime insertTime;

    private LocalDateTime selectTime;
}
