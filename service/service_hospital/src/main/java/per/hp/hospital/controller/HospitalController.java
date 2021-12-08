package per.hp.hospital.controller;

import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import per.hp.hospital.common.result.Result;
import per.hp.hospital.model.hosp.Hospital;
import per.hp.hospital.service.HospitalService;
import per.hp.hospital.vo.hosp.HospitalQueryVo;

@RestController
@RequestMapping("/admin/hosp/hospital")
public class HospitalController {
    @Autowired
    HospitalService hospitalService;

    // 医院列表
    @GetMapping("list/{page}/{limit}")
    public Result list(@PathVariable int page, @PathVariable int limit, HospitalQueryVo hospitalQueryVo) {
        Page<Hospital> pageModel = hospitalService.getHospitalPage(page, limit, hospitalQueryVo);
        return Result.ok(pageModel);
    }

    // 更新医院的上线状态
    @GetMapping("updateStatus/{id}/{status}")
    public Result lock(
            @PathVariable("id") String id,
            @PathVariable("status") Integer status){
        hospitalService.updateStatus(id, status);
        return Result.ok();
    }

    @GetMapping("show/{id}")
    public Result show(
            @ApiParam(name = "id", value = "医院id", required = true)
            @PathVariable String id) {
        return Result.ok(hospitalService.show(id));
    }



}
