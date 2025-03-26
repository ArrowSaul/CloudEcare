package com.sky.service;



import com.sky.dto.PatientAddDTO;
import com.sky.dto.PatientDTO;
import com.sky.entity.Patient;

import java.util.List;

public interface PatientService {
    /**
     * 新增就诊人
     * @param patientAddDTO
     */
    void addPatient(PatientAddDTO patientAddDTO);
    /**
     * 查询就诊人信息列表
     * @return
     */
    List<Patient> list();
    /**
     * 批量删除就诊人
     * @param ids
     */
    void deleteBatch(List<Long> ids);

    /**
     * 编辑就诊人信息
     * @param patientDTO
     */
    void update(PatientDTO patientDTO);
    /**
     * 根据id查询就诊人信息
     * @param id
     * @return
     */
    Patient getPatient(Long id);
}
