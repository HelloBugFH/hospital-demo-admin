package per.hp.hospital.service;

import org.springframework.data.domain.Page;
import per.hp.hospital.model.hosp.Hospital;
import per.hp.hospital.vo.hosp.HospitalQueryVo;

import java.util.Map;

public interface HospitalService {
    void save(Map<String, Object> paramMap);

    Hospital getHospitalByCode(String hoscode);

    Page<Hospital> getHospitalPage(int page, int limit, HospitalQueryVo hospitalQueryVo);

    /**
     * 更新上线状态
     */
    void updateStatus(String id, Integer status);

    /**
     * 医院详情
     * @param id
     * @return
     */
    Map<String, Object> show(String id);

}
