package per.hp.hospital.service;

import org.springframework.data.domain.Page;
import per.hp.hospital.model.hosp.Schedule;
import per.hp.hospital.vo.hosp.ScheduleQueryVo;

import java.util.Map;

public interface ScheduleService {
    void save(Map paramMap);

    Page<Schedule> selectPage(int page, int limit, ScheduleQueryVo scheduleQueryVo);

    void remove(String hoscode, String hosScheduleId);
}
