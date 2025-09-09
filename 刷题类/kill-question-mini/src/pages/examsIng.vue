<!--
 * @Author: FengXue
 * @Date: 2024-05-16 15:52:07
 * @LastEditors: FengXue
 * @LastEditTime: 2024-06-04 14:52:22
 * @filePath: Do not edit
-->
<template>
  <up-navbar :title="peak ? '巅峰时刻' : '刷题ing'" :autoBack="true"></up-navbar>
  <div class="main">
    <div class="content">
      <div class="time" v-if="peak">{{ dateformat(time) }}</div>
      <div class="title">
        <div class="number">
          [{{ list[number].number }}/{{ list.length }},{{
            list[number].state === 0
              ? "单选"
              : list[number].state === 1
                ? "多选"
                : list[number].state === 2
                  ? "判断"
                  : "填空"
          }}]
        </div>
        <div class="text">{{ list[number].questionTitle }}</div>
      </div>
      <div class="option" v-if="list[number].state != 3">
        <div class="item" :class="{
          error:
            !list[number].correct &&
            (item.content == list[number].answer ||
              (list[number].state == 1 &&
                list[number].answer.indexOf(item.content) != -1) ||
              (list[number].state == 2 && item.title == list[number].answer)),
          active: item.select,
        }" v-for="(item, index) in list[number].options" @click="select(item)">
          <span>{{ item.title }}. {{ item.content }}</span>
        </div>
      </div>
      <div class="fill_in_the_blank" v-if="list[number].state == 3">
        <div class="item" v-for="(item, index) in list[number].answer">
          <up-input :placeholder="'答案' + (index + 1)" v-model="list[number].myAnswer[index]"></up-input>
        </div>
      </div>

      <div class="sure" v-if="
        (list[number].state == 1 || list[number].state == 3) &&
        setting.mode != 1
      " @click="sure">
        确认答案
      </div>
      <div class="answer" v-if="list[number].isDo">
        参考答案:
        <div v-if="list[number].state == 3">
          [
          <span v-for="(item, index) in list[number].answer">"{{ item.content }}"{{
            index == list[number].answer.length - 1 ? "" : ","
          }}</span>]
        </div>
        <div v-else>
          {{ list[number].parseAnswer }}
        </div>
      </div>
      <div class="myAnswer" v-if="list[number].isDo">
        我的答案:
        {{
          list[number].state != 2
            ? list[number].myParseAnswer
            : list[number].myParseAnswer == 0
              ? "错"
              : "对"
        }}
      </div>
      <div class="analysis" v-if="list[number].isDo">
        参考解析: {{ list[number].analysis }}
      </div>
      <div class="btn-full" @click="submit" v-if="number == list.length - 1">
        提交
      </div>
    </div>
    <div class="function-nav">
      <div class="item" @click="lastQuestion">
        <div class="icon">
          <img :src="last" class="icon-img" alt="" />
        </div>
        <div class="text">上一题</div>
      </div>
      <div class="item" @click="answerCard">
        <div class="icon">
          <img :src="answer" class="icon-img" alt="" />
        </div>
        <div class="text">答题卡</div>
      </div>
      <div class="item" @click="collectQuestion">
        <div class="icon">
          <img v-if="list[number].isCollect" :src="collectAcitve" class="icon-img" alt="" />
          <img v-else :src="collect" class="icon-img" alt="" />
        </div>
        <div class="text">收藏</div>
      </div>
      <div class="item" @click="nextQuestion">
        <div class="icon">
          <img :src="next" class="icon-img" alt="" />
        </div>
        <div class="text">下一题</div>
      </div>
    </div>
    <up-popup :show="showAnswerCard" mode="bottom" :round="10" @close="showAnswerCard = false">
      <div class="popup">
        <div class="title">
          <div>答题卡</div>
          <div class="close" @click="showAnswerCard = false">关闭</div>
        </div>
        <div class="list">
          <div class="item" :class="{ active: item.isDo }" @click="changeQuestion(index)" v-for="(item, index) in list">
            {{ index + 1 }}
          </div>
        </div>
      </div>
    </up-popup>
  </div>
</template>

