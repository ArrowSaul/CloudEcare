package com.sky.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Consultations {

    /**
     * 病例的唯一标识符
     */
    private Long id;

    /**
     * 患者的唯一标识符，用于关联患者信息
     */
    private Long patientId;

    /**
     * 是否手术，表示患者是否进行了手术 0-是 1-否
     */
    private Integer isSurgery;

    /**
     * 是否发烧，表示患者是否发烧 0-是 1-否
     */
    private Integer hasFever;

    /**
     * 疾病类型，描述患者的疾病类别
     */
    private String diseaseType;

    /**
     * 症状描述，详细记录患者的症状
     */
    private String symptoms;

    /**
     * 图像URL，存储患者的医学影像资料地址
     */
    private String image;

    /**
     * 创建时间，记录病例创建的时间
     */
    private LocalDateTime createTime;
}