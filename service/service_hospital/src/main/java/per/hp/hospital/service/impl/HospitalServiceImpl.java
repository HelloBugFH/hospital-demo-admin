package per.hp.hospital.service.impl;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import per.hp.hospital.model.hosp.Hospital;
import per.hp.hospital.repository.HospitalRepository;
import per.hp.hospital.service.HospitalService;

import java.util.Date;
import java.util.Map;

@Service
public class HospitalServiceImpl implements HospitalService {
    @Autowired
    HospitalRepository hospitalRepository;


    @Override
    public void save(Map<String, Object> paramMap) {
        // 将map转成对象
        String mapString = JSONObject.toJSONString(paramMap);
        Hospital hospital = JSONObject.parseObject(mapString, Hospital.class);

        // 判断数据是否存在
        String hoscode = hospital.getHoscode();
        Hospital hospital_inDB = hospitalRepository.getHospitalByHoscode(hoscode);
        // 数据库中存在数据执行修改
        if (hospital_inDB != null) {
            hospital.setStatus(hospital_inDB.getStatus());
            hospital.setCreateTime(hospital_inDB.getCreateTime());
            hospital.setUpdateTime(new Date());
            hospital.setIsDeleted(0);
            hospitalRepository.save(hospital);
        }
        // 数据库中不存在执行添加
        else {
            hospital.setStatus(0);
            hospital.setCreateTime(new Date());
            hospital.setUpdateTime(new Date());
            hospital.setIsDeleted(0);
            hospitalRepository.save(hospital);
        }
    }
}