<script setup lang="ts">
import { onLoad, onUnload } from "@dcloudio/uni-app";
import last from "@/static/icon/practiceExams/last.png";
import answer from "@/static/icon/practiceExams/answer.png";
import collect from "@/static/icon/practiceExams/collect.png";
import collectAcitve from "@/static/icon/practiceExams/collect-active.png";
import next from "@/static/icon/practiceExams/next.png";
import { onUnmounted } from "vue";
onLoad((e) => {
  let data = JSON.parse(decodeURIComponent(e.questionBank));
  questionBank.value = JSON.parse(
    decodeURIComponent(e.questionBank)
  ).questionBank;
  questionRecord.value = JSON.parse(
    decodeURIComponent(e.questionBank)
  ).questionRecord;

  getSetting();

  if (data.key != undefined) {
    key.value = data.key;
    searchList();
  } else if (data.isPeak != undefined) {
    peak.value = true;
    peakedness.value.userId = user.userId;
    timer.value = setInterval(() => {
      if (time.value == 3600) {
        clearInterval(timer.value);
      }
      time.value++;
    }, 1000);
    getPeakList();
  } else {
    getList();
  }
});
//关闭页面监听
onUnmounted(() => {
  //关闭定时器
  clearInterval(timer.value);
});

const key = ref();
const peak = ref(false);
const peakedness = ref({
  questionBankId: "",
  total: 0,
  correct: 0,
  userId: "",
  time: 0,
});
const submit = () => {
  let flag = false;
  list.value.forEach((element) => {
    if (!element.isDo) {
      flag = true;
    }
  });
  if (flag) {
    uni.showToast({ title: "请先完成所有题目", icon: "none" });
    return;
  }
  peakedness.value.time = time.value;
  request("/questionBank/peak", peakedness.value, 'POST', {})
    .then(res => {
      uni.showToast({ title: "提交成功", icon: "none" });
      setTimeout(() => {
        uni.navigateBack();
      }, 1500);
    })
    .catch(err => {
      console.log(err);
    })

};
const time = ref(0); //倒计时
const timer = ref();

const dateformat = (time) => {
  time = 3600 - time;
  let h = Math.floor(time / 3600);
  let m = Math.floor((time - h * 3600) / 60);
  let s = time - h * 3600 - m * 60;
  let date = `${h < 10 ? "0" + h : h}:${m < 10 ? "0" + m : m}:${s < 10 ? "0" + s : s
    }`;
  return date;
};
const user = uni.getStorageSync("userInfo");
const setting = ref();
const getSetting = () => {
  request("/questionSetting/findByUserId", {}, 'GET', {})
    .then(res => {
      setting.value = res;
    })
    .catch(err => {
      console.log(err);
    })

};
const searchList = () => {
  request("/question/search", {
    questionTitle: key.value,
    questionBankId: questionBank.value.questionBankId,
  }, 'GET', {})
    .then(res => {
      let data = res;
      for (let index = 0; index < data.length; index++) {
        const element = data[index];
        element.number = index + 1;
        element.isDo = false;
        element.isCollect = false;
        element.myAnswer = "";
        element.correct = true;
        element.options = JSON.parse(element.options);
        element.options.forEach((option) => {
          option.select = false;
        });
        if (element.state == 1 || element.state == 3) {
          element.answer = JSON.parse(element.answer);
        }
        if (element.state == 3) {
          element.myAnswer = [];
        }
      }
      if (data.length == 0) {
        uni.showToast({
          title: "暂无题目",
          icon: "error",
          success: () => {
            setTimeout(() => {
              uni.navigateBack();
            }, 1500);
          },
        });
      } else {
        list.value = data;
        dealWithSearch();
      }
    })
    .catch(err => {
      console.log(err);
    })

};
const getList = () => {
  request("/question/findAll", {
    questionBankId: questionBank.value.questionBankId,
  }, 'GET', {})
    .then(res => {
      let data = res;
      for (let index = 0; index < data.length; index++) {
        const element = data[index];
        element.number = index + 1;
        element.isDo = false;
        element.isCollect = false;
        element.myAnswer = "";
        element.myParseAnswer = "";
        element.correct = true;
        element.options = JSON.parse(element.options);
        element.options.forEach((option) => {
          option.select = false;
        });
        if (element.state == 1 || element.state == 3) {
          element.answer = JSON.parse(element.answer);
        }
        if (element.state == 3) {
          element.myAnswer = [];
        }
      }
      if (data.length == 0) {
        uni.showToast({
          title: "暂无题目",
          icon: "error",
          success: () => {
            setTimeout(() => {
              uni.navigateBack();
            }, 1500);
          },
        });
      } else {
        list.value = data;
        dealWith();
      }
    })
    .catch(err => {
      console.log(err);
    })

};

