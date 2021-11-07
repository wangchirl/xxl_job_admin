package io.renren.modules.xxljob.controller;

import io.renren.modules.xxljob.core.model.XxlJobGroup;
import io.renren.modules.xxljob.core.model.XxlJobRegistry;
import io.renren.modules.xxljob.core.util.I18nUtil;
import io.renren.modules.xxljob.dao.XxlJobGroupDao;
import io.renren.modules.xxljob.dao.XxlJobInfoDao;
import io.renren.modules.xxljob.dao.XxlJobRegistryDao;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.enums.RegistryConfig;
import io.renren.modules.xxljob.vo.PageQueryBaseVO;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.*;

/**
 * 执行器管理
 */
@RestController
@RequestMapping("/jobgroup")
public class JobGroupController {

    @Resource
    public XxlJobInfoDao xxlJobInfoDao;
    @Resource
    public XxlJobGroupDao xxlJobGroupDao;
    @Resource
    private XxlJobRegistryDao xxlJobRegistryDao;

    /**
     * 分页查询
     */
    @PostMapping("/pageList")
    public PageQueryBaseVO<XxlJobGroup> pageList(@RequestParam(required = false, defaultValue = "0") int start,
                                                 @RequestParam(required = false, defaultValue = "10") int length,
                                                 String appname, String title) {

        // page query
        List<XxlJobGroup> list = xxlJobGroupDao.pageList(start, length, appname, title);
        int listCount = xxlJobGroupDao.pageListCount(start, length, appname, title);

        // package result
        PageQueryBaseVO<XxlJobGroup> vo = new PageQueryBaseVO<>();
        vo.setRecordsTotal(listCount) // 总记录数
                .setRecordsFiltered(listCount) // 过滤后的总记录数
                .setData(list); // 分页列表
        return vo;
    }

    /**
     * 保存执行器
     */
    @PostMapping("/save")
    public ReturnT<String> save(XxlJobGroup xxlJobGroup) {

        // valid
        if (xxlJobGroup.getAppname() == null || xxlJobGroup.getAppname().trim().length() == 0) {
            return new ReturnT<>(500, (I18nUtil.getString("system_please_input") + "AppName"));
        }
        if (xxlJobGroup.getAppname().length() < 4 || xxlJobGroup.getAppname().length() > 64) {
            return new ReturnT<>(500, I18nUtil.getString("jobgroup_field_appname_length"));
        }
        if (xxlJobGroup.getAppname().contains(">") || xxlJobGroup.getAppname().contains("<")) {
            return new ReturnT<>(500, "AppName" + I18nUtil.getString("system_unvalid"));
        }
        if (xxlJobGroup.getTitle() == null || xxlJobGroup.getTitle().trim().length() == 0) {
            return new ReturnT<>(500, (I18nUtil.getString("system_please_input") + I18nUtil.getString("jobgroup_field_title")));
        }
        if (xxlJobGroup.getTitle().contains(">") || xxlJobGroup.getTitle().contains("<")) {
            return new ReturnT<>(500, I18nUtil.getString("jobgroup_field_title") + I18nUtil.getString("system_unvalid"));
        }
        if (xxlJobGroup.getAddressType() != 0) {
            if (xxlJobGroup.getAddressList() == null || xxlJobGroup.getAddressList().trim().length() == 0) {
                return new ReturnT<>(500, I18nUtil.getString("jobgroup_field_addressType_limit"));
            }
            if (xxlJobGroup.getAddressList().contains(">") || xxlJobGroup.getAddressList().contains("<")) {
                return new ReturnT<>(500, I18nUtil.getString("jobgroup_field_registryList") + I18nUtil.getString("system_unvalid"));
            }

            String[] addresss = xxlJobGroup.getAddressList().split(",");
            for (String item : addresss) {
                if (item == null || item.trim().length() == 0) {
                    return new ReturnT<>(500, I18nUtil.getString("jobgroup_field_registryList_unvalid"));
                }
            }
        }

        // process
        xxlJobGroup.setUpdateTime(new Date());

        int ret = xxlJobGroupDao.save(xxlJobGroup);
        return (ret > 0) ? ReturnT.SUCCESS : ReturnT.FAIL;
    }

