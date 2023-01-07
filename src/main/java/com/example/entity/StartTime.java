package com.example.entity;

import lombok.*;

import java.time.LocalDateTime;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StartTime {

    private LocalDateTime startTime;
}
