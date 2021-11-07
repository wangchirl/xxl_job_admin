<template>
  <div class="mod-config">
    <el-form
      :inline="true"
      :model="dataForm"
      @keyup.enter.native="getDataList()"
    >
      <el-form-item>
        执行器
        <el-select
          v-model="jobGroup"
          placeholder="请选择"
          default-first-option=true
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
      <el-form-item>
        状态
        <el-select
          v-model="triggerStatus"
          placeholder="请选择"
          default-first-option=true
        >
          <el-option
            v-for="item in triggerStatuses"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          >
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-input
          v-model="dataForm.jobDesc"
          placeholder="请输入描述信息"
          clearable
        ></el-input>
      </el-form-item>
      <el-form-item>
        <el-input
          v-model="dataForm.executorHandler"
          placeholder="请输入JobHandler"
          clearable
        ></el-input>
      </el-form-item>
      <el-form-item>
        <el-input
          v-model="dataForm.author"
          placeholder="请输入负责人"
          clearable
        ></el-input>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="getDataList()">搜索</el-button>
        <el-button type="success" @click="addOrUpdateHandle()">新增</el-button>
      </el-form-item>
    </el-form>
    <el-table
      :data="dataList"
      border
      v-loading="dataListLoading"
      @selection-change="selectionChangeHandle"
      style="width: 100%"
    >
      <el-table-column
        prop="jobGroup"
        header-align="center"
        align="center"
        label="任务ID"
      >
      </el-table-column>
      <el-table-column
        prop="jobDesc"
        header-align="center"
        align="center"
        label="任务描述"
      >
      </el-table-column>
      <el-table-column
        prop="scheduleType"
        header-align="center"
        align="center"
        label="调度类型"
      >
      </el-table-column>

      <el-table-column
        prop="executorHandler"
        header-align="center"
        align="center"
        label="运行模式"
      >
      </el-table-column>
      <el-table-column
        prop="author"
        header-align="center"
        align="center"
        label="负责人"
      >
      </el-table-column>

      <el-table-column
        prop="triggerStatus"
        header-align="center"
        align="center"
        label="状态"
      >
      </el-table-column>
      <el-table-column
        fixed="right"
        header-align="center"
        align="center"
        width="150"
        label="操作"
      >
        <template slot-scope="scope">
          <el-dropdown class="avatar-container" trigger="click">
            <div class="avatar-wrapper">
              <el-button type="primary" size="medium">
                操作
                <i class="el-icon-caret-bottom" />
              </el-button>
            </div>
            <el-dropdown-menu slot="dropdown" class="user-dropdown">
              <el-dropdown-item>
                <el-button
                  type="text"
                  size="small"
                  @click="trigger(scope.row.id)"
                  >执行一次</el-button
                >
              </el-dropdown-item>
              <el-dropdown-item>
                <el-button
                  type="text"
                  size="small"
                  @click="checklog(scope.row.id)"
                  >查看日志</el-button
                >
              </el-dropdown-item>
              <el-dropdown-item divided>
                <el-button
                  type="text"
                  size="small"
                  @click="registries(scope.row.id)"
                  >注册节点</el-button
                >
              </el-dropdown-item>
              <el-dropdown-item
                divided
                v-if="
                  !(
                    scope.row.scheduleType != 'CRON' &&
                    scope.row.scheduleType != 'FIX_RATE'
                  )
                "
              >
                <el-button
                  type="text"
                  size="small"
                  @click="
                    nextTrigger(scope.row.scheduleType, scope.row.scheduleConf)
                  "
                  >下次执行时间</el-button
                >
              </el-dropdown-item>
              <el-dropdown-item divided>
                <el-button
                  type="text"
                  size="small"
                  @click="startOrStop(scope.row.id, scope.row.triggerStatus)"
                  >{{
                    scope.row.triggerStatus == 1 ? "停止" : "启动"
                  }}</el-button
                >
              </el-dropdown-item>
              <el-dropdown-item divided>
                <el-button
                  type="text"
                  size="small"
                  @click="addOrUpdateHandle(scope.row.id)"
                  >编辑</el-button
                >
              </el-dropdown-item>
              <el-dropdown-item divided>
                <el-button
                  type="text"
                  size="small"
                  @click="remove(scope.row.id)"
                  >删除</el-button
                >
              </el-dropdown-item>
              <el-dropdown-item divided>
                <el-button type="text" size="small" @click="copy(scope.row.id)"
                  >复制</el-button
                >
              </el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination
      @size-change="sizeChangeHandle"
      @current-change="currentChangeHandle"
      :current-page="start"
      :page-sizes="[10, 20, 50, 100]"
      :page-size="length"
      :total="totalPage"
      layout="total, sizes, prev, pager, next, jumper"
    >
    </el-pagination>
    <!-- 弹窗, 新增 / 修改 -->
    <add-or-update
      v-if="addOrUpdateVisible"
      ref="addOrUpdate"
      @refreshDataList="getDataList"
    ></add-or-update>
  </div>
