package io.renren.modules.xxljob.controller;

import io.renren.modules.xxljob.core.model.XxlJobInfo;
import io.renren.modules.xxljob.core.model.XxlJobLogGlue;
import io.renren.modules.xxljob.core.util.I18nUtil;
import io.renren.modules.xxljob.dao.XxlJobInfoDao;
import io.renren.modules.xxljob.dao.XxlJobLogGlueDao;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.glue.GlueTypeEnum;
import io.renren.modules.xxljob.vo.JobCodeInitVO;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * 编辑器 glue 代码
 */
@RestController
@RequestMapping("/jobcode")
public class JobCodeController {

    @Resource
    private XxlJobInfoDao xxlJobInfoDao;
    @Resource
    private XxlJobLogGlueDao xxlJobLogGlueDao;

    /**
     * glue 代码编辑器
     */
    @GetMapping("/init")
    public JobCodeInitVO init(int jobId) {
        XxlJobInfo jobInfo = xxlJobInfoDao.loadById(jobId);
        List<XxlJobLogGlue> jobLogGlues = xxlJobLogGlueDao.findByJobId(jobId);

        if (jobInfo == null) {
            throw new RuntimeException(I18nUtil.getString("jobinfo_glue_jobid_unvalid"));
        }
        if (GlueTypeEnum.BEAN == GlueTypeEnum.match(jobInfo.getGlueType())) {
            throw new RuntimeException(I18nUtil.getString("jobinfo_glue_gluetype_unvalid"));
        }

        // Glue类型-字典
        JobCodeInitVO vo = new JobCodeInitVO();
        vo.setGlueTypeEnums(GlueTypeEnum.values())
                .setJobInfo(jobInfo)
                .setJobLogGlues(jobLogGlues);
        return vo;
    }

    /**
     * 保存 glue 代码
     */
    @PostMapping("/save")
    public ReturnT<String> save(int id, String glueSource, String glueRemark) {
        // valid
        if (glueRemark == null) {
            return new ReturnT<>(500, (I18nUtil.getString("system_please_input") + I18nUtil.getString("jobinfo_glue_remark")));
        }
        if (glueRemark.length() < 4 || glueRemark.length() > 100) {
            return new ReturnT<>(500, I18nUtil.getString("jobinfo_glue_remark_limit"));
        }
        XxlJobInfo exists_jobInfo = xxlJobInfoDao.loadById(id);
        if (exists_jobInfo == null) {
            return new ReturnT<>(500, I18nUtil.getString("jobinfo_glue_jobid_unvalid"));
        }

        // update new code
        exists_jobInfo.setGlueSource(glueSource);
        exists_jobInfo.setGlueRemark(glueRemark);
        exists_jobInfo.setGlueUpdatetime(new Date());

        exists_jobInfo.setUpdateTime(new Date());
        xxlJobInfoDao.update(exists_jobInfo);

        // log old code
        XxlJobLogGlue xxlJobLogGlue = new XxlJobLogGlue();
        xxlJobLogGlue.setJobId(exists_jobInfo.getId());
        xxlJobLogGlue.setGlueType(exists_jobInfo.getGlueType());
        xxlJobLogGlue.setGlueSource(glueSource);
        xxlJobLogGlue.setGlueRemark(glueRemark);

        xxlJobLogGlue.setAddTime(new Date());
        xxlJobLogGlue.setUpdateTime(new Date());
        xxlJobLogGlueDao.save(xxlJobLogGlue);

        // remove code backup more than 30
        xxlJobLogGlueDao.removeOld(exists_jobInfo.getId(), 30);

        return ReturnT.SUCCESS;
    }

}
