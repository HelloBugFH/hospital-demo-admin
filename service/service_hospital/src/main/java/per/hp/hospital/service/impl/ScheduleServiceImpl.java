package per.hp.hospital.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import per.hp.hospital.model.hosp.Schedule;
import per.hp.hospital.repository.ScheduleRepository;
import per.hp.hospital.service.ScheduleService;
import per.hp.hospital.vo.hosp.ScheduleQueryVo;

import java.util.Date;
import java.util.Map;

@Service
@Slf4j
public class ScheduleServiceImpl implements ScheduleService {

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Override
    public void save(Map paramMap) {
        String paramString = JSON.toJSONString(paramMap);
        Schedule schedule = JSONObject.parseObject(paramString, Schedule.class);

        Schedule schedule_exist = scheduleRepository.getScheduleByHoscodeAndHosScheduleId(schedule.getHoscode(), schedule.getHosScheduleId());

        if (schedule_exist != null) {
            schedule_exist.setUpdateTime(new Date());
            schedule_exist.setIsDeleted(0);
            schedule_exist.setStatus(1);
            scheduleRepository.save(schedule_exist);
        } else {
            schedule.setCreateTime(new Date());
            schedule.setUpdateTime(new Date());
            schedule.setIsDeleted(0);
            schedule.setStatus(1);
            scheduleRepository.save(schedule);
        }
    }

    @Override
    public Page<Schedule> selectPage(int page, int limit, ScheduleQueryVo scheduleQueryVo) {
        Schedule schedule = new Schedule();
        BeanUtils.copyProperties(scheduleQueryVo, schedule);
        schedule.setIsDeleted(0);
        schedule.setStatus(1);
        Pageable pageAble = PageRequest.of(page - 1, limit);
        ExampleMatcher exampleMatcher = ExampleMatcher.matching()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                .withIgnoreCase(true);
        Example<Schedule> example = Example.of(schedule, exampleMatcher);
        Page<Schedule> all = scheduleRepository.findAll(example, pageAble);
        return all;
    }

    @Override
    public void remove(String hoscode, String hosScheduleId) {
        Schedule schedule = scheduleRepository.getScheduleByHoscodeAndHosScheduleId(hoscode, hosScheduleId);
        if (schedule!=null){
            scheduleRepository.deleteById(schedule.getId());
        }
    }
}

