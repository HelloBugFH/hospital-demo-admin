package per.hp.hospital.controller.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import per.hp.hospital.common.MD5;
import per.hp.hospital.common.exception.HospitalException;
import per.hp.hospital.common.helper.HttpRequestHelper;
import per.hp.hospital.common.result.Result;
import per.hp.hospital.common.result.ResultCodeEnum;
import per.hp.hospital.model.hosp.Department;
import per.hp.hospital.model.hosp.Hospital;
import per.hp.hospital.service.DepartmentService;
import per.hp.hospital.service.HospitalService;
import per.hp.hospital.service.HospitalSetService;
import per.hp.hospital.vo.hosp.DepartmentQueryVo;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("/api/hosp")
public class ApiController {
    @Autowired
    HospitalService hospitalService;

    @Autowired
    HospitalSetService setService;

    @Autowired
    DepartmentService departmentService;

    @PostMapping("department/remove")
    public Result removeDepartment(HttpServletRequest request) {
        Map paramMap = checkSign(request);
        String hoscode = (String) paramMap.get("hoscode");
        String depcode = (String) paramMap.get("depcode");
        departmentService.remove(hoscode,depcode);
        return Result.ok();
    }


    // 查询科室接口
    @PostMapping("department/list")
    public Result department(HttpServletRequest request) {
        Map paramMap = checkSign(request);

        String hoscode = (String) paramMap.get("hoscode");
        int page = StringUtils.isEmpty(paramMap.get("page")) ? 1 : Integer.parseInt((String) paramMap.get("page"));
        int limit = StringUtils.isEmpty(paramMap.get("limit")) ? 1 : Integer.parseInt((String) paramMap.get("limit"));

        DepartmentQueryVo departmentQueryVo = new DepartmentQueryVo();
        departmentQueryVo.setHoscode(hoscode);
        Page<Department> departmentPage = departmentService.selectPage(page, limit, departmentQueryVo);

        return Result.ok(departmentPage);
    }


    // 上传科室
    @PostMapping("saveDepartment")
    public Result saveDepartment(HttpServletRequest request) {
        Map paramMap = checkSign(request);
        departmentService.save(paramMap);
        return Result.ok();
    }

    // 查询医院
    @PostMapping("hospital/show")
    public Result showHospital(HttpServletRequest request) {
        Map paramMap = checkSign(request);
        String hoscode = (String) paramMap.get("hoscode");
        Hospital hospital = hospitalService.getHospitalByCode(hoscode);
        return Result.ok(hospital);
    }

    // 上传医院接口
    @PostMapping("saveHospital")
    public Result saveHospital(HttpServletRequest request) {
        // 获取参数并检验sign
        Map paramMap = checkSign(request);

        // base64编码导致的“+”变成“ ”的问题解决
        String logoData = (String) paramMap.get("logoData");
        String logoData_replace = logoData.replaceAll(" ", "+");
        paramMap.put("logoData", logoData_replace);

        // 调用service加入数据库
        hospitalService.save(paramMap);
        return Result.ok();
    }

    // 获取request中的参数，并且检验sign
    private Map checkSign(HttpServletRequest request) {
        // 获取传递的医院信息
        Map<String, String[]> map = request.getParameterMap();
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(map);


        // 获取医院传递过来的签名
        String sign = (String) paramMap.get("sign");

        // 根据医院代码查询数据库中的签名
        String hoscode = (String) paramMap.get("hoscode");
        String sign_inDB = setService.getSignKeyByHoscode(hoscode);
        // 将数据库的签名进行加密
        String sign_inDB_encrypt = MD5.encrypt(sign_inDB);

        // 比对
        if (!sign.equals(sign_inDB_encrypt)) {
            throw new HospitalException(ResultCodeEnum.SIGN_ERROR);
        }
        return paramMap;
    }
}