    /**
     * 更新执行器
     */
    @PostMapping("/update")
    public ReturnT<String> update(XxlJobGroup xxlJobGroup) {
        // valid
        if (xxlJobGroup.getAppname() == null || xxlJobGroup.getAppname().trim().length() == 0) {
            return new ReturnT<>(500, (I18nUtil.getString("system_please_input") + "AppName"));
        }
        if (xxlJobGroup.getAppname().length() < 4 || xxlJobGroup.getAppname().length() > 64) {
            return new ReturnT<>(500, I18nUtil.getString("jobgroup_field_appname_length"));
        }
        if (xxlJobGroup.getTitle() == null || xxlJobGroup.getTitle().trim().length() == 0) {
            return new ReturnT<>(500, (I18nUtil.getString("system_please_input") + I18nUtil.getString("jobgroup_field_title")));
        }
        if (xxlJobGroup.getAddressType() == 0) {
            // 0=自动注册
            List<String> registryList = findRegistryByAppName(xxlJobGroup.getAppname());
            String addressListStr = null;
            if (registryList != null && !registryList.isEmpty()) {
                Collections.sort(registryList);
                addressListStr = "";
                for (String item : registryList) {
                    addressListStr += item + ",";
                }
                addressListStr = addressListStr.substring(0, addressListStr.length() - 1);
            }
            xxlJobGroup.setAddressList(addressListStr);
        } else {
            // 1=手动录入
            if (xxlJobGroup.getAddressList() == null || xxlJobGroup.getAddressList().trim().length() == 0) {
                return new ReturnT<>(500, I18nUtil.getString("jobgroup_field_addressType_limit"));
            }
            String[] addresss = xxlJobGroup.getAddressList().split(",");
            for (String item : addresss) {
                if (item == null || item.trim().length() == 0) {
                    return new ReturnT<>(500, I18nUtil.getString("jobgroup_field_registryList_unvalid"));
                }
            }
        }

        // process
        xxlJobGroup.setUpdateTime(new Date());

        int ret = xxlJobGroupDao.update(xxlJobGroup);
        return (ret > 0) ? ReturnT.SUCCESS : ReturnT.FAIL;
    }

    private List<String> findRegistryByAppName(String appnameParam) {
        HashMap<String, List<String>> appAddressMap = new HashMap<String, List<String>>();
        List<XxlJobRegistry> list = xxlJobRegistryDao.findAll(RegistryConfig.DEAD_TIMEOUT, new Date());
        if (list != null) {
            for (XxlJobRegistry item : list) {
                if (RegistryConfig.RegistType.EXECUTOR.name().equals(item.getRegistryGroup())) {
                    String appname = item.getRegistryKey();
                    List<String> registryList = appAddressMap.get(appname);
                    if (registryList == null) {
                        registryList = new ArrayList<String>();
                    }

                    if (!registryList.contains(item.getRegistryValue())) {
                        registryList.add(item.getRegistryValue());
                    }
                    appAddressMap.put(appname, registryList);
                }
            }
        }
        return appAddressMap.get(appnameParam);
    }

    /**
     * 删除执行器
     */
    @PostMapping("/remove")
    public ReturnT<String> remove(int id) {

        // valid
        int count = xxlJobInfoDao.pageListCount(0, 10, id, -1, null, null, null);
        if (count > 0) {
            return new ReturnT<>(500, I18nUtil.getString("jobgroup_del_limit_0"));
        }

        List<XxlJobGroup> allList = xxlJobGroupDao.findAll();
        if (allList.size() == 1) {
            return new ReturnT<>(500, I18nUtil.getString("jobgroup_del_limit_1"));
        }

        int ret = xxlJobGroupDao.remove(id);
        return (ret > 0) ? ReturnT.SUCCESS : ReturnT.FAIL;
    }

    /**
     * 执行器详情
     */
    @PostMapping("/loadById")
    public ReturnT<XxlJobGroup> loadById(int id) {
        XxlJobGroup jobGroup = xxlJobGroupDao.load(id);
        return jobGroup != null ? new ReturnT<>(jobGroup) : new ReturnT<>(ReturnT.FAIL_CODE, null);
    }

}
