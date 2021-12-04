package per.hp.cmn.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;
import per.hp.hospital.model.cmn.Dict;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface DictService extends IService<Dict> {
    //根据数据id查询子数据列表
    List<Dict> findChildrenData(Long id);

    /**
     * 导出
     * @param response
     */
    void exportData(HttpServletResponse response);

    public void importDictData(MultipartFile file);
}
