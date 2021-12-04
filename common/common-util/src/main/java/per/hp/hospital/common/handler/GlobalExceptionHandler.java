package per.hp.hospital.common.handler;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import per.hp.hospital.common.exception.HospitalException;
import per.hp.hospital.common.result.Result;

/**
 * 全局异常处理类
 *
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public Result error(Exception e){
        e.printStackTrace();
        return Result.fail();
    }

    /**
     * 自定义异常处理方法
     * @param e
     * @return
     */
    @ExceptionHandler(HospitalException.class)
    @ResponseBody
    public Result error(HospitalException e){
        return Result.build(e.getCode(), e.getMessage());
    }
}

