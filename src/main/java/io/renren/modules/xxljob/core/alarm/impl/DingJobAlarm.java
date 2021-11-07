package io.renren.modules.xxljob.core.alarm.impl;

import io.renren.modules.xxljob.core.alarm.JobAlarm;
import io.renren.modules.xxljob.core.constants.AlarmConstants;
import io.renren.modules.xxljob.core.model.XxlJobInfo;
import io.renren.modules.xxljob.core.model.XxlJobLog;
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
 * https://developers.dingtalk.com/document/robots/customize-robot-security-settings
 * 钉钉机器人服务
 */
@Component
public class DingJobAlarm implements JobAlarm {

    private static Logger logger = LoggerFactory.getLogger(DingJobAlarm.class);

    @Autowired
    private RestTemplate restTemplate;

    @Value("${alibaba.dingtalk.robot.baseurl}")
    private String baseDingWebhook;

    /**
     * fail alarm
     */
    @Override
    public boolean doAlarm(XxlJobInfo info, XxlJobLog jobLog) {
        boolean alarmResult = true;
        // send monitor email
        if (info != null && info.getAlarmEmail() != null && info.getAlarmEmail().trim().length() > 0) {
            // 全局判断下，没有直接返回
            if (!info.getAlarmEmail().contains(AlarmConstants.ACCESS_TOKEN)) {
                return true;
            }
            Set<String> dingWebhookSet = new HashSet<>(Arrays.asList(info.getAlarmEmail().split(",")));
            // 过滤掉非钉钉token开头的信息，支持多个配置，以逗号隔开
            List<String> accessTokens = dingWebhookSet.stream().filter(item -> item.startsWith(AlarmConstants.ACCESS_TOKEN)).collect(Collectors.toList());
            Map<String, Object> map = loadEmailJobAlarmTemplate(info, jobLog);
            // 发送钉钉消息
            for (String accessToken : accessTokens) {
                try {
                    restTemplate.postForEntity(baseDingWebhook + accessToken, map, Object.class);
                } catch (Exception e) {
                    logger.error(">>>>>>>>>>> xxl-job, job fail alarm dingTalk send error, JobLogId:{}", jobLog.getId(), e);
                    alarmResult = false;
                }
            }
        }
        return alarmResult;
    }


    private static final Map<String, Object> loadEmailJobAlarmTemplate(XxlJobInfo info, XxlJobLog jobLog) {
        HashMap<String, Object> map = new HashMap<>(2);
        /** 设置消息类型 **/
        map.put("msgtype", "text");

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
        HashMap<String, String> cmap = new HashMap<>(1);
        cmap.put(AlarmConstants.CONTENT, content);
        map.put("text", cmap);
        /** 设置消息内容 -- stop **/


        /** 设置是否@指定人 --start **/
        Map<String, Object> atmap = new HashMap<>();
        String[] authorList = info.getAuthor().split(",");
        List<String> arrayList = new ArrayList<>();
        for (String author : authorList) {
            if ("".equals(author) || author.split("-").length < 2) {
                continue;
            }
            arrayList.add(author.split("-")[1]);
        }
        if (arrayList.size() > 0) {
            atmap.put("atMobiles", arrayList.toArray());
            map.put("at", atmap);
        }
        /** 设置是否@指定人 --stop **/

        return map;
    }
}