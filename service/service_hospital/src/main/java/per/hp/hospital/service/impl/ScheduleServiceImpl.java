package per.hp.hospital.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import per.hp.hospital.model.hosp.Schedule;
import per.hp.hospital.repository.ScheduleRepository;
import per.hp.hospital.service.DepartmentService;
import per.hp.hospital.service.HospitalService;
import per.hp.hospital.service.ScheduleService;
import per.hp.hospital.vo.hosp.BookingScheduleRuleVo;
import per.hp.hospital.vo.hosp.ScheduleQueryVo;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class ScheduleServiceImpl implements ScheduleService {

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private HospitalService hospitalService;

    @Autowired
    private DepartmentService departmentService;

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

    @Override
    public Map<String, Object> getRuleSchedule(long page, long limit, String hoscode, String depcode) {
        // ????????????
        Criteria criteria = Criteria.where("hoscode").is(hoscode).and("depcode").is(depcode);
        // ????????????????????????
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(criteria), // ????????????
                Aggregation.group("workDate")   // ????????????
                .first("workDate").as("workDate")
                // ????????????
                .count().as("docCount")
                .sum("reservedNumber").as("reservedNumber")
                .sum("availableNumber").as("availableNumber"),
                // ??????
                Aggregation.sort(Sort.Direction.DESC,"workDate"),
                // ??????
                Aggregation.skip((page-1)*limit),
                Aggregation.limit(limit)
        );

        // ????????????
        AggregationResults<BookingScheduleRuleVo> aggregateResult = mongoTemplate.aggregate(aggregation, Schedule.class, BookingScheduleRuleVo.class);
        List<BookingScheduleRuleVo> bookingScheduleRuleVoList = aggregateResult.getMappedResults();

        // ??????????????????
        Aggregation totalAggregation =  Aggregation.newAggregation(
                Aggregation.match(criteria),
                Aggregation.group("workDate")
        );

        AggregationResults<BookingScheduleRuleVo> totalAggregateResult = mongoTemplate.aggregate(totalAggregation, Schedule.class, BookingScheduleRuleVo.class);
        int total = totalAggregateResult.getMappedResults().size();

        // ???????????????????????????
        for (BookingScheduleRuleVo bookingScheduleRuleVo : bookingScheduleRuleVoList) {
            Date workDate = bookingScheduleRuleVo.getWorkDate();
            String dayOfWeek = this.getDayOfWeek(new DateTime(workDate));
            bookingScheduleRuleVo.setDayOfWeek(dayOfWeek);
        }

        // ??????????????????
        HashMap<String, Object> finalDataMap = new HashMap<>();
        finalDataMap.put("bookingScheduleRuleVoList",bookingScheduleRuleVoList);
        finalDataMap.put("total",total);
        // ??????????????????
        String hospitalName = hospitalService.getHospitalName(hoscode);
        finalDataMap.put("hospitalName",hospitalName);
        return finalDataMap;
    }

    @Override
    public List<Schedule> getDetailSchedule(String hoscode, String depcode, String workDate) {
        // ???????????????
        List<Schedule> scheduleList =
                scheduleRepository.findScheduleByHoscodeAndDepcodeAndWorkDate(hoscode,depcode,new DateTime(workDate).toDate());
        // ???????????????
        scheduleList.stream().forEach(item->{
            this.packageSchedule(item);
        });
        return scheduleList;
    }

    private void packageSchedule(Schedule schedule) {
        // ??????????????????
        schedule.getParam().put("hosname",hospitalService.getHospitalName(schedule.getHoscode()));
        // ????????????
        schedule.getParam().put("depname",departmentService.getDepName(schedule.getHoscode(),schedule.getDepcode()));
        // ???????????????
        schedule.getParam().put("dayofWeek",this.getDayOfWeek(new DateTime(schedule.getWorkDate())));
    }

    /**
     * ??????????????????????????????
     * @param dateTime
     * @return
     */
    private String getDayOfWeek(DateTime dateTime) {
        String dayOfWeek = "";
        switch (dateTime.getDayOfWeek()) {
            case DateTimeConstants.SUNDAY:
                dayOfWeek = "??????";
                break;
            case DateTimeConstants.MONDAY:
                dayOfWeek = "??????";
                break;
            case DateTimeConstants.TUESDAY:
                dayOfWeek = "??????";
                break;
            case DateTimeConstants.WEDNESDAY:
                dayOfWeek = "??????";
                break;
            case DateTimeConstants.THURSDAY:
                dayOfWeek = "??????";
                break;
            case DateTimeConstants.FRIDAY:
                dayOfWeek = "??????";
                break;
            case DateTimeConstants.SATURDAY:
                dayOfWeek = "??????";
            default:
                break;
        }
        return dayOfWeek;
    }

}

