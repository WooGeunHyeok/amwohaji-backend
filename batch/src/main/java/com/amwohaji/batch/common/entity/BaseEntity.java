package com.amwohaji.batch.common.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@SuperBuilder
public class BaseEntity implements Serializable {

    private LocalDateTime insdate;
    private String insid;
    private LocalDateTime upddate;
    private String updid;
}
