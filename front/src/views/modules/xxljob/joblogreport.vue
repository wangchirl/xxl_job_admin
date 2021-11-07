<template></template>
<style>
</style>
<script>
export default {
  data() {
    return {
      dataListLoading: false,
    };
  },
  activated() {
    this.getDataList();
    this.getEcharts();
  },
  methods: {
    // 获取数据列表
    getDataList() {
      this.dataListLoading = true;
      this.$http({
        url: this.$http.adornUrl("/index/init"),
        method: "get",
      }).then(({ data }) => {
        console.log("index init data ==>> ");
        console.log(data);
        this.dataListLoading = false;
      });
    },
    getEcharts() {
      this.$http({
        url: this.$http.adornUrl("/index/chartInfo"),
        method: "post",
        data: this.$http.adornData({
          startDate: '2021-10-30 00:00:00',
          endDate: '2021-11-06 23:59:59'
        }),
      }).then(({ data }) => {
        console.log("index chartInfo data ==>> ");
        console.log(data);
      });
    },
  },
};
</script>
