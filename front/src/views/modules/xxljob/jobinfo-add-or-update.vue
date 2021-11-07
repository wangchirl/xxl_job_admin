<template>
  <el-dialog
    :title="!dataForm.id ? '新增' : '修改'"
    :close-on-click-modal="false"
    :visible.sync="visible"
  >
    <el-form
      :model="dataForm"
      :rules="dataRule"
      ref="dataForm"
      @keyup.enter.native="dataFormSubmit()"
      label-width="80px"
    >
      <!-- 基础配置 -->
      <el-form-item label="执行器">
        <el-select
          v-model="dataForm.jobGroup"
          placeholder="请选择"
          default-first-option="true"
        >
          <el-option
            v-for="item in enumList.jobGroupList"
            :key="item.id"
            :label="item.title"
            :value="item.id"
          >
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="任务描述" prop="jobDesc">
        <el-input
          v-model="dataForm.jobDesc"
          placeholder="请输入任务描述"
        ></el-input>
      </el-form-item>
      <el-form-item label="负责人" prop="author">
        <el-input
          v-model="dataForm.author"
          placeholder="请输入负责人"
        ></el-input>
      </el-form-item>
      <el-form-item label="报警邮件" prop="alarmEmail">
        <el-input
          v-model="dataForm.alarmEmail"
          placeholder="请输入报警通知方式"
        ></el-input>
      </el-form-item>
      <!-- 调度配置 -->
      <el-form-item label="调度类型">
        <el-select
          v-model="dataForm.scheduleType"
          placeholder="请选择"
          default-first-option="true"
        >
          <el-option
            v-for="item in enumList.scheduleTypeEnums"
            :key="item"
            :label="
              item === 'NONE' ? '无' : item === 'CRON' ? 'CRON' : '固定速度'
            "
            :value="item"
          >
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item
        v-if="dataForm.scheduleType === 'CRON'"
        label="Cron"
        prop="scheduleConf"
      >
        <el-input
          v-model="dataForm.scheduleConf"
          placeholder="cron表达式"
        ></el-input>
      </el-form-item>
      <el-form-item
        v-if="dataForm.scheduleType === 'FIX_RATE'"
        label="Cron"
        prop="scheduleConf"
      >
        <el-input
          v-model="dataForm.scheduleConf"
          placeholder="请输入(second)"
        ></el-input>
      </el-form-item>
      <!-- 任务配置 -->
      <el-form-item label="调度类型" prop="glueType">
        <el-input v-model="dataForm.glueType" placeholder="GLUE类型"></el-input>
      </el-form-item>
      <el-form-item label="JobHandler" prop="executorHandler">
        <el-input
          v-model="dataForm.executorHandler"
          placeholder="请输入JobHandler"
        ></el-input>
      </el-form-item>
      <el-form-item label="任务参数" prop="executorParam">
        <el-input
          v-model="dataForm.executorParam"
          placeholder="请输入任务参数"
        ></el-input>
      </el-form-item>
      <!-- 高级配置 -->
      <el-form-item label="路由策略" prop="executorRouteStrategy">
        <el-input
          v-model="dataForm.executorRouteStrategy"
          placeholder="执行器路由策略"
        ></el-input>
      </el-form-item>
      <el-form-item label="子任务ID" prop="childJobid">
        <el-input
          v-model="dataForm.childJobid"
          placeholder="请输入子任务ID，多个逗号分隔"
        ></el-input>
      </el-form-item>
      <el-form-item label="调度过期策略" prop="misfireStrategy">
        <el-input
          v-model="dataForm.misfireStrategy"
          placeholder="调度过期策略"
        ></el-input>
      </el-form-item>
      <el-form-item label="阻塞处理策略" prop="executorBlockStrategy">
        <el-input
          v-model="dataForm.executorBlockStrategy"
          placeholder="阻塞处理策略"
        ></el-input>
      </el-form-item>
      <el-form-item label="任务超时时间" prop="executorTimeout">
        <el-input
          v-model="dataForm.executorTimeout"
          placeholder="任务执行超时时间，单位秒"
        ></el-input>
      </el-form-item>
      <el-form-item label="失败重试次数" prop="executorFailRetryCount">
        <el-input
          v-model="dataForm.executorFailRetryCount"
          placeholder="失败重试次数"
        ></el-input>
      </el-form-item>
    </el-form>
    <span slot="footer" class="dialog-footer">
      <el-button @click="visible = false">取消</el-button>
      <el-button type="primary" @click="dataFormSubmit()">确定</el-button>
    </span>
  </el-dialog>
</template>

