package per.hp.cmn.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient("service-cmn")
@Component
public interface DictFeignClient {
    // 根据dicCode和value查询
    @GetMapping("/admin/cmn/dict/getName/{dicCode}/{value}")
    public String getName(@PathVariable("dicCode") String dicCode,@PathVariable("value") String value);

    // 根据value查询
    @GetMapping("/admin/cmn/dict/getName/{value}")
    public String getName(@PathVariable("value") String value);

}
