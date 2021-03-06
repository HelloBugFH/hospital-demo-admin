package per.hp.hospital.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import per.hp.hospital.model.hosp.Department;
import per.hp.hospital.repository.DepartmentRepository;
import per.hp.hospital.service.DepartmentService;
import per.hp.hospital.vo.hosp.DepartmentQueryVo;
import per.hp.hospital.vo.hosp.DepartmentVo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DepartmentServiceImpl implements DepartmentService {

    @Autowired
    private DepartmentRepository departmentRepository;

    @Override
    public void save(Map<String, Object> paramMap) {
        String paramString = JSON.toJSONString(paramMap);
        Department department = JSONObject.parseObject(paramString, Department.class);

        Department department_exist = departmentRepository.getDepartmentByHoscodeAndDepcode(department.getHoscode(), department.getDepcode());

        if (department_exist != null) {
            department_exist.setUpdateTime(new Date());
            department_exist.setIsDeleted(0);
            departmentRepository.save(department_exist);
        } else {
            department.setCreateTime(new Date());
            department.setUpdateTime(new Date());
            department.setIsDeleted(0);
            departmentRepository.save(department);
        }
    }

    @Override
    public Page<Department> selectPage(Integer page, Integer limit, DepartmentQueryVo departmentQueryVo) {
        Department department = new Department();
        BeanUtils.copyProperties(departmentQueryVo, department);
        department.setIsDeleted(0);
        Pageable pageAble = PageRequest.of(page - 1, limit);
        ExampleMatcher exampleMatcher = ExampleMatcher.matching()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                .withIgnoreCase(true);
        Example<Department> example = Example.of(department, exampleMatcher);
        Page<Department> all = departmentRepository.findAll(example, pageAble);
        return all;
    }

    @Override
    public void remove(String hoscode, String depcode) {
        Department department = departmentRepository.getDepartmentByHoscodeAndDepcode(hoscode, depcode);
        if (null != department) {
            departmentRepository.deleteById(department.getId());
        }
    }

    //???????????????????????????????????????????????????
    @Override
    public List<DepartmentVo> findDeptTree(String hoscode) {
        //??????list?????????????????????????????????
        List<DepartmentVo> result = new ArrayList<>();

        //???????????????????????????????????????????????????
        Department departmentQuery = new Department();
        departmentQuery.setHoscode(hoscode);
        Example example = Example.of(departmentQuery);
        //?????????????????? departmentList
        List<Department> departmentList = departmentRepository.findAll(example);

        //?????????????????????  bigcode ???????????????????????????????????????????????????
        Map<String, List<Department>> deparmentMap =
                departmentList.stream().collect(Collectors.groupingBy(Department::getBigcode));
        //??????map?????? departmentMap
        for(Map.Entry<String,List<Department>> entry : deparmentMap.entrySet()) {
            //???????????????
            String bigcode = entry.getKey();
            //????????????????????????????????????
            List<Department> deparment1List = entry.getValue();
            //???????????????
            DepartmentVo departmentVo1 = new DepartmentVo();
            departmentVo1.setDepcode(bigcode);
            departmentVo1.setDepname(deparment1List.get(0).getBigname());

            //???????????????
            List<DepartmentVo> children = new ArrayList<>();
            for(Department department: deparment1List) {
                DepartmentVo departmentVo2 =  new DepartmentVo();
                departmentVo2.setDepcode(department.getDepcode());
                departmentVo2.setDepname(department.getDepname());
                //?????????list??????
                children.add(departmentVo2);
            }
            //????????????list?????????????????????children??????
            departmentVo1.setChildren(children);
            //????????????result??????
            result.add(departmentVo1);
        }
        //??????
        return result;
    }

    @Override
    public String getDepName(String hoscode, String depcode) {
        Department department = departmentRepository.getDepartmentByHoscodeAndDepcode(hoscode, depcode);
        if (department!=null){
            String depname = department.getDepname();
            return depname;
        }
        return null;
    }


}

