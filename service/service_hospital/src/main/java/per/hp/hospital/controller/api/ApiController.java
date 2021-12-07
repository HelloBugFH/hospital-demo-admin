package per.hp.hospital.controller.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import per.hp.hospital.common.MD5;
import per.hp.hospital.common.exception.HospitalException;
import per.hp.hospital.common.helper.HttpRequestHelper;
import per.hp.hospital.common.result.Result;
import per.hp.hospital.common.result.ResultCodeEnum;
import per.hp.hospital.service.HospitalService;
import per.hp.hospital.service.HospitalSetService;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("/api/hosp")
public class ApiController {
    @Autowired
    HospitalService hospitalService;

    @Autowired
    HospitalSetService setService;



    // 上传医院接口
    @PostMapping("saveHospital")
    public Result saveHospital(HttpServletRequest request) {
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

        // base64编码导致的“+”变成“ ”的问题解决
        String logoData = (String) paramMap.get("logoData");
        String logoData_replace = logoData.replaceAll(" ", "+");
        paramMap.put("logoData", logoData_replace);

        // 调用service加入数据库
        hospitalService.save(paramMap);
        return Result.ok();
    }
}
