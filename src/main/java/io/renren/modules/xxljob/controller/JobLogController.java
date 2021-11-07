package io.renren.modules.xxljob.controller;

import io.renren.modules.xxljob.core.complete.XxlJobCompleter;
import io.renren.modules.xxljob.core.model.XxlJobGroup;
import io.renren.modules.xxljob.core.model.XxlJobInfo;
import io.renren.modules.xxljob.core.model.XxlJobLog;
import io.renren.modules.xxljob.core.scheduler.XxlJobScheduler;
import io.renren.modules.xxljob.core.util.I18nUtil;
import io.renren.modules.xxljob.dao.XxlJobGroupDao;
import io.renren.modules.xxljob.dao.XxlJobInfoDao;
import io.renren.modules.xxljob.dao.XxlJobLogDao;
import com.xxl.job.core.biz.ExecutorBiz;
import com.xxl.job.core.biz.model.KillParam;
import com.xxl.job.core.biz.model.LogParam;
import com.xxl.job.core.biz.model.LogResult;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.util.DateUtil;
import io.renren.modules.xxljob.vo.JobLogInitVO;
import io.renren.modules.xxljob.vo.JobLogVO;
import io.renren.modules.xxljob.vo.PageQueryBaseVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * 执行日志
 */
@RestController
@RequestMapping("/joblog")
public class JobLogController {
    private static Logger logger = LoggerFactory.getLogger(JobLogController.class);

    @Resource
    private XxlJobGroupDao xxlJobGroupDao;
    @Resource
    public XxlJobInfoDao xxlJobInfoDao;
    @Resource
    public XxlJobLogDao xxlJobLogDao;

    /**
     * 初始化数据
     */
    @GetMapping("/init")
    public JobLogInitVO init(@RequestParam(required = false, defaultValue = "0") Integer jobId) {

        // 执行器列表
        List<XxlJobGroup> jobGroupList = xxlJobGroupDao.findAll();

        JobLogInitVO vo = new JobLogInitVO();
        vo.setJobGroupList(jobGroupList);
        // 任务
        if (jobId > 0) {
            XxlJobInfo jobInfo = xxlJobInfoDao.loadById(jobId);
            if (jobInfo == null) {
                throw new RuntimeException(I18nUtil.getString("jobinfo_field_id") + I18nUtil.getString("system_unvalid"));
            }
            vo.setJobInfo(jobInfo);
        }
        return vo;
    }

    /**
     * 任务分组
     */
    @PostMapping("/getJobsByGroup")
    public ReturnT<List<XxlJobInfo>> getJobsByGroup(int jobGroup) {
        List<XxlJobInfo> list = xxlJobInfoDao.getJobsByGroup(jobGroup);
        return new ReturnT<>(list);
    }

    /**
     * 分页查询
     */
    @PostMapping("/pageList")
    public PageQueryBaseVO<XxlJobLog> pageList(@RequestParam(required = false, defaultValue = "0") int start,
                                               @RequestParam(required = false, defaultValue = "10") int length,
                                               int jobGroup, int jobId, int logStatus, String filterTime) {

        // parse param
        Date triggerTimeStart = null;
        Date triggerTimeEnd = null;
        if (filterTime != null && filterTime.trim().length() > 0) {
            String[] temp = filterTime.split(" - ");
            if (temp.length == 2) {
                triggerTimeStart = DateUtil.parseDateTime(temp[0]);
                triggerTimeEnd = DateUtil.parseDateTime(temp[1]);
            }
        }

        // page query
        List<XxlJobLog> list = xxlJobLogDao.pageList(start, length, jobGroup, jobId, triggerTimeStart, triggerTimeEnd, logStatus);
        int listCount = xxlJobLogDao.pageListCount(start, length, jobGroup, jobId, triggerTimeStart, triggerTimeEnd, logStatus);

        // package result
        PageQueryBaseVO<XxlJobLog> vo = new PageQueryBaseVO<>();
        vo.setRecordsTotal(listCount)  // 总记录数
                .setRecordsFiltered(listCount) // 过滤后的总记录数
                .setData(list); // 分页列表
        return vo;
    }

    /**
     * 执行日志
     */
    @GetMapping("/logDetailPage")
    public JobLogVO logDetailPage(int id) {

        // base check
        ReturnT<String> logStatue = ReturnT.SUCCESS;
        XxlJobLog jobLog = xxlJobLogDao.load(id);
        if (jobLog == null) {
            throw new RuntimeException(I18nUtil.getString("joblog_logid_unvalid"));
        }
        JobLogVO vo = new JobLogVO();
        vo.setTriggerCode(jobLog.getTriggerCode())
                .setHandleCode(jobLog.getHandleCode())
                .setExecutorAddress(jobLog.getExecutorAddress())
                .setTriggerTime(jobLog.getTriggerTime().getTime())
                .setLogId(jobLog.getId());
        return vo;
    }