const getPeakList = () => {
  request("/question/findPeak", {
    questionBankId: questionBank.value.questionBankId,
  }, 'GET', {})
    .then(res => {
      let data = res;
      peakedness.value.total = data.length;
      peakedness.value.questionBankId = questionBank.value.questionBankId;
      for (let index = 0; index < data.length; index++) {
        const element = data[index];
        element.number = index + 1;
        element.isDo = false;
        element.isCollect = false;
        element.myAnswer = "";
        element.myParseAnswer = "";
        element.correct = true;
        element.options = JSON.parse(element.options);
        element.options.forEach((option) => {
          option.select = false;
        });
        if (element.state == 1 || element.state == 3) {
          element.answer = JSON.parse(element.answer);
        }
        if (element.state == 3) {
          element.myAnswer = [];
        }
      }
      list.value = data;
      setting.value.optionChaos = 1;
      setting.value.autoSwitch = 1;
      dealWith();
    })
    .catch(err => {
      console.log(err);
    })

};

const questionRecord = ref();

// 处理已做题目
const dealWith = () => {
  dealWithMode();
  dealWithOption();
  if (!peak.value) {
    dealWithAnswer();
  }
  dealWithCollect();
  list.value.forEach((question) => {
    question.parseAnswer = dealWithParseAnswer(question);
  });
};
//处理答案解析
const dealWithParseAnswer = (question) => {
  let res = "";
  if (question.state == 0) {
    question.options.forEach((option) => {
      if (option.content == question.answer) {
        res = option.title;
      }
    });
  }

  if (question.state == 1) {
    let arr = [];
    question.answer.forEach((item) => {
      question.options.forEach((option) => {
        if (option.content == item) {
          arr.push(option.title);
        }
      });
    });
    res = arr;
  }
  if (question.state == 2) {
    question.options.forEach((option) => {
      if (option.content == question.answer) {
        res = option.content;
      }
    });
  }
  return res;
};
//处理搜索
const dealWithSearch = () => {
  dealWithCollect();
};

