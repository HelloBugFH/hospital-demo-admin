package per.hp.hospital.service.impl;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import per.hp.cmn.client.DictFeignClient;
import per.hp.hospital.model.hosp.BookingRule;
import per.hp.hospital.model.hosp.Hospital;
import per.hp.hospital.repository.HospitalRepository;
import per.hp.hospital.service.HospitalService;
import per.hp.hospital.vo.hosp.HospitalQueryVo;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class HospitalServiceImpl implements HospitalService {
    @Autowired
    private HospitalRepository hospitalRepository;

    @Autowired
    private DictFeignClient dictFeignClient;

    @Override
    public void updateStatus(String id, Integer status) {
        if(status.intValue() == 0 || status.intValue() == 1) {
            Hospital hospital = hospitalRepository.findById(id).get();
            hospital.setStatus(status);
            hospital.setUpdateTime(new Date());
            hospitalRepository.save(hospital);
        }
    }

    @Override
    public Map<String, Object> show(String id) {
        HashMap<String, Object> resultMap = new HashMap<>();
        Hospital hospital = hospitalRepository.findById(id).get();
        // 封装医院等级和详细地址
        Hospital hospital_pack = this.setHospitalType(hospital);
        resultMap.put("hospital",hospital_pack);
        BookingRule bookingRule = hospital_pack.getBookingRule();
        resultMap.put("bookingRule",bookingRule);
        return resultMap;
    }



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

    @Override
    public Hospital getHospitalByCode(String hoscode) {
        Hospital hospital = hospitalRepository.getHospitalByHoscode(hoscode);
        return hospital;
    }

    @Override
    public Page<Hospital> getHospitalPage(int page, int limit, HospitalQueryVo hospitalQueryVo) {
        Pageable pageable = PageRequest.of(page-1,limit);
        // 条件匹配器
        ExampleMatcher exampleMatcher = ExampleMatcher.matching()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                .withIgnoreCase(true);
        Hospital hospital = new Hospital();
        BeanUtils.copyProperties(hospitalQueryVo,hospital);

        Example<Hospital> example = Example.of(hospital,exampleMatcher);

        Page<Hospital> pages = hospitalRepository.findAll(example, pageable);

        // 进行医院等级封装
        List<Hospital> hospitalList = pages.getContent();
        hospitalList.stream().forEach(item->{
            this.setHospitalType(item);
        });
        return pages;
    }

    private Hospital setHospitalType(Hospital hospital) {
        // 查询医院类型
        String hostypeString = dictFeignClient.getName("Hostype", hospital.getHostype());
        // 查询省市
        String provinceString = dictFeignClient.getName(hospital.getProvinceCode());
        String cityString = dictFeignClient.getName(hospital.getCityCode());
        String districtString = dictFeignClient.getName(hospital.getDistrictCode());

        hospital.getParam().put("hostypeString",hostypeString);
        hospital.getParam().put("fullAddress",provinceString+cityString+districtString);
        return hospital;
    }
}
