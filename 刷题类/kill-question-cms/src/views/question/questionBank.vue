<!--
 * @Author: FengXue
 * @Date: 2024-04-09 23:50:32
 * @LastEditors: FengXue
 * @LastEditTime: 2024-05-16 10:58:30
 * @filePath: Do not edit
-->
<template>
  <div class="init">
    <div class="title">题库管理</div>
    <el-divider direction="horizontal" content-position="left"></el-divider>
    <div class="function-nav">
      <el-button type="primary" @click="addQuestion">添加题库</el-button>
      <el-popconfirm :title="'是否确认删除' + checkList.length + '项数据?'" confirmButtonText="确认" cancelButtonText="取消"
        confirmButtonType="primary" cancelButtonType="text" icon="el-icon-question" iconColor="#f90" hideIcon="false"
        @confirm="handleBatchDelete">
        <template #reference>
          <el-button type="danger" size="default" @click="">批量删除</el-button>
        </template>
      </el-popconfirm>
    </div>
    <div class="init-main">
      <el-scrollbar>
        <el-table :data="tableData" style="width: 100%" @selection-change="handleSelectionChange">
          <el-table-column type="selection" width="55"></el-table-column>
          <el-table-column label="序号" width="80">
            <template #default="scope">
              {{ scope.$index + 1 }}
            </template>
          </el-table-column>

          <el-table-column prop="questionBankName" label="题库名称" width="auto"></el-table-column>
          <el-table-column prop="" width="300">
            <template #default="scope">
              <div class="group">
                <el-button type="primary" size="mini" @click="handleEdit(scope.row)">编辑</el-button>
                <el-button type="primary" size="mini" @click="handleQuestion(scope.row)">编辑题目</el-button>
                <el-popconfirm :title="'是否确认删除?'" confirmButtonText="确认" cancelButtonText="取消"
                  confirmButtonType="primary" cancelButtonType="text" icon="el-icon-question" iconColor="#f90"
                  hideIcon="false" @confirm="handleDelete(scope.row)">
                  <template #reference>
                    <el-button type="danger" size="mini" @click="">删除</el-button>
                  </template>
                </el-popconfirm>
              </div>
            </template>
          </el-table-column>
        </el-table>
      </el-scrollbar>
    </div>

    <!-- 分页 -->
    <div class="pagination">
      <el-pagination v-model:current-page="page.pageNum" v-model:page-size="page.pageSize"
        :page-sizes="[10, 20, 30, 40, 50]" layout="total, sizes, prev, pager, next, jumper" :total="page.total"
        @size-change="handleSizeChange" @current-change="handleCurrentChange" />
    </div>
    <!-- 编辑 -->
    <el-dialog title="编辑题库" v-model="isEdit" width="50%" :show-close="false" :close-on-click-modal="false">
      <el-form :model="questionBank" ref="questionForm" :rules="rules" label-width="100px" :inline="false"
        size="normal">
        <el-form-item label="题库名称" prop="questionBankName">
          <el-input v-model="questionBank.questionBankName" clearable></el-input>
        </el-form-item>
        <el-form-item label="题库类型" size="normal">
          <el-radio-group v-model="questionBank.type" @change="handleRadioChange">
            <!-- <el-radio label="0" name="type" size="large">个人</el-radio>
            <el-radio label="1" name="type" size="large">共享</el-radio> -->

            <el-radio :value="0" size="large">个人</el-radio>
            <el-radio :value="1" size="large">共享</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="" size="normal">
          <el-button @click="isEdit = false">取消</el-button>
          <el-button type="primary" @click="submitForm(questionForm)">保存</el-button>
        </el-form-item>
      </el-form>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ElMessage, FormInstance, FormRules } from "element-plus";
import { onMounted, reactive } from "vue";
import Axios from "../../request";
import { useMainStore } from "@/stores/index";
onMounted(() => {
  getQuestionList();
});

const router = useRouter();
const store = useMainStore();
const tableData = ref([]); //表格数据
const checkList = ref([]); //已选择数据
const page = ref({
  pageNum: 1,
  pageSize: 20,
  total: 0,
});
const handleSizeChange = (val: number) => {
  page.value.pageSize = val;
  getQuestionList();
};
const handleCurrentChange = (val: number) => {
  page.value.pageNum = val;
  getQuestionList();
};
const getQuestionList = () => {
  Axios({
    url: "/questionBank/findQuestionBankByPage",
    params: { ...page.value },
  }).then((res) => {
    tableData.value = res.data.records;
    page.value.total = res.data.total;
  });
};
interface questionBank {
  questionBankId: string;
  questionBankName: string;
  userId: string;
  author: string;
  type: number;
}
const questionBank = ref<questionBank>({
  questionBankId: "",
  questionBankName: "",
  userId: store.user.userId,
  author: store.user.nickName,
  type: 0,
});
const questionForm = ref<FormInstance>();
//规则校验
const rules = reactive<FormRules<questionBank>>({
  questionBankName: [
    { required: true, message: "请输入题库名称", trigger: "blur" },
  ],
});
//监听选择
const handleSelectionChange = (val: any) => {
  checkList.value = val;
};
const isEdit = ref(false);
//切换类型
const handleRadioChange = (e) => { };
//新增
const addQuestion = () => {
  isEdit.value = true;
  isChange.value = false;
  questionBank.value.questionBankName = "";
  questionBank.value.type = 0;
};
const submitForm = async (formEl: FormInstance | undefined) => {
  if (!formEl) return;
  await formEl.validate((valid) => {
    if (valid) {
      if (isChange.value) {
        Axios({
          url:
            "/questionBank/updateQuestionBank",
          method: "POST",
          data: questionBank.value,
        }).then((res) => {
          if (res.data.code == 200) {
            isEdit.value = false;
            ElMessage.success("保存成功");
            getQuestionList();
          } else {
            ElMessage.error(res.data.msg);
          }
        });
        return;
      }
      Axios({
        url: "/questionBank/create",
        method: "POST",
        data: questionBank.value,
      }).then((res) => {
        if (res.data.code == 200) {
          isEdit.value = false;
          ElMessage.success("保存成功");
          getQuestionList();
        } else {
          ElMessage.error(res.data.msg);
        }
      });
    }
  });
};
const isChange = ref(false);
const handleEdit = (row: any) => {
  isEdit.value = true;
  questionBank.value = row;
  isChange.value = true;
};
const handleBatchDelete = () => {
  if (checkList.value.length == 0) {
    ElMessage.warning("请选择要删除的数据");
    return;
  }
  handleDelete(checkList.value);
};
const handleDelete = (row: any) => {
  row = row instanceof Array ? row : [row];
  Axios({
    url: "/questionBank/delQuestionBankById",
    method: "POST",
    data: row,
  }).then((res) => {
    if (res.data.code == 200) {
      ElMessage.success("删除成功");
      getQuestionList();
    }
  });
};

const handleQuestion = (row: any) => {
  router.push({
    name: "question",
    params: { questionBank: JSON.stringify(row) },
  });
};
</script>
<style lang="scss" scoped></style>
