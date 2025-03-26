package com.sky.dto;

import lombok.Data;

@Data
public class PatientAddDTO {

    // 姓名字段，确保不为空
    private String name;

    // 手机号字段，确保不为空且符合中国大陆手机号格式
    private String phone;

    // 性别字段，确保不为空且在允许的范围内（0-2）
    private Integer sex;

    // 年龄字段，确保不为空
    private Integer age;

    // 身份证号字段，确保不为空且符合中国大陆身份证号格式
    private String idNumber;

    // 医保卡号字段，允许为空
    private String medicalCardNumber;

}