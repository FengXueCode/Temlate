<!--
 * @Author: FengXue
 * @Date: 2024-04-09 23:50:32
 * @LastEditors: FengXue
 * @LastEditTime: 2024-06-03 15:13:57
 * @filePath: Do not edit
-->
<template>
  <div class="init">
    <el-page-header @back="goBack" :icon="ArrowLeft">
      <template #content>
        <div class="header">{{ questionBank.questionBankName }}</div>
      </template>
    </el-page-header>
    <el-divider direction="horizontal" content-position="left"></el-divider>
    <div class="function-nav">
      <el-button type="primary" @click="addQuestion">添加题目</el-button>
      <el-button type="primary" @click="download">下载导入模板</el-button>
      <div class="margin"></div>
      <el-upload ref="upload" class="upload-demo" :action="url" :limit="1" :auto-upload="true" name="file"
        :data="upload" accept=".xlsx" :on-success="succesUpload" :on-error="successError" :headers="headers">
        <template #trigger>
          <el-button type="success">导入题目</el-button>
        </template>
      </el-upload>
    </div>
    <div class="init-main">
      <el-scrollbar>
        <div class="list">
          <div class="item" v-for="(item, index) in list">
            <div class="title">
              <div class="title-content">
                <div>[{{ index + 1 }}/{{ list.length }}</div>
                <div style="margin-left: 10px">
                  {{
                    item.state === 0
                      ? "单选"
                      : item.state === 1
                        ? "多选"
                        : item.state === 2
                          ? "判断"
                          : item.state === 3
                            ? "填空"
                            : "未知"
                  }}]
                </div>

                <div style="margin-left: 10px">{{ item.questionTitle }}</div>
              </div>
              <div class="btns">
                <el-icon @click="handleEdit(item)">
                  <EditPen />
                </el-icon>
                <el-popconfirm title="是否确认删除" confirmButtonText="确认" cancelButtonText="取消" confirmButtonType="primary"
                  cancelButtonType="text" icon="el-icon-question" iconColor="#f90" hideIcon="false"
                  @confirm="handleDelete(item)">
                  <template #reference>
                    <el-icon color="red">
                      <Delete />
                    </el-icon>
                  </template>
                </el-popconfirm>
              </div>
            </div>
            <div v-if="item.state == 0 || item.state == 1" class="options">
              <div class="item" v-for="(option, index) in item.options">
                <div class="title">{{ option.title }}、</div>
                <div class="content">{{ option.content }}</div>
              </div>
            </div>
            <div class="answer">
              <div class="content" v-if="item.state == 3">
                参考答案：
                <span v-for="(answer, index) in item.answer">{{ answer.content
                }}{{ index !== item.answer.length - 1 ? "," : "" }}</span>
              </div>
              <div class="content" v-else>
                参考答案：{{ item.state == 2 ? item.answer : item.parseAnswer }}
              </div>
            </div>
            <div class="analysis">题目解析：{{ item.analysis }}</div>

            <el-divider />
          </div>
        </div>
      </el-scrollbar>
    </div>

    <!-- 分页 -->

    <!-- 编辑 -->
    <el-dialog title="编辑题目" v-model="isEdit" width="50%" :show-close="false" :close-on-click-modal="false">
      <el-form :model="question" ref="questionForm" :rules="rules" label-width="100px" :inline="false" size="normal">
        <el-form-item label="题目类型" size="normal" prop="state">
          <el-select v-model="question.state" placeholder="选中题目类型">
            <el-option label="单选" :value="0"></el-option>
            <el-option label="多选" :value="1"></el-option>
            <el-option label="判断" :value="2"></el-option>
            <el-option label="填空" :value="3"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="题目标题" prop="questionTitle">
          <el-input v-model="question.questionTitle" clearable @input="disposeTitle"></el-input>
          <div class="tip" v-if="question.state == 3">
            提示：中国四大名著__、__、__、__。(俩个下划线为一个空)
          </div>
        </el-form-item>
        <el-form-item label="题目选项" prop="options" v-if="question.state == 0 || question.state == 1">
          <div class="option" v-for="(item, index) in question.options" :key="index">
            <div class="item-title">{{ item.title }}</div>
            <div class="item-input">
              <el-input v-model="item.content" placeholder="请输入选项内容"></el-input>
            </div>
            <el-icon :size="24" color="red" v-if="index != question.options.length - 1 || index === 6"
              @click="delOption">
              <Remove />
            </el-icon>
            <el-icon :size="24" v-if="index === question.options.length - 1 && index != 6" @click="addOption">
              <CirclePlus />
            </el-icon>
          </div>
        </el-form-item>
        <el-form-item label="参考答案" prop="answer">
          <div class="item" v-if="question.state === 0">
            <el-select v-model="question.answer" placeholder="选中参考答案">
              <el-option v-for="item in question.options" :key="item.title" :label="item.title" :value="item.content">
              </el-option>
            </el-select>
          </div>
          <div class="item" v-if="question.state === 1">
            <el-select multiple v-model="question.answer" placeholder="选中参考答案">
              <el-option v-for="item in question.options" :key="item.content" :label="item.content"
                :value="item.content">
              </el-option>
            </el-select>
          </div>
          <div class="item" v-if="question.state === 2">
            <el-radio-group v-model="question.answer">
              <el-radio value="错" size="large">错</el-radio>
              <el-radio value="对" size="large">对</el-radio>
            </el-radio-group>
          </div>
          <div class="item" v-if="question.state === 3">
            <div class="answer" v-for="(item, index) in question.answer">
              <div class="title">{{ item.title }}</div>
              <div class="input">
                <el-input v-model="item.content" placeholder="请输入选项内容" size="normal" clearable></el-input>
              </div>
            </div>
          </div>
        </el-form-item>
        <el-form-item label="题目解析" prop="questionName">
          <el-input type="textarea" v-model="question.analysis" clearable></el-input>
        </el-form-item>
        <el-form-item label="" size="normal">
          <el-button @click="cancelEdit">取消</el-button>
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
import { ArrowLeft } from "@element-plus/icons-vue";
onMounted(() => {
  getQuestionList();
});
const route = useRoute();
const router = useRouter();
const goBack = () => {
  router.go(-1);
};
const questionBank = JSON.parse(route.params.questionBank);
const store = useMainStore();
const list = ref([]); //表格数据

interface question {
  questionId: string;
  questionTitle: string;
  options: JSON;
  state: number; // 0 单选 1多选 2判断 3填空
  answer: string | string[]; //答案
  analysis: string; //解析
  questionBankId: string;
}
const question = ref<question>({
  questionId: "",
  questionTitle: "",
  options: [{ title: "A", content: "" }],
  state: 0,
  answer: "",
  analysis: "",
  questionBankId: questionBank.questionBankId,
});
//处理标题（填空题）
const disposeTitle = () => {
  if (question.value.state === 3) {
    let arr = question.value.questionTitle.split("__");
    question.value.answer = [];
    for (let i = 0; i < arr.length - 1; i++) {
      question.value.answer.push({
        title: "答" + (i + 1),
        content: "",
      });
    }
  }
};
const questionForm = ref<FormInstance>();
//规则校验
const validatePass = (rule: any, value: any, callback: any) => {
  value.forEach((element) => {
    if (element.content == "") {
      callback(new Error("请输入选项内容"));
    }
  });
  callback();
};
const rules = reactive<FormRules<question>>({
  state: [
    { required: true, message: "请选择题目类型", trigger: ["blur", "change"] },
  ],
  questionTitle: [
    { required: true, message: "请输入题目名称", trigger: "blur" },
  ],
  answer: [
    { required: true, message: "请输入参考答案", trigger: ["blur", "change"] },
  ],
  options: [{ validator: validatePass, trigger: ["blur", "change"] }],
});

const isEdit = ref(false);
//新增
const addQuestion = () => {
  isEdit.value = true;
  isChange.value = false;
  question.value = {
    questionId: "",
    questionTitle: "",
    options: [{ title: "A", content: "" }],
    state: 0,
    answer: "",
    analysis: "",
    questionBankId: questionBank.questionBankId,
  };
};

const addOption = () => {
  if (question.value.options.length > 6) {
    return;
  }
  question.value.options.push({
    title: "",
    content: "",
  });
  disposeOption();
};

