package com.sky.controller.user;
import com.sky.dto.PatientAddDTO;
import com.sky.dto.PatientDTO;
import com.sky.entity.Patient;
import com.sky.result.Result;
import com.sky.service.PatientService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/patient")
@Slf4j
@Api(tags = "C端就诊人相关接口")
public class PatientController {
    @Autowired
    private PatientService patientService;
    /**
     * 新增就诊人
     * @param patientAddDTO
     */
    @PostMapping
    @ApiOperation("新增就诊人")
    public Result addPatient(@RequestBody PatientAddDTO patientAddDTO) {
        log.info("新增就诊人：{}", patientAddDTO);
        patientService.addPatient(patientAddDTO);
        return Result.success();
    }

    /**
     * 查询就诊人信息列表
     * @return
     */
    @GetMapping("/list")
    @ApiOperation("查询就诊人信息列表")
    public Result<List<Patient>> list() {
        log.info("查询就诊人信息列表");
        List<Patient> list = patientService.list();
        return Result.success(list);
    }

    /**
     * 就诊人批量删除
     * @param ids
     * @return
     */
    @DeleteMapping
    @ApiOperation("就诊人批量删除")
    public Result delete(@RequestParam List<Long> ids) {
        log.info("就诊人批量删除:{}", ids);
        patientService.deleteBatch(ids);
        return Result.success();
    }

    /**
     * 编辑就诊人信息
     * @param patientDTO
     * @return
     */
    @PutMapping
    @ApiOperation("编辑就诊人信息")
    public Result update(@RequestBody PatientDTO patientDTO){
        log.info("编辑就诊人信息，{}",patientDTO);
        patientService.update(patientDTO);
        return Result.success();
    }
    /**
     * 根据id查询就诊人信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    @ApiOperation("根据id查询就诊人信息")
    public Result getPatient(@PathVariable Long id){
        log.info("查询就诊人信息，{}",id);
        Patient patient = patientService.getPatient(id);
        return Result.success(patient);
    }
}