</template>

<script>
import AddOrUpdate from "./jobinfo-add-or-update";
export default {
  data() {
    return {
      dataForm: {
        jobDesc: "",
        executorHandler: "",
        author: "",
      },
      jobGroup: "", // 默认第一个，如果有的话
      triggerStatuses: [
        { label: "全部", value: -1 },
        { label: "停止", value: 0 },
        { label: "启动", value: 1 },
      ],
      triggerStatus: -1, // 默认全部
      dataList: [],
      start: 0,
      length: 10,
      totalPage: 0,
      dataListLoading: false,
      addOrUpdateVisible: false,
      enumList: {
        jobGroupList: [], // 执行器列表
        executorRouteStrategyEnums: [],
        glueTypeEnums: [],
        executorBlockStrategyEnums: [],
        scheduleTypeEnums: [],
        misfireStrategyEnums: [],
      },
      addressList: "",
      nextTriggers: [],
    };
  },
  components: {
    AddOrUpdate,
  },
  activated() {
    this.init();
  },
  methods: {
    // 新增 / 修改
    addOrUpdateHandle(id) {
      this.addOrUpdateVisible = true;
      this.$nextTick(() => {
        this.$refs.addOrUpdate.init(id,this.enumList);
      });
    },
    // 复制，新增逻辑，不传id
    copy(id) {
      this.$http({
        url: this.$http.adornUrl("/jobinfo/save"),
        method: "post",
      }).then(({ data }) => {
        console.log("jobinfo copy ==>> ");
        console.log(data);
        this.dataListLoading = false;
      });
    },
    // 删除
    remove(id) {
      this.$http({
        url: this.$http.adornUrl("/jobinfo/remove"),
        method: "post",
      }).then(({ data }) => {
        console.log("jobinfo remove ==>> ");
        console.log(data);
        this.dataListLoading = false;
      });
    },
    // 编辑
    edit(id) {
      this.$http({
        url: this.$http.adornUrl("/jobinfo/save"),
        method: "post",
      }).then(({ data }) => {
        console.log("jobinfo save ==>> ");
        console.log(data);
        this.dataListLoading = false;
      });
    },
    // 启动 or 停止
    startOrStop(id, status) {
      var uri = status === 1 ? "stop" : "start";
      this.$http({
        url: this.$http.adornUrl(`/jobinfo/${uri}`),
        method: "post",
        data: this.$http.adornData(
          {
            id: id,
          },
          true,
          "form"
        ),
        headers: {
          "Content-Type": "application/x-www-form-urlencoded;charset=UTF-8",
        },
      }).then(({ data }) => {
        console.log("jobinfo " + uri + " ==>> ");
        console.log(data);
        this.getDataList();
        this.dataListLoading = false;
      });
    },
    // 下次执行时间
    nextTrigger(type, conf) {
      this.$http({
        url: this.$http.adornUrl("/jobinfo/nextTriggerTime"),
        method: "post",
        data: this.$http.adornData(
          {
            scheduleType: type,
            scheduleConf: conf,
          },
          true,
          "form"
        ),
        headers: {
          "Content-Type": "application/x-www-form-urlencoded;charset=UTF-8",
        },
      }).then(({ data }) => {
        console.log("jobinfo nextTriggerTime ==>> ");
        console.log(data);
        this.nextTriggers = data.content;
        console.log(this.nextTriggers);
        this.dataListLoading = false;
      });
    },
    // 注册节点
    registries(id) {
      this.$http({
        url: this.$http.adornUrl("/jobgroup/loadById"),
        method: "post",
        data: this.$http.adornData(
          {
            id: id,
          },
          true,
          "form"
        ),
        headers: {
          "Content-Type": "application/x-www-form-urlencoded;charset=UTF-8",
        },
      }).then(({ data }) => {
        console.log("jobgroup registries ==>> ");
        console.log(data);
        this.addressList = data.content.addressList;
        console.log(this.addressList);
        this.dataListLoading = false;
      });
    },
    // TODO 查看日志
    checklog(id) {
      let href = window.location.href; //当前页面域名
      let params = `xxljob-logdetail?id=${id}`; //跳转页面及传参
      let url = href.replace("xxljob-jobinfo", params); //替换
      window.open(url, "_blank");
      console.log("jobinfo checklog ==>> ");
    },
    // 执行一次
    trigger(id) {
      this.$http({
        url: this.$http.adornUrl("/jobinfo/trigger"),
        method: "post",
        data: this.$http.adornData(
          {
            id: id,
          },
          true,
          "form"
        ),
        headers: {
          "Content-Type": "application/x-www-form-urlencoded;charset=UTF-8",
        },
      }).then(({ data }) => {
        console.log("jobinfo trigger ==>> ");
        console.log(data);
        this.dataListLoading = false;
      });
    },
    // 获取数据列表
    getDataList() {
      this.$http({
        url: this.$http.adornUrl("/jobinfo/pageList"),
        method: "post",
        data: this.$http.adornData(
          {
            jobGroup: this.jobGroup,
            triggerStatus: this.triggerStatus,
            jobDesc: this.dataForm.jobDesc,
            executorHandler: this.dataForm.executorHandler,
            author: this.dataForm.author,
            start: this.start,
            length: this.length,
          },
          true,
          "form"
        ),
        headers: {
          "Content-Type": "application/x-www-form-urlencoded;charset=UTF-8",
        },
      }).then(({ data }) => {
        console.log("jobinfo pageList ==>> ");
        console.log(data);
        this.dataList = data.data;
        this.totalPage = data.recordsTotal; // 总记录数
        this.dataListLoading = false;
      });
    },
    // 初始化
    init() {
      this.dataListLoading = true;
      this.$http({
        url: this.$http.adornUrl("/jobinfo/init"),
        method: "get",
      }).then(({ data }) => {
        this.enumList.executorRouteStrategyEnums = data.executorRouteStrategyEnums;
        this.enumList.glueTypeEnums = data.glueTypeEnums;
        this.enumList.executorBlockStrategyEnums = data.executorBlockStrategyEnums;
        this.enumList.scheduleTypeEnums = data.scheduleTypeEnums;
        this.enumList.misfireStrategyEnums = data.misfireStrategyEnums;
        this.enumList.jobGroupList = data.jobGroupList; // 执行器
        if (this.enumList.jobGroupList.length > 0) {
          this.jobGroup = data.jobGroupList[0].id;
        }
        this.getDataList(); // 加载列表数据
        this.dataListLoading = false;
      });
    },
    // 每页数
    sizeChangeHandle(val) {
      this.length = val;
      this.start = 1;
      this.getDataList();
    },
    // 当前页
    currentChangeHandle(val) {
      this.start = val;
      this.getDataList();
    },
    // 多选
    selectionChangeHandle(val) {
      this.dataListSelections = val;
    },
  },
};
</script>