const delOption = (index: number) => {
  question.value.options.splice(index, 1);
  disposeOption();
};
const disposeOption = () => {
  for (let i = 0; i < question.value.options.length; i++) {
    const element = question.value.options[i];
    switch (i) {
      case 0:
        element.title = "A";
        break;
      case 1:
        element.title = "B";
        break;
      case 2:
        element.title = "C";
        break;
      case 3:
        element.title = "D";
        break;
      case 4:
        element.title = "E";
        break;
      case 5:
        element.title = "F";
        break;
      case 6:
        element.title = "G";
        break;

      default:
        break;
    }
  }
};
const submitForm = async (formEl: FormInstance | undefined) => {
  if (!formEl) return;
  await formEl.validate((valid) => {
    if (valid) {
      if (question.value.state === 1 || question.value.state === 3) {
        question.value.answer = JSON.stringify(question.value.answer);
      }
      if (question.value.state == 2) {
        question.value.options = [
          { title: "0", content: "错" },
          { title: "1", content: "对" },
        ];
        // question.value.answer = question.value.answer == "0" ? "错" : "对";
      }
      question.value.options = JSON.stringify(question.value.options);
      if (isChange.value) {
        Axios({
          url: "/question/update",
          method: "POST",
          data: question.value,
        }).then((res) => {
          if (res.data.code == 200) {
            isEdit.value = false;
            ElMessage.success("保存成功");
            question.value = {
              questionId: "",
              questionTitle: "",
              options: [{ title: "A", content: "" }],
              state: 0,
              answer: "",
              analysis: "",
              questionBankId: questionBank.questionBankId,
            };
            getQuestionList();
          } else {
            ElMessage.error(res.data.msg);
          }
        });
        return;
      }
      Axios({
        url: "/question/create",
        method: "POST",
        data: question.value,
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
  question.value = row;
  isChange.value = true;
};
const cancelEdit = () => {
  isEdit.value = false;
  getQuestionList();
};

const handleDelete = (row: any) => {
  row.options = JSON.stringify(row.options);
  row.answer = JSON.stringify(row.answer);
  row = row instanceof Array ? row : [row];

  Axios({
    url: "/question/delete",
    method: "POST",
    data: row,
  }).then((res) => {
    if (res.data.code == 200) {
      ElMessage.success("删除成功");
      getQuestionList();
    }
  });
};

const getQuestionList = () => {
  Axios({
    url: "/question/cms/findAll",
    params: {
      questionBankId: questionBank.questionBankId,
    },
  }).then((res) => {
    list.value = res.data;
    list.value.forEach((element) => {
      element.options = JSON.parse(element.options);
      if (element.state == 1 || element.state == 3) {
        element.answer = JSON.parse(element.answer);
      }

      element.parseAnswer = [];
      if (element.state === 1) {
        element.answer.forEach((item) => {
          element.options.forEach((option) => {
            if (item == option.content) {
              element.parseAnswer.push(option.title);
            }
          });
        });
      }
      if (element.state === 0) {
        element.options.forEach((option) => {
          if (element.answer == option.content) {
            element.parseAnswer = option.title;
          }
        });
      }
    });
    console.log(list.value);
  });
};
//----------------------<drtm-导入题目>----------------------
function succesUpload(res) {
  console.log("res", res);
  ElMessage.success("导入成功");
  getQuestionList();
}
function successError(err) {
  console.log("err", err);
  ElMessage.error("导入失败，请重试");
}
const url = import.meta.env.VITE_BASE_URL + "/question/upload";
const upload = {
  questionBankId: questionBank.questionBankId,
};

const headers = {
  'authorization': store.user.token
}
//----------------------<xzmb-下载模板>----------------------
function download() {
  window.open(import.meta.env.VITE_BASE_URL + "/question/download");
}
</script>
<style lang="scss" scoped>
.function-nav {
  display: flex;

  .margin {
    width: 10px;
  }
}

.header {
  margin-left: 10px;
  font-size: 16px;
}

.option {
  display: flex;
  align-items: center;
  margin-bottom: 5px;

  .item-title {
    width: 30px;
  }

  .item-input {
    margin-right: 10px;
  }
}

.item {
  width: 100%;
}

.list {
  margin-top: 20px;
  width: 100%;

  .item {
    .title {
      display: flex;
      justify-content: space-between;

      .title-content {
        display: flex;
      }

      .btns {
        width: 50px;
        display: flex;
        justify-content: space-between;
      }
    }
  }

  .options {
    margin-top: 5px;
    display: grid;
    grid-template-columns: repeat(4, 1fr);

    .item {
      display: flex;
    }
  }

  .answer {
    margin-top: 5px;
  }

  .analysis {
    margin-top: 5px;
  }
}
</style>
