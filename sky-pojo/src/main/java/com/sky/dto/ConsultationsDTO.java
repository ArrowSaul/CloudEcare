package com.sky.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "咨询记录新增时传递的数据模型")
public class ConsultationsDTO {

    @ApiModelProperty("就诊人id")
    private Long patientId;

    @ApiModelProperty("是否手术")
    private Integer isSurgery;

    @ApiModelProperty("是否发烧")
    private Integer hasFever;

    @ApiModelProperty("疾病类型")
    private String diseaseType;

    @ApiModelProperty("疾病描述")
    private String symptoms;

    @ApiModelProperty("图片")
    private String image;
}