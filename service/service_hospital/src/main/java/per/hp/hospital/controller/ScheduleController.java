package per.hp.hospital.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import per.hp.hospital.common.result.Result;
import per.hp.hospital.model.hosp.Schedule;
import per.hp.hospital.service.ScheduleService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/hosp/schedule")
public class ScheduleController {
    @Autowired
    ScheduleService scheduleService;

    // 根据医院编号和科室编号查询排班信息
    @GetMapping("getScheduleRule/{page}/{limit}/{hoscode}/{depcode}")
    public Result getScheduleRule(@PathVariable int page,@PathVariable int limit,
                                  @PathVariable String hoscode,@PathVariable String depcode){
        Map<String, Object> ruleSchedule = scheduleService.getRuleSchedule(page, limit, hoscode, depcode);
        return Result.ok(ruleSchedule);
    }

    // 某个工作日期中的排班详细信息
    @GetMapping("getScheduleDetail/{hoscode}/{depcode}/{workDate}")
    public Result getScheduleDetail(@PathVariable String hoscode,@PathVariable String depcode,@PathVariable String workDate){
        List<Schedule> list = scheduleService.getDetailSchedule(hoscode,depcode,workDate);
        return Result.ok(list);
    }
}
