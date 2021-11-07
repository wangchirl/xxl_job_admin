package io.renren.modules.xxljob.service;


import io.renren.modules.xxljob.core.model.XxlJobInfo;
import com.xxl.job.core.biz.model.ReturnT;
import io.renren.modules.xxljob.vo.ChartInfoVO;
import io.renren.modules.xxljob.vo.DashboardInitVO;
import io.renren.modules.xxljob.vo.PageQueryBaseVO;

import java.util.Date;
import java.util.Map;

/**
 * core job action for xxl-job
 *
 * @author xuxueli 2016-5-28 15:30:33
 */
public interface XxlJobService {

    /**
     * page list
     *
     * @param start
     * @param length
     * @param jobGroup
     * @param jobDesc
     * @param executorHandler
     * @param author
     * @return
     */
    PageQueryBaseVO<XxlJobInfo> pageList(int start, int length, int jobGroup, int triggerStatus, String jobDesc, String executorHandler, String author);

    /**
     * add job
     *
     * @param jobInfo
     * @return
     */
    ReturnT<String> add(XxlJobInfo jobInfo);

    /**
     * update job
     *
     * @param jobInfo
     * @return
     */
    ReturnT<String> update(XxlJobInfo jobInfo);

    /**
     * remove job
     * *
     *
     * @param id
     * @return
     */
    ReturnT<String> remove(int id);

    /**
     * start job
     *
     * @param id
     * @return
     */
    ReturnT<String> start(int id);

    /**
     * stop job
     *
     * @param id
     * @return
     */
    ReturnT<String> stop(int id);

    /**
     * dashboard info
     *
     * @return
     */
    DashboardInitVO dashboardInfo();

    /**
     * chart info
     *
     * @param startDate
     * @param endDate
     * @return
     */
    ReturnT<ChartInfoVO> chartInfo(Date startDate, Date endDate);

    XxlJobInfo info(int id);

}