// 背题模式
const dealWithMode = () => {
  if (setting.value.mode == 1) {
    list.value.forEach((question) => {
      question.isDo = true;
      question.options.forEach((option) => {
        if (option.title == question.answer) {
          option.select = true;
        }
        if (question.state == 1) {
          question.answer.forEach((item) => {
            if (option.title == item) {
              option.select = true;
            }
          });
        }
      });
      if (question.state == 3) {
        question.answer.forEach((item) => {
          question.myAnswer.push(item.content);
        });
      }
    });
  }
};
const dealWithOption = () => {
  if (setting.value.optionChaos == 1) {
    if (setting.value.mode == 1) {
      return;
    }
    list.value.forEach((question) => {
      if (question.state == 0 || question.state == 1) {
        let contents = question.options.map((item) => item.content);
        for (let i = contents.length - 1; i > 0; i--) {
          const j = Math.floor(Math.random() * (i + 1));
          [contents[i], contents[j]] = [contents[j], contents[i]];
        }
        question.options.forEach((item, index) => {
          item.content = contents[index];
        });
      }
    });
  }
};
// 处理答案+跳转到未做题目
const dealWithAnswer = () => {
  if (setting.value.scope == 0) {
    questionRecord.value.accomplishArray.forEach((element) => {
      // element = JSON.parse(element);
      list.value.forEach((question) => {
        if (question.questionId == element.questionId) {
          question.isDo = true;
          question.myAnswer = element.answer;
          // 处理已选
          question.options.forEach((option) => {
            if (question.state == 0 || question.state == 2) {
              option.title == element.answer
                ? (option.select = true)
                : (option.select = false);
            }
            if (question.state == 1) {
              let arr = element.answer.split(",");
              arr.forEach((item) => {
                item = item.slice(1, -1);
                if (option.title == item) {
                  option.select = true;
                }
              });
            }
          });
          if (question.state == 3) {
            question.myAnswer = JSON.parse(element.answer);
          }
          question.correct = element.correct;
          if (number.value == list.value.length - 1) {
            return;
          }
          number.value++;
        }
      });
    });
  }
};
// 处理收藏
const dealWithCollect = () => {
  questionRecord.value.collectArray.forEach((element) => {
    list.value.forEach((question) => {
      if (question.questionId == element) {
        question.isCollect = true;
      }
    });
  });
};
const questionBank = ref();
const number = ref(0);
const list = ref([{ number: 1, state: 0, questionTitle: "" }]);
// 选择答案
const select = (item: any) => {
  let question = list.value[number.value];
  if (question.isDo) {
    return;
  }
  if (question.state == 1) {
    if (!item.select) {
      if (question.myAnswer == "") {
        question.myAnswer = [];
        question.myParseAnswer = [];
        question.myAnswer.push(item.content);
        question.myParseAnswer.push(item.title);
      } else {
        question.myAnswer.push(item.content);
        question.myParseAnswer.push(item.title);
      }
    } else {
      question.myAnswer.splice(question.myAnswer.indexOf(item.content), 1);
      question.myParseAnswer.splice(question.myAnswer.indexOf(item.title), 1);
    }
    item.select = !item.select;
    return;
  }
  question.myAnswer = item.content;
  question.myParseAnswer = item.title;
  question.isDo = true;
  question.options.forEach((option: any) => {
    option.select = false;
  });
  item.select = true;
  judgement();
};
// 确认答案
const sure = () => {
  let question = list.value[number.value];
  if (question.isDo) {
    return;
  }
  if (question.state == 3) {
    question.myParseAnswer = question.myAnswer;
  }
  judgement();
};
const judgement = () => {
  if (setting.value.mode == 1) {
    return;
  }
  let question = list.value[number.value];
  request("/question/judgement", {
    userId: user.userId,
    questionId: question.questionId,
    answer: JSON.stringify(question.myAnswer),
    peak: peak.value,
    questionBankId: questionBank.value.questionBankId
  }, 'GET', {})
    .then(res => {
      question.correct = res;

      if (setting.value.autoCollect == 1 && !res) {
        question.isCollect = true;
      }
      if (setting.value.autoSwitch == 1 && res) {
        if (number.value != list.value.length - 1) {
          number.value++;
        }
      }
      if (res) {
        peakedness.value.correct++;
      }
    })
    .catch(err => {
      console.log(err);
    })

  question.isDo = true;
};
// 上一题
const lastQuestion = () => {
  if (number.value == 0) {
    uni.showToast({
      title: "已经是第一题了",
      icon: "none",
    });
    return;
  }
  number.value--;
};
//下一题
const nextQuestion = () => {
  if (number.value == list.value.length - 1) {
    uni.showToast({
      title: "已经是最后一题了",
      icon: "none",
    });
    return;
  }
  number.value++;
};
// 答题卡
const showAnswerCard = ref(false);
const answerCard = () => {
  showAnswerCard.value = true;
};
const changeQuestion = (val) => {
  number.value = val;
  showAnswerCard.value = false;
};
// 收藏题目
const collectQuestion = () => {
  let question = list.value[number.value];
  request("/questionRecord/collect", {
    userId: user.userId,
    questionId: question.questionId,
    questionBankId: questionBank.value.questionBankId,
    isAuto: false,
  }, 'GET', {})
    .then(res => {
      if (res == "收藏成功") {
        uni.showToast({
          title: "收藏成功",
          icon: "none",
        });
      } else {
        uni.showToast({
          title: "取消收藏成功",
          icon: "none",
        });
      }
      question.isCollect = !question.isCollect;
    })
    .catch(err => {
      console.log(err);
    })
};
</script>
<style lang="scss" scoped>
.main {
  position: relative;

  .content {
    width: 100%;
    height: calc(85vh - 60px);
    overflow-y: auto;
  }

  .title {
    display: flex;

    .text {
      margin-left: 10px;
    }

    .number {
      width: 100px;
    }

    margin-bottom: 10px;
  }

  .option {
    .item {
      width: 99%;
      min-height: 40px;
      box-sizing: border-box;
      padding: 0 5px 0 5px;
      box-shadow: $uni-border-shadow;
      margin-bottom: 5px;
      display: flex;
      align-items: center;
    }

    .active {
      background: #025e7d;
      color: white;
    }

    .error {
      background: $uni-color-error;
      color: white;
    }
  }

  $active: #025e7d;

  .sure {
    width: 96%;
    height: 40px;
    color: white;
    background: $active;
    display: flex;
    align-items: center;
    justify-content: center;
    border-radius: 10px;
    margin: auto;
    margin-top: 20px;
  }

  .answer {
    margin-top: 10px;
    display: flex;
  }

  .myAnswer {
    margin-top: 10px;
  }

  .analysis {
    margin-top: 10px;
  }

  .fill_in_the_blank {
    .item {
      margin-bottom: 5px;
    }
  }
}

.function-nav {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  width: 100%;
  height: 60px;
  position: absolute;
  bottom: 10px;
  left: 0;
  border-top: 1px solid $uni-border-color;

  .item {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;

    .icon,
    .icon-img {
      width: 32px;
      height: 32px;
    }
  }
}

.popup {
  width: 100%;
  height: 70vh;
  overflow-y: auto;
  padding: 15px;
  box-sizing: border-box;

  .title {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .list {
    display: grid;
    grid-template-columns: repeat(5, 1fr);
    grid-gap: 10px;
    margin-top: 10px;

    .item {
      background: #ecf5ff;
      width: 100%;
      height: 40px;
      border-radius: 10px;
      display: flex;
      align-items: center;
      justify-content: center;
    }

    .active {
      background: #f8e3c5;
    }
  }
}

.btn-full {
  position: absolute;
  bottom: 80px;
}

.time {
  display: flex;
  width: 100%;
  justify-content: flex-end;
}
</style>
