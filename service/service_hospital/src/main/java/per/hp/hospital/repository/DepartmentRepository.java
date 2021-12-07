package per.hp.hospital.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import per.hp.hospital.model.hosp.Department;

@Repository
public interface DepartmentRepository extends MongoRepository<Department,String> {

    Department getDepartmentByHoscodeAndDepcode(String hoscode, String depcode);
}
