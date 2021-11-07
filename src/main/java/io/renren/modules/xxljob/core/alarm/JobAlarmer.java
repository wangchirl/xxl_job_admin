package io.renren.modules.xxljob.core.alarm;

import io.renren.modules.xxljob.core.constants.AlarmTypeEnum;
import io.renren.modules.xxljob.core.model.XxlJobInfo;
import io.renren.modules.xxljob.core.model.XxlJobLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class JobAlarmer implements ApplicationContextAware, InitializingBean {

    private static Logger logger = LoggerFactory.getLogger(JobAlarmer.class);

    private ApplicationContext applicationContext;
    private List<JobAlarm> jobAlarmList;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Map<String, JobAlarm> serviceBeanMap = applicationContext.getBeansOfType(JobAlarm.class);
        if (!serviceBeanMap.isEmpty()) {
            jobAlarmList = new ArrayList<>(serviceBeanMap.values());
        }
        // 开启情况过滤
        List<Class<? extends JobAlarm>> availableAlarms = getAvailableAlarms();
        jobAlarmList = jobAlarmList.stream().filter(item -> availableAlarms.contains(item.getClass())).collect(Collectors.toList());
    }

    private List<Class<? extends JobAlarm>> getAvailableAlarms() {
        AlarmTypeEnum[] typeEnums = AlarmTypeEnum.values();
        List<Class<? extends JobAlarm>> result = new ArrayList<>();
        for (AlarmTypeEnum typeEnum : typeEnums) {
            boolean enable = Boolean.parseBoolean(applicationContext.getEnvironment().getProperty(typeEnum.getEnableName()));
            if (enable) {
                result.add(typeEnum.getImplClass());
            }
        }
        return result;
    }


    /**
     * job alarm
     */
    public boolean alarm(XxlJobInfo info, XxlJobLog jobLog) {

        boolean result = false;
        if (jobAlarmList != null && jobAlarmList.size() > 0) {
            result = true;  // success means all-success
            for (JobAlarm alarm : jobAlarmList) {
                boolean resultItem = false;
                try {
                    // TODO 同步方式比较慢？？？
                    resultItem = alarm.doAlarm(info, jobLog);
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
                if (!resultItem) {
                    result = false;
                }
            }
        }
        return result;
    }

}
