<template>
  <el-dialog
    :title="!dataForm.id ? '新增' : '修改'"
    :close-on-click-modal="false"
    :visible.sync="visible">
    <el-form :model="dataForm" :rules="dataRule" ref="dataForm" @keyup.enter.native="dataFormSubmit()" label-width="80px">
    <el-form-item label="执行器AppName" prop="appName">
      <el-input v-model="dataForm.appName" placeholder="执行器AppName"></el-input>
    </el-form-item>
    <el-form-item label="执行器名称" prop="title">
      <el-input v-model="dataForm.title" placeholder="执行器名称"></el-input>
    </el-form-item>
    <el-form-item label="执行器地址类型：0=自动注册、1=手动录入" prop="addressType">
      <el-input v-model="dataForm.addressType" placeholder="执行器地址类型：0=自动注册、1=手动录入"></el-input>
    </el-form-item>
    <el-form-item label="执行器地址列表，多地址逗号分隔" prop="addressList">
      <el-input v-model="dataForm.addressList" placeholder="执行器地址列表，多地址逗号分隔"></el-input>
    </el-form-item>
    <el-form-item label="" prop="updateTime">
      <el-input v-model="dataForm.updateTime" placeholder=""></el-input>
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
    data () {
      return {
        visible: false,
        dataForm: {
          id: 0,
          appName: '',
          title: '',
          addressType: '',
          addressList: '',
          updateTime: ''
        },
        dataRule: {
          appName: [
            { required: true, message: '执行器AppName不能为空', trigger: 'blur' }
          ],
          title: [
            { required: true, message: '执行器名称不能为空', trigger: 'blur' }
          ],
          addressType: [
            { required: true, message: '执行器地址类型：0=自动注册、1=手动录入不能为空', trigger: 'blur' }
          ],
          addressList: [
            { required: true, message: '执行器地址列表，多地址逗号分隔不能为空', trigger: 'blur' }
          ],
          updateTime: [
            { required: true, message: '不能为空', trigger: 'blur' }
          ]
        }
      }
    },
    methods: {
      init (id) {
        this.dataForm.id = id || 0
        this.visible = true
        this.$nextTick(() => {
          this.$refs['dataForm'].resetFields()
          if (this.dataForm.id) {
            this.$http({
              url: this.$http.adornUrl(`/generator/jobgroup/info/${this.dataForm.id}`),
              method: 'get',
              params: this.$http.adornParams()
            }).then(({data}) => {
              if (data && data.code === 0) {
                this.dataForm.appName = data.jobGroup.appName
                this.dataForm.title = data.jobGroup.title
                this.dataForm.addressType = data.jobGroup.addressType
                this.dataForm.addressList = data.jobGroup.addressList
                this.dataForm.updateTime = data.jobGroup.updateTime
              }
            })
          }
        })
      },
      // 表单提交
      dataFormSubmit () {
        this.$refs['dataForm'].validate((valid) => {
          if (valid) {
            this.$http({
              url: this.$http.adornUrl(`/generator/jobgroup/${!this.dataForm.id ? 'save' : 'update'}`),
              method: 'post',
              data: this.$http.adornData({
                'id': this.dataForm.id || undefined,
                'appName': this.dataForm.appName,
                'title': this.dataForm.title,
                'addressType': this.dataForm.addressType,
                'addressList': this.dataForm.addressList,
                'updateTime': this.dataForm.updateTime
              })
            }).then(({data}) => {
              if (data && data.code === 0) {
                this.$message({
                  message: '操作成功',
                  type: 'success',
                  duration: 1500,
                  onClose: () => {
                    this.visible = false
                    this.$emit('refreshDataList')
                  }
                })
              } else {
                this.$message.error(data.msg)
              }
            })
          }
        })
      }
    }
  }
</script>
