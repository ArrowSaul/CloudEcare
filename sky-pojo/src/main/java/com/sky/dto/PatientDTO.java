package com.sky.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class PatientDTO implements Serializable {
    /**
     * 序列化版本唯一标识符
     * 用于在序列化和反序列化过程中确保类的版本一致性和兼容性
     */
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     * 用于唯一标识每个对象的主键
     */
    private Long id;

    /**
     * 名称
     * 对象的名称，如用户的姓名或地址的名称
     */
    private String name;

    /**
     * 电话号码
     * 对象的联系电话，用于联系和通信
     */
    private String phone;

    /**
     * 性别
     * 对象的性别，0表示未知，1表示男性，2表示女性
     */
    private Integer sex;

    /**
     * 定义年龄属性
     * 使用Integer包装类型而非int基本类型，以便支持null值，表示未指定或未知的年龄
     * 私有访问级别确保了属性的封装性，外部类无法直接访问，必须通过getter和setter方法进行操作
     */
    private Integer age;

    /**
     * 身份证号码
     * 对象的身份证号，用于身份验证和信息加密
     */
    private String idNumber;


    /**
     * 医保卡号
     * 用于唯一标识一个医保用户
     */
    private String medicalCardNumber;
}
