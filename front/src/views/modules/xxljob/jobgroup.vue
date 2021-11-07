<template>
  <div class="mod-config">
    <el-form
      :inline="true"
      :model="dataForm"
      @keyup.enter.native="getDataList()"
    >
      <el-form-item>
        AppName
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
          placeholder="名称"
          clearable
        ></el-input>
      </el-form-item>
      <el-form-item>
        <el-button @click="getDataList()">搜索</el-button>
        <el-button type="primary" @click="addOrUpdateHandle()">新增</el-button>
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
        prop="appName"
        header-align="center"
        align="center"
        label="AppName"
      >
      </el-table-column>
      <el-table-column
        prop="title"
        header-align="center"
        align="center"
        label="名称"
      >
      </el-table-column>
      <el-table-column
        prop="addressType"
        header-align="center"
        align="center"
        label="注册方式"
      >
      </el-table-column>
      <el-table-column
        prop="addressList"
        header-align="center"
        align="center"
        label="OnLine 机器地址"
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
            >编辑</el-button
          >
          <el-button
            type="text"
            size="small"
            @click="deleteHandle(scope.row.id)"
            >删除</el-button
          >
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
import AddOrUpdate from "./jobgroup-add-or-update";
export default {
  data() {
    return {
      dataForm: {
        key: "",
      },
      dataList: [],
      jobGroup: 1,
      options: [{ label: 1, value: 1 }],
      start: 1,
      length: 10,
      totalPage: 0,
      dataListLoading: false,
      dataListSelections: [],
      addOrUpdateVisible: false,
    };
  },
  components: {
    AddOrUpdate,
  },
  activated() {
    this.getDataList();
  },
  methods: {
    // 编辑
    edit(id) {
      this.$http({
        url: this.$http.adornUrl("/jobgroup/save"),
        method: "post",
      }).then(({ data }) => {
        console.log("jobgroup save ==>> ");
        console.log(data);
        this.dataListLoading = false;
      });
    },
    // 更新
    edit(id) {
      this.$http({
        url: this.$http.adornUrl("/jobgroup/update"),
        method: "post",
      }).then(({ data }) => {
        console.log("jobgroup update ==>> ");
        console.log(data);
        this.dataListLoading = false;
      });
    },
    // 删除
    remove(id) {
      this.$http({
        url: this.$http.adornUrl("/jobgroup/remove"),
        method: "post",
      }).then(({ data }) => {
        console.log("jobgroup remove ==>> ");
        console.log(data);
        this.dataListLoading = false;
      });
    },
    // 获取数据列表
    getDataList() {
      this.dataListLoading = true;
      this.$http({
        url: this.$http.adornUrl("/jobgroup/pageList"),
        method: "post",
      }).then(({ data }) => {
        console.log("jobinfo pageList ==>> ");
        console.log(data);
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
    // 新增 / 修改
    addOrUpdateHandle(id) {
      this.addOrUpdateVisible = true;
      this.$nextTick(() => {
        this.$refs.addOrUpdate.init(id);
      });
    },
  },
};
</script>
