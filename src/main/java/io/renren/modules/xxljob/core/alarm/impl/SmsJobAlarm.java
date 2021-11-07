package io.renren.modules.xxljob.core.alarm.impl;

import io.renren.modules.xxljob.core.alarm.JobAlarm;
import io.renren.modules.xxljob.core.constants.AlarmConstants;
import io.renren.modules.xxljob.core.model.XxlJobInfo;
import io.renren.modules.xxljob.core.model.XxlJobLog;
import io.renren.modules.xxljob.core.util.HttpUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * https://market.aliyun.com/products/57126001/cmapi025016.html?spm=5176.2020520132.101.8.47777218vz4oUq#sku=yuncode1901600006
 * <p>
 * 重要提示如下:
 * HttpUtils请从
 * https://github.com/aliyun/api-gateway-demo-sign-java/blob/master/src/main/java/com/aliyun/api/gateway/demo/util/HttpUtils.java 下载
 * 相应的依赖请参照
 * https://github.com/aliyun/api-gateway-demo-sign-java/blob/master/pom.xml
 * <p>
 * 短信服务
 */
@Component
public class SmsJobAlarm implements JobAlarm {

    private static Logger logger = LoggerFactory.getLogger(SmsJobAlarm.class);

    @Autowired
    private RestTemplate restTemplate;

    @Value("${alibaba.sms.base.host}")
    private String baseHost;

    @Value("${alibaba.sms.base.path}")
    private String basePath;

    @Value("${alibaba.sms.appcode}")
    private String appCode;

    /**
     * fail alarm
     */

    @Override
    public boolean doAlarm(XxlJobInfo info, XxlJobLog jobLog) {
        boolean alarmResult = true;
        // send monitor email
        if (info != null && info.getAlarmEmail() != null && info.getAlarmEmail().trim().length() > 0) {
            // 全局判断下，没有直接返回
            if (!info.getAlarmEmail().contains(AlarmConstants.MOBILE)) {
                return true;
            }
            Set<String> dingWebhookSet = new HashSet<>(Arrays.asList(info.getAlarmEmail().split(",")));
            // 过滤掉非电话开头的信息，支持多个配置，以逗号隔开
            List<String> mobiles = dingWebhookSet.stream()
                    .filter(item -> item.startsWith(AlarmConstants.MOBILE))
                    .map(item -> item.substring(AlarmConstants.MOBILE.length() + 1))
                    .collect(Collectors.toList());
            // 正常申请短信模板
            Map<String, String> querys = loadSmsJobAlarmTemplate(info, jobLog);
            // TODO 测试替换
            querys.put(AlarmConstants.CONTENT, "【创信】你的验证码是：5873，3分钟内有效！");
            // 发送短信消息
            Map<String, String> bodys = new HashMap<>();
            for (String mobile : mobiles) {
                try {
                    Map<String, String> headers = new HashMap<>();
                    headers.put(AlarmConstants.AUTHORIZATION, AlarmConstants.APP_CODE + " " + appCode);
                    querys.put(AlarmConstants.MOBILE, mobile);
                    HttpUtils.doPost(baseHost, basePath, "POST", headers, querys, bodys);
                } catch (Exception e) {
                    logger.error(">>>>>>>>>>> xxl-job, job fail alarm sms send error, JobLogId:{}", jobLog.getId(), e);
                    alarmResult = false;
                }
            }
        }
        return alarmResult;
    }


    private static final Map<String, String> loadSmsJobAlarmTemplate(XxlJobInfo info, XxlJobLog jobLog) {
        HashMap<String, String> map = new HashMap<>(1);
        /** 设置消息内容 -- start **/
        String content =
                "【任务报警】 \t\n" +
                        "负责人 : \t" + info.getAuthor() + "\t\n" +
                        "任务id : \t" + info.getId() + "\t\n" +
                        "任务名称 : \t" + info.getJobDesc() + "\t\n" +
                        "执行器名称 : \t" + info.getExecutorHandler() + "\t\n" +
                        "执行器ip : \t" + jobLog.getExecutorAddress() + "\t\n" +
                        "任务参数 : \t" + jobLog.getExecutorParam() + "\t\n" +
                        "LogId : \t" + jobLog.getId() + "\t\n" +
                        "TriggerMsg : \t" + jobLog.getTriggerMsg().replace("<br>", "\n") + "\t\n" +
                        "HandleCode : \t" + jobLog.getHandleMsg() + "\t\n" +
                        "报警时间 : \t" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "\t\n";
        map.put(AlarmConstants.CONTENT, content);
        return map;
    }
}