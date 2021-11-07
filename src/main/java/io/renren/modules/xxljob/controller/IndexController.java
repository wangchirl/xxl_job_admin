package io.renren.modules.xxljob.controller;

import com.xxl.job.core.biz.model.ReturnT;
import io.renren.modules.xxljob.service.XxlJobService;
import io.renren.modules.xxljob.vo.ChartInfoVO;
import io.renren.modules.xxljob.vo.DashboardInitVO;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 首页 - 运行报表
 */
@RestController
@RequestMapping("/index")
public class IndexController {

    @Resource
    private XxlJobService xxlJobService;

    /**
     * 初始数据
     */
    @GetMapping("/init")
    public DashboardInitVO init() {
        return xxlJobService.dashboardInfo();
    }

    /**
     * 图标数据
     */
    @PostMapping("/chartInfo")
    public ReturnT<ChartInfoVO> chartInfo(Date startDate, Date endDate) {
        return xxlJobService.chartInfo(startDate, endDate);
    }

    /**
     * 文档帮助
     */
    @GetMapping("/help")
    public String help() {
        return "help";
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dateFormat.setLenient(false);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
    }

}
