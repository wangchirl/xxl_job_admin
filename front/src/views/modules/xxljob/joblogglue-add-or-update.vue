<template>
  <el-dialog
    :title="!dataForm.id ? '新增' : '修改'"
    :close-on-click-modal="false"
    :visible.sync="visible">
    <el-form :model="dataForm" :rules="dataRule" ref="dataForm" @keyup.enter.native="dataFormSubmit()" label-width="80px">
    <el-form-item label="任务，主键ID" prop="jobId">
      <el-input v-model="dataForm.jobId" placeholder="任务，主键ID"></el-input>
    </el-form-item>
    <el-form-item label="GLUE类型" prop="glueType">
      <el-input v-model="dataForm.glueType" placeholder="GLUE类型"></el-input>
    </el-form-item>
    <el-form-item label="GLUE源代码" prop="glueSource">
      <el-input v-model="dataForm.glueSource" placeholder="GLUE源代码"></el-input>
    </el-form-item>
    <el-form-item label="GLUE备注" prop="glueRemark">
      <el-input v-model="dataForm.glueRemark" placeholder="GLUE备注"></el-input>
    </el-form-item>
    <el-form-item label="" prop="addTime">
      <el-input v-model="dataForm.addTime" placeholder=""></el-input>
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
          jobId: '',
          glueType: '',
          glueSource: '',
          glueRemark: '',
          addTime: '',
          updateTime: ''
        },
        dataRule: {
          jobId: [
            { required: true, message: '任务，主键ID不能为空', trigger: 'blur' }
          ],
          glueType: [
            { required: true, message: 'GLUE类型不能为空', trigger: 'blur' }
          ],
          glueSource: [
            { required: true, message: 'GLUE源代码不能为空', trigger: 'blur' }
          ],
          glueRemark: [
            { required: true, message: 'GLUE备注不能为空', trigger: 'blur' }
          ],
          addTime: [
            { required: true, message: '不能为空', trigger: 'blur' }
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
              url: this.$http.adornUrl(`/generator/joblogglue/info/${this.dataForm.id}`),
              method: 'get',
              params: this.$http.adornParams()
            }).then(({data}) => {
              if (data && data.code === 0) {
                this.dataForm.jobId = data.jobLogglue.jobId
                this.dataForm.glueType = data.jobLogglue.glueType
                this.dataForm.glueSource = data.jobLogglue.glueSource
                this.dataForm.glueRemark = data.jobLogglue.glueRemark
                this.dataForm.addTime = data.jobLogglue.addTime
                this.dataForm.updateTime = data.jobLogglue.updateTime
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
              url: this.$http.adornUrl(`/generator/joblogglue/${!this.dataForm.id ? 'save' : 'update'}`),
              method: 'post',
              data: this.$http.adornData({
                'id': this.dataForm.id || undefined,
                'jobId': this.dataForm.jobId,
                'glueType': this.dataForm.glueType,
                'glueSource': this.dataForm.glueSource,
                'glueRemark': this.dataForm.glueRemark,
                'addTime': this.dataForm.addTime,
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
