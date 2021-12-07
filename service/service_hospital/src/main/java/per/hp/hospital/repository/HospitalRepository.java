package per.hp.hospital.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import per.hp.hospital.model.hosp.Hospital;
@Repository
public interface HospitalRepository  extends MongoRepository<Hospital,String> {
    // 根据医院代码查询对应的医院
    Hospital getHospitalByHoscode(String hoscode);
}
