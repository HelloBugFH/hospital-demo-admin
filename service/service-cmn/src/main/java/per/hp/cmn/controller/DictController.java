package per.hp.cmn.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import per.hp.cmn.service.DictService;
import per.hp.hospital.common.result.Result;
import per.hp.hospital.model.cmn.Dict;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Api(description = "数据字典接口")
@RestController
@Slf4j
@RequestMapping("/admin/cmn/dict")
public class DictController {

    @Autowired
    private DictService dictService;

    // 根据dicCode查询下级节点
    @GetMapping(value = "/findByDictCode/{dictCode}")
    public Result<List<Dict>> findByDictCode(
            @ApiParam(name = "dictCode", value = "节点编码", required = true)
            @PathVariable String dictCode) {
        List<Dict> list = dictService.findByDictCode(dictCode);
        return Result.ok(list);
    }

        //根据数据id查询子数据列表
    @GetMapping("/findChildData/{id}")
    public Result findChildData(@PathVariable Long id) {
//        log.info("id---"+id);
        List<Dict> list = dictService.findChildrenData(id);
//        log.info("list---"+list.toString());
        return Result.ok(list);
    }

    @ApiOperation(value="导出")
    @GetMapping(value = "/exportData")
    public void exportData(HttpServletResponse response) {
        dictService.exportData(response);
    }

    @PostMapping("/importData")
    public Result importData(MultipartFile file) {
        dictService.importDictData(file);
        return Result.ok();
    }

    // 根据dicCode和value查询
    @GetMapping("getName/{dicCode}/{value}")
    public String getName(@PathVariable String dicCode,@PathVariable String value){
        String dicName = dictService.getDicName(dicCode,value);
        return dicName;
    }

    // 根据value查询
    @GetMapping("getName/{value}")
    public String getName(@PathVariable String value){
        String dicName = dictService.getDicName("",value);
        return dicName;
    }

}

