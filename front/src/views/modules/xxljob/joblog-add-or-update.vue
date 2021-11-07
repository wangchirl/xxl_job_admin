<template>
  <el-dialog
    :title="!dataForm.id ? '新增' : '修改'"
    :close-on-click-modal="false"
    :visible.sync="visible">
    <el-form :model="dataForm" :rules="dataRule" ref="dataForm" @keyup.enter.native="dataFormSubmit()" label-width="80px">
    <el-form-item label="执行器主键ID" prop="jobGroup">
      <el-input v-model="dataForm.jobGroup" placeholder="执行器主键ID"></el-input>
    </el-form-item>
    <el-form-item label="任务，主键ID" prop="jobId">
      <el-input v-model="dataForm.jobId" placeholder="任务，主键ID"></el-input>
    </el-form-item>
    <el-form-item label="执行器地址，本次执行的地址" prop="executorAddress">
      <el-input v-model="dataForm.executorAddress" placeholder="执行器地址，本次执行的地址"></el-input>
    </el-form-item>
    <el-form-item label="执行器任务handler" prop="executorHandler">
      <el-input v-model="dataForm.executorHandler" placeholder="执行器任务handler"></el-input>
    </el-form-item>
    <el-form-item label="执行器任务参数" prop="executorParam">
      <el-input v-model="dataForm.executorParam" placeholder="执行器任务参数"></el-input>
    </el-form-item>
    <el-form-item label="执行器任务分片参数，格式如 1/2" prop="executorShardingParam">
      <el-input v-model="dataForm.executorShardingParam" placeholder="执行器任务分片参数，格式如 1/2"></el-input>
    </el-form-item>
    <el-form-item label="失败重试次数" prop="executorFailRetryCount">
      <el-input v-model="dataForm.executorFailRetryCount" placeholder="失败重试次数"></el-input>
    </el-form-item>
    <el-form-item label="调度-时间" prop="triggerTime">
      <el-input v-model="dataForm.triggerTime" placeholder="调度-时间"></el-input>
    </el-form-item>
    <el-form-item label="调度-结果" prop="triggerCode">
      <el-input v-model="dataForm.triggerCode" placeholder="调度-结果"></el-input>
    </el-form-item>
    <el-form-item label="调度-日志" prop="triggerMsg">
      <el-input v-model="dataForm.triggerMsg" placeholder="调度-日志"></el-input>
    </el-form-item>
    <el-form-item label="执行-时间" prop="handleTime">
      <el-input v-model="dataForm.handleTime" placeholder="执行-时间"></el-input>
    </el-form-item>
    <el-form-item label="执行-状态" prop="handleCode">
      <el-input v-model="dataForm.handleCode" placeholder="执行-状态"></el-input>
    </el-form-item>
    <el-form-item label="执行-日志" prop="handleMsg">
      <el-input v-model="dataForm.handleMsg" placeholder="执行-日志"></el-input>
    </el-form-item>
    <el-form-item label="告警状态：0-默认、1-无需告警、2-告警成功、3-告警失败" prop="alarmStatus">
      <el-input v-model="dataForm.alarmStatus" placeholder="告警状态：0-默认、1-无需告警、2-告警成功、3-告警失败"></el-input>
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
          jobGroup: '',
          jobId: '',
          executorAddress: '',
          executorHandler: '',
          executorParam: '',
          executorShardingParam: '',
          executorFailRetryCount: '',
          triggerTime: '',
          triggerCode: '',
          triggerMsg: '',
          handleTime: '',
          handleCode: '',
          handleMsg: '',
          alarmStatus: ''
        },
        dataRule: {
          jobGroup: [
            { required: true, message: '执行器主键ID不能为空', trigger: 'blur' }
          ],
          jobId: [
            { required: true, message: '任务，主键ID不能为空', trigger: 'blur' }
          ],
          executorAddress: [
            { required: true, message: '执行器地址，本次执行的地址不能为空', trigger: 'blur' }
          ],
          executorHandler: [
            { required: true, message: '执行器任务handler不能为空', trigger: 'blur' }
          ],
          executorParam: [
            { required: true, message: '执行器任务参数不能为空', trigger: 'blur' }
          ],
          executorShardingParam: [
            { required: true, message: '执行器任务分片参数，格式如 1/2不能为空', trigger: 'blur' }
          ],
          executorFailRetryCount: [
            { required: true, message: '失败重试次数不能为空', trigger: 'blur' }
          ],
          triggerTime: [
            { required: true, message: '调度-时间不能为空', trigger: 'blur' }
          ],
          triggerCode: [
            { required: true, message: '调度-结果不能为空', trigger: 'blur' }
          ],
          triggerMsg: [
            { required: true, message: '调度-日志不能为空', trigger: 'blur' }
          ],
          handleTime: [
            { required: true, message: '执行-时间不能为空', trigger: 'blur' }
          ],
          handleCode: [
            { required: true, message: '执行-状态不能为空', trigger: 'blur' }
          ],
          handleMsg: [
            { required: true, message: '执行-日志不能为空', trigger: 'blur' }
          ],
          alarmStatus: [
            { required: true, message: '告警状态：0-默认、1-无需告警、2-告警成功、3-告警失败不能为空', trigger: 'blur' }
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
              url: this.$http.adornUrl(`/generator/joblog/info/${this.dataForm.id}`),
              method: 'get',
              params: this.$http.adornParams()
            }).then(({data}) => {
              if (data && data.code === 0) {
                this.dataForm.jobGroup = data.jobLog.jobGroup
                this.dataForm.jobId = data.jobLog.jobId
                this.dataForm.executorAddress = data.jobLog.executorAddress
                this.dataForm.executorHandler = data.jobLog.executorHandler
                this.dataForm.executorParam = data.jobLog.executorParam
                this.dataForm.executorShardingParam = data.jobLog.executorShardingParam
                this.dataForm.executorFailRetryCount = data.jobLog.executorFailRetryCount
                this.dataForm.triggerTime = data.jobLog.triggerTime
                this.dataForm.triggerCode = data.jobLog.triggerCode
                this.dataForm.triggerMsg = data.jobLog.triggerMsg
                this.dataForm.handleTime = data.jobLog.handleTime
                this.dataForm.handleCode = data.jobLog.handleCode
                this.dataForm.handleMsg = data.jobLog.handleMsg
                this.dataForm.alarmStatus = data.jobLog.alarmStatus
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
              url: this.$http.adornUrl(`/generator/joblog/${!this.dataForm.id ? 'save' : 'update'}`),
              method: 'post',
              data: this.$http.adornData({
                'id': this.dataForm.id || undefined,
                'jobGroup': this.dataForm.jobGroup,
                'jobId': this.dataForm.jobId,
                'executorAddress': this.dataForm.executorAddress,
                'executorHandler': this.dataForm.executorHandler,
                'executorParam': this.dataForm.executorParam,
                'executorShardingParam': this.dataForm.executorShardingParam,
                'executorFailRetryCount': this.dataForm.executorFailRetryCount,
                'triggerTime': this.dataForm.triggerTime,
                'triggerCode': this.dataForm.triggerCode,
                'triggerMsg': this.dataForm.triggerMsg,
                'handleTime': this.dataForm.handleTime,
                'handleCode': this.dataForm.handleCode,
                'handleMsg': this.dataForm.handleMsg,
                'alarmStatus': this.dataForm.alarmStatus
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
