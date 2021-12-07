package per.hp.hospital.service;

import per.hp.hospital.model.hosp.Hospital;

import java.util.Map;

public interface HospitalService {
    void save(Map<String, Object> paramMap);

    Hospital getHospitalByCode(String hoscode);
}
