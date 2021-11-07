<template>
  <div class="mod-config">
    <el-form
      :inline="true"
      :model="dataForm"
      @keyup.enter.native="getDataList()"
    >
      <el-form-item>
        执行器
        <el-select v-model="jobGroup" placeholder="请选择">
          <el-option
            v-for="item in options"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          >
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item>
        任务
        <el-select v-model="jobGroup" placeholder="请选择">
          <el-option
            v-for="item in options"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          >
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item>
        状态
        <el-select v-model="jobGroup" placeholder="请选择">
          <el-option
            v-for="item in options"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          >
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item>
        调度时间
        <el-select v-model="jobGroup" placeholder="请选择">
          <el-option
            v-for="item in options"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          >
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-input
          v-model="dataForm.key"
          placeholder="参数名"
          clearable
        ></el-input>
      </el-form-item>
      <el-form-item>
        <el-button @click="getDataList()">搜索</el-button>
        <el-button
          v-if="isAuth('generator:joblog:save')"
          type="primary"
          @click="addOrUpdateHandle()"
          >清理</el-button
        >
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
        prop="jobId"
        header-align="center"
        align="center"
        label="任务ID"
      >
      </el-table-column>
      <el-table-column
        prop="executorAddress"
        header-align="center"
        align="center"
        label="调度时间"
      >
      </el-table-column>
      <el-table-column
        prop="executorHandler"
        header-align="center"
        align="center"
        label="调度结果"
      >
      </el-table-column>
      <el-table-column
        prop="executorParam"
        header-align="center"
        align="center"
        label="调度备注"
      >
      </el-table-column>
      <el-table-column
        prop="executorShardingParam"
        header-align="center"
        align="center"
        label="执行时间"
      >
      </el-table-column>
      <el-table-column
        prop="executorFailRetryCount"
        header-align="center"
        align="center"
        label="执行结果"
      >
      </el-table-column>
      <el-table-column
        prop="triggerTime"
        header-align="center"
        align="center"
        label="执行备注"
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
          <el-button
            type="text"
            size="small"
            @click="addOrUpdateHandle(scope.row.id)"
            >执行日志</el-button
          >
          <el-button
            type="text"
            size="small"
            @click="deleteHandle(scope.row.id)"
            >停止执行</el-button
          >
        </template>
      </el-table-column>
    </el-table>
    <el-pagination
      @size-change="sizeChangeHandle"
      @current-change="currentChangeHandle"
      :current-page="pageIndex"
      :page-sizes="[10, 20, 50, 100]"
      :page-size="pageSize"
      :total="totalPage"
      layout="total, sizes, prev, pager, next, jumper"
    >
    </el-pagination>
  </div>
</template>

<script>
export default {
  data() {
    return {
      dataForm: {
        key: "",
      },
      dataList: [],
      jobGroup: 1,
      options: [{ label: 1, value: 1 }],
      pageIndex: 1,
      pageSize: 10,
      totalPage: 0,
      dataListLoading: false,
      dataListSelections: [],
      addOrUpdateVisible: false,
    };
  },
  activated() {
    this.init();
  },
  methods: {
    // 分页查询
    getDataList() {
      this.$http({
        url: this.$http.adornUrl("/joblog/pageList"),
        method: "post",
      }).then(({ data }) => {
        console.log("jobinfo pageList ==>> ");
        console.log(data);
        this.dataListLoading = false;
      });
    },
    // 获取数据列表
    init() {
      this.dataListLoading = true;
      this.$http({
        url: this.$http.adornUrl("/joblog/init"),
        method: "get",
      }).then(({ data }) => {
        console.log("joblog init ==>> ");
        console.log(data);
        this.dataListLoading = false;
      });
    },
    // 每页数
    sizeChangeHandle(val) {
      this.pageSize = val;
      this.pageIndex = 1;
      this.getDataList();
    },
    // 当前页
    currentChangeHandle(val) {
      this.pageIndex = val;
      this.getDataList();
    },
    // 多选
    selectionChangeHandle(val) {
      this.dataListSelections = val;
    }, // 执行日志页
    logDetailPage() {
      Location.href = "";
    },
    // 滚动日志
    logDetailCat() {
      this.$http({
        url: this.$http.adornUrl("/jobinfo/logDetailCat"),
        method: "post",
      }).then(({ data }) => {
        console.log("jobinfo logDetailCat ==>> ");
        console.log(data);
        this.dataListLoading = false;
      });
    },
    // 停止执行
    killJob() {
      this.$http({
        url: this.$http.adornUrl("/jobinfo/logKill"),
        method: "post",
      }).then(({ data }) => {
        console.log("jobinfo logKill ==>> ");
        console.log(data);
        this.dataListLoading = false;
      });
    },
    // 清理日志
    clearLog() {
      this.$http({
        url: this.$http.adornUrl("/jobinfo/clearLog"),
        method: "post",
      }).then(({ data }) => {
        console.log("jobinfo clearLog ==>> ");
        console.log(data);
        this.dataListLoading = false;
      });
    },
  },
};
</script>
