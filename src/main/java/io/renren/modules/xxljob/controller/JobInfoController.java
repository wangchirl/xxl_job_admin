package io.renren.modules.xxljob.controller;

import io.renren.modules.xxljob.core.exception.XxlJobException;
import io.renren.modules.xxljob.core.model.XxlJobGroup;
import io.renren.modules.xxljob.core.model.XxlJobInfo;
import io.renren.modules.xxljob.core.route.ExecutorRouteStrategyEnum;
import io.renren.modules.xxljob.core.scheduler.MisfireStrategyEnum;
import io.renren.modules.xxljob.core.scheduler.ScheduleTypeEnum;
import io.renren.modules.xxljob.core.thread.JobScheduleHelper;
import io.renren.modules.xxljob.core.thread.JobTriggerPoolHelper;
import io.renren.modules.xxljob.core.trigger.TriggerTypeEnum;
import io.renren.modules.xxljob.core.util.I18nUtil;
import io.renren.modules.xxljob.dao.XxlJobGroupDao;
import io.renren.modules.xxljob.service.XxlJobService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.enums.ExecutorBlockStrategyEnum;
import com.xxl.job.core.glue.GlueTypeEnum;
import com.xxl.job.core.util.DateUtil;
import io.renren.modules.xxljob.vo.JobInfoInitVO;
import io.renren.modules.xxljob.vo.PageQueryBaseVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.*;

/**
 * 任务管理
 */
@RestController
@RequestMapping("/jobinfo")
public class JobInfoController {
    private static Logger logger = LoggerFactory.getLogger(JobInfoController.class);

    @Resource
    private XxlJobGroupDao xxlJobGroupDao;
    @Resource
    private XxlJobService xxlJobService;

    /**
     * 任务管理首页
     */
    @GetMapping("/init")
    public JobInfoInitVO init(@RequestParam(required = false, defaultValue = "-1") int jobGroup) {
        // 执行器列表
        List<XxlJobGroup> jobGroupList = xxlJobGroupDao.findAll();

        // 枚举-字典
        JobInfoInitVO vo = new JobInfoInitVO();
        vo.setExecutorRouteStrategyEnums(ExecutorRouteStrategyEnum.values()) // 路由策略-列表
                .setGlueTypeEnums(GlueTypeEnum.values()) // Glue类型-字典
                .setExecutorBlockStrategyEnums(ExecutorBlockStrategyEnum.values()) // 阻塞处理策略-字典
                .setScheduleTypeEnums(ScheduleTypeEnum.values()) // 调度类型
                .setMisfireStrategyEnums(MisfireStrategyEnum.values()) // 调度过期策略
                .setJobGroupList(jobGroupList)
                .setJobGroup(jobGroup);
        return vo;
    }

    /**
     * 分页查询
     */
    @PostMapping("/pageList")
    public PageQueryBaseVO<XxlJobInfo> pageList(@RequestParam(required = false, defaultValue = "0") int start,
                                                @RequestParam(required = false, defaultValue = "10") int length,
                                                int jobGroup, int triggerStatus, String jobDesc, String executorHandler, String author) {

        return xxlJobService.pageList(start, length, jobGroup, triggerStatus, jobDesc, executorHandler, author);
    }

    /**
     * 任务详情
     */
    @GetMapping("/info/{id}")
    public XxlJobInfo info(@PathVariable("id") int id) {
        return xxlJobService.info(id);
    }

    /**
     * 添加任务
     */
    @PostMapping("/save")
    public ReturnT<String> add(XxlJobInfo jobInfo) {
        return xxlJobService.add(jobInfo);
    }

    /**
     * 更新任务
     */
    @PostMapping("/update")
    public ReturnT<String> update(XxlJobInfo jobInfo) {
        return xxlJobService.update(jobInfo);
    }

    /**
     * 删除任务
     */
    @PostMapping("/remove")
    public ReturnT<String> remove(int id) {
        return xxlJobService.remove(id);
    }

    /**
     * 停止任务
     */
    @PostMapping("/stop")
    public ReturnT<String> pause(int id) {
        return xxlJobService.stop(id);
    }

    /**
     * 启动任务
     */
    @PostMapping("/start")
    public ReturnT<String> start(int id) {
        return xxlJobService.start(id);
    }

    /**
     * 执行任务
     */
    @PostMapping("/trigger")
    public ReturnT<String> triggerJob(int id, String executorParam, String addressList) {
        // force cover job param
        if (executorParam == null) {
            executorParam = "";
        }

        JobTriggerPoolHelper.trigger(id, TriggerTypeEnum.MANUAL, -1, null, executorParam, addressList);
        return ReturnT.SUCCESS;
    }

    /**
     * 下一次执行时间
     */
    @PostMapping("/nextTriggerTime")
    public ReturnT<List<String>> nextTriggerTime(String scheduleType, String scheduleConf) {

        XxlJobInfo paramXxlJobInfo = new XxlJobInfo();
        paramXxlJobInfo.setScheduleType(scheduleType);
        paramXxlJobInfo.setScheduleConf(scheduleConf);

        List<String> result = new ArrayList<>();
        try {
            Date lastTime = new Date();
            for (int i = 0; i < 5; i++) {
                lastTime = JobScheduleHelper.generateNextValidTime(paramXxlJobInfo, lastTime);
                if (lastTime != null) {
                    result.add(DateUtil.formatDateTime(lastTime));
                } else {
                    break;
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return new ReturnT<>(ReturnT.FAIL_CODE, (I18nUtil.getString("schedule_type") + I18nUtil.getString("system_unvalid")) + e.getMessage());
        }
        return new ReturnT<>(result);

    }

}