<script>
export default {
  data() {
    return {
      visible: false,
      enumList: {},
      dataForm: {
        id: 0,
        jobGroup: "",
        jobDesc: "",
        addTime: "",
        updateTime: "",
        author: "",
        alarmEmail: "",
        scheduleType: "",
        scheduleConf: "",
        misfireStrategy: "",
        executorRouteStrategy: "",
        executorHandler: "",
        executorParam: "",
        executorBlockStrategy: "",
        executorTimeout: "",
        executorFailRetryCount: "",
        glueType: "",
        glueSource: "",
        glueRemark: "",
        glueUpdatetime: "",
        childJobid: "",
        triggerStatus: "",
        triggerLastTime: "",
        triggerNextTime: "",
      },
      dataRule: {
        jobGroup: [
          { required: true, message: "执行器主键ID不能为空", trigger: "blur" },
        ],
        jobDesc: [{ required: true, message: "不能为空", trigger: "blur" }],
        author: [
          { required: true, message: "负责人不能为空", trigger: "blur" },
        ],
        scheduleType: [
          { required: true, message: "调度类型不能为空", trigger: "blur" },
        ],
      },
    };
  },
  methods: {
    init(id, enums) {
      this.enumList = enums;
      this.dataForm.id = id || 0;
      this.visible = true;
      this.$nextTick(() => {
        this.$refs["dataForm"].resetFields();
        if (this.dataForm.id) {
          this.$http({
            url: this.$http.adornUrl(`/jobinfo/info/${this.dataForm.id}`),
            method: "get",
            params: this.$http.adornParams(),
          }).then(({ data }) => {
            if (data) {
              this.dataForm.jobGroup = data.jobGroup;
              this.dataForm.jobDesc = data.jobDesc;
              this.dataForm.addTime = data.addTime;
              this.dataForm.updateTime = data.updateTime;
              this.dataForm.author = data.author;
              this.dataForm.alarmEmail = data.alarmEmail;
              this.dataForm.scheduleType = data.scheduleType;
              this.dataForm.scheduleConf = data.scheduleConf;
              this.dataForm.misfireStrategy = data.misfireStrategy;
              this.dataForm.executorRouteStrategy = data.executorRouteStrategy;
              this.dataForm.executorHandler = data.executorHandler;
              this.dataForm.executorParam = data.executorParam;
              this.dataForm.executorBlockStrategy = data.executorBlockStrategy;
              this.dataForm.executorTimeout = data.executorTimeout;
              this.dataForm.executorFailRetryCount =
                data.executorFailRetryCount;
              this.dataForm.glueType = data.glueType;
              this.dataForm.glueSource = data.glueSource;
              this.dataForm.glueRemark = data.glueRemark;
              this.dataForm.glueUpdatetime = data.glueUpdatetime;
              this.dataForm.childJobid = data.childJobid;
              this.dataForm.triggerStatus = data.triggerStatus;
              this.dataForm.triggerLastTime = data.triggerLastTime;
              this.dataForm.triggerNextTime = data.triggerNextTime;
            }
          });
        }
      });
    },
    // 表单提交
    dataFormSubmit() {
      this.$refs["dataForm"].validate((valid) => {
        if (valid) {
          this.$http({
            url: this.$http.adornUrl(
              `/jobinfo/${!this.dataForm.id ? "save" : "update"}`
            ),
            method: "post",
            data: this.$http.adornData({
              id: this.dataForm.id || undefined,
              jobGroup: this.dataForm.jobGroup,
              jobDesc: this.dataForm.jobDesc,
              addTime: this.dataForm.addTime,
              updateTime: this.dataForm.updateTime,
              author: this.dataForm.author,
              alarmEmail: this.dataForm.alarmEmail,
              scheduleType: this.dataForm.scheduleType,
              scheduleConf: this.dataForm.scheduleConf,
              misfireStrategy: this.dataForm.misfireStrategy,
              executorRouteStrategy: this.dataForm.executorRouteStrategy,
              executorHandler: this.dataForm.executorHandler,
              executorParam: this.dataForm.executorParam,
              executorBlockStrategy: this.dataForm.executorBlockStrategy,
              executorTimeout: this.dataForm.executorTimeout,
              executorFailRetryCount: this.dataForm.executorFailRetryCount,
              glueType: this.dataForm.glueType,
              glueSource: this.dataForm.glueSource,
              glueRemark: this.dataForm.glueRemark,
              glueUpdatetime: this.dataForm.glueUpdatetime,
              childJobid: this.dataForm.childJobid,
              triggerStatus: this.dataForm.triggerStatus,
              triggerLastTime: this.dataForm.triggerLastTime,
              triggerNextTime: this.dataForm.triggerNextTime,
            }),
          }).then(({ data }) => {
            if (data && data.code === 0) {
              this.$message({
                message: "操作成功",
                type: "success",
                duration: 1500,
                onClose: () => {
                  this.visible = false;
                  this.$emit("refreshDataList");
                },
              });
            } else {
              this.$message.error(data.msg);
            }
          });
        }
      });
    },
  },
};
</script>
