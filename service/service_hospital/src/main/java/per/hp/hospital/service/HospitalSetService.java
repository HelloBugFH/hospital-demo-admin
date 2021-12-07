package per.hp.hospital.service;

import com.baomidou.mybatisplus.extension.service.IService;
import per.hp.hospital.model.hosp.HospitalSet;

public interface HospitalSetService extends IService<HospitalSet> {
    String getSignKeyByHoscode(String hoscode);
}