    /**
     * 滚动日志
     */
    @PostMapping("/logDetailCat")
    public ReturnT<LogResult> logDetailCat(String executorAddress, long triggerTime, long logId, int fromLineNum) {
        try {
            ExecutorBiz executorBiz = XxlJobScheduler.getExecutorBiz(executorAddress);
            ReturnT<LogResult> logResult = executorBiz.log(new LogParam(triggerTime, logId, fromLineNum));

            // is end
            if (logResult.getContent() != null && logResult.getContent().getFromLineNum() > logResult.getContent().getToLineNum()) {
                XxlJobLog jobLog = xxlJobLogDao.load(logId);
                if (jobLog.getHandleCode() > 0) {
                    logResult.getContent().setEnd(true);
                }
            }
            return logResult;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return new ReturnT<>(ReturnT.FAIL_CODE, e.getMessage());
        }
    }

    /**
     * 停止执行
     */
    @PostMapping("/logKill")
    public ReturnT<String> logKill(int id) {
        // base check
        XxlJobLog log = xxlJobLogDao.load(id);
        XxlJobInfo jobInfo = xxlJobInfoDao.loadById(log.getJobId());
        if (jobInfo == null) {
            return new ReturnT<>(500, I18nUtil.getString("jobinfo_glue_jobid_unvalid"));
        }
        if (ReturnT.SUCCESS_CODE != log.getTriggerCode()) {
            return new ReturnT<>(500, I18nUtil.getString("joblog_kill_log_limit"));
        }

        // request of kill
        ReturnT<String> runResult = null;
        try {
            ExecutorBiz executorBiz = XxlJobScheduler.getExecutorBiz(log.getExecutorAddress());
            runResult = executorBiz.kill(new KillParam(jobInfo.getId()));
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            runResult = new ReturnT<>(500, e.getMessage());
        }

        if (ReturnT.SUCCESS_CODE == runResult.getCode()) {
            log.setHandleCode(ReturnT.FAIL_CODE);
            log.setHandleMsg(I18nUtil.getString("joblog_kill_log_byman") + ":" + (runResult.getMsg() != null ? runResult.getMsg() : ""));
            log.setHandleTime(new Date());
            XxlJobCompleter.updateHandleInfoAndFinish(log);
            return new ReturnT<>(runResult.getMsg());
        } else {
            return new ReturnT<>(500, runResult.getMsg());
        }
    }

    /**
     * 清理日志
     */
    @PostMapping("/clearLog")
    public ReturnT<String> clearLog(int jobGroup, int jobId, int type) {

        Date clearBeforeTime = null;
        int clearBeforeNum = 0;
        if (type == 1) {
            clearBeforeTime = DateUtil.addMonths(new Date(), -1);    // 清理一个月之前日志数据
        } else if (type == 2) {
            clearBeforeTime = DateUtil.addMonths(new Date(), -3);    // 清理三个月之前日志数据
        } else if (type == 3) {
            clearBeforeTime = DateUtil.addMonths(new Date(), -6);    // 清理六个月之前日志数据
        } else if (type == 4) {
            clearBeforeTime = DateUtil.addYears(new Date(), -1);    // 清理一年之前日志数据
        } else if (type == 5) {
            clearBeforeNum = 1000;        // 清理一千条以前日志数据
        } else if (type == 6) {
            clearBeforeNum = 10000;        // 清理一万条以前日志数据
        } else if (type == 7) {
            clearBeforeNum = 30000;        // 清理三万条以前日志数据
        } else if (type == 8) {
            clearBeforeNum = 100000;    // 清理十万条以前日志数据
        } else if (type == 9) {
            clearBeforeNum = 0;            // 清理所有日志数据
        } else {
            return new ReturnT<>(ReturnT.FAIL_CODE, I18nUtil.getString("joblog_clean_type_unvalid"));
        }

        List<Long> logIds = null;
        do {
            logIds = xxlJobLogDao.findClearLogIds(jobGroup, jobId, clearBeforeTime, clearBeforeNum, 1000);
            if (logIds != null && logIds.size() > 0) {
                xxlJobLogDao.clearLog(logIds);
            }
        } while (logIds != null && logIds.size() > 0);

        return ReturnT.SUCCESS;
    }

}
