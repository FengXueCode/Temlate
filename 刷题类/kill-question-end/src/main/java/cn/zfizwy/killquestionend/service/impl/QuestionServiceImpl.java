package cn.zfizwy.killquestionend.service.impl;



import cn.zfizwy.killquestionend.common.R;
import cn.zfizwy.killquestionend.entity.*;
import cn.zfizwy.killquestionend.mapper.QuestionMapper;
import cn.zfizwy.killquestionend.service.QuestionRecordService;
import cn.zfizwy.killquestionend.service.QuestionService;
import cn.zfizwy.killquestionend.service.QuestionSettingService;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * @author FengXue
 * @description 针对表【question】的数据库操作Service实现
 * @createDate 2024-05-15 16:01:50
 */
@Service
public class QuestionServiceImpl extends ServiceImpl<QuestionMapper, Question>
        implements QuestionService {

    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    QuestionRecordService questionRecordService;

    @Autowired
    QuestionSettingService questionSettingService;

    @Override
    public R<Object> delete(List<Question> list) {
        List<String> toDeleteIds = new ArrayList<>(); // 用于保存需要删除的 Id
        for (Question question : list) {
            toDeleteIds.add(question.getQuestionId()); // 将需要删除的 QuestionId 加入 toDeleteIds
        }
        // 删除需要删除的 QuestionBank
        int flag = questionMapper.deleteBatchIds(toDeleteIds);

        if (flag > 0) {
            return new R<>(200, "删除成功", null);
        }
        return new R<>(-1, "删除失败", null);
    }

    @Override
    public R<Object> update(Question question) {
        int flag = questionMapper.update(question, new QueryWrapper<Question>().eq("question_id", question.getQuestionId()));

        if (flag > 0) {
            return new R<>(200, "更新成功", null);
        }
        return new R<>(-1, "更新失败", null);
    }

    @Override
    public R<Object> judge(String questionId, String userId, String myAnswer, Boolean peak, String questionBankId) {
//        获取题目
        Question question = questionMapper.selectById(questionId);
        boolean success = false;
//        比对答案
        if (question.getState() == 1) {
            String answer = question.getAnswer().substring(1, question.getAnswer().length() - 1);
            List<String> answerList = Arrays.asList(answer.split(","));
            myAnswer = myAnswer.substring(1, myAnswer.length() - 1);
            List<String> myAnswerList = Arrays.asList(myAnswer.split(","));
            Set<String> set = new HashSet<>(answerList);
            Set<String> set1 = new HashSet<>(myAnswerList);
            success = set.equals(set1);
        }
        if (question.getState() == 0 || question.getState() == 2) {
            myAnswer = myAnswer.substring(1, myAnswer.length() - 1);
            if (myAnswer.equals(question.getAnswer())) {
                success = true;
            }
        }
        if (question.getState() == 3) {
            String answer = question.getAnswer();
            System.out.println(myAnswer);
            System.out.println(answer);
            answer = answer.substring(1, answer.length() - 1);
            List<String> list = Arrays.asList(answer.split(","));
            answer = "[";
            for (int i = 1; i < list.size(); i += 2) {
                String s = list.get(i);
                String s1 = s.split(":")[1];
                s1 = s1.substring(0, s1.length() - 1);
                answer += s1 + (i == list.size() - 1 ? "]" : ",");
            }

            success = myAnswer.equals(answer);
        }
//        修改做题记录
        QueryWrapper<QuestionRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        queryWrapper.eq("question_bank_id", questionBankId);
        QuestionRecord record = questionRecordService.getOne(queryWrapper);

        //        完成的数组
        List<Accomplish> accomplishArray = JSON.parseArray((String) record.getAccomplishArray(), Accomplish.class);
//        错误的数组
        List<String> errorArray = JSON.parseArray((String) record.getErrorArray(), String.class);
//        收藏的数组
        List<String> collectArray = JSON.parseArray((String) record.getCollectArray(), String.class);

//        添加到已做数组
        Accomplish accomplish = new Accomplish();
        accomplish.setQuestionId(questionId);
        accomplish.setAnswer(myAnswer);
        accomplish.setCorrect(success);
//        判断是否做过，做过更新数据
        boolean flag = false;
        for (Accomplish a : accomplishArray) {
            if (a.getQuestionId().equals(questionId)) {
                a.setAnswer(myAnswer);
                a.setCorrect(success);
                flag = true;
            }
        }
        if (!flag && !peak) {
            accomplishArray.add(accomplish);
        }
        record.setAccomplishArray(JSON.toJSONString(accomplishArray));
        if (!success) {
            //        添加错题
            if (errorArray.indexOf(questionId) == -1 && !peak) {
                errorArray.add(questionId);
                record.setErrorArray(JSON.toJSONString(errorArray));
            }
//            做错自动收藏
            QuestionSetting setting = questionSettingService.getOne(new QueryWrapper<QuestionSetting>().eq("user_id", userId));
            if (setting.getAutoCollect() == 1 && !peak) {
                int i = collectArray.indexOf(questionId);
                if (i == -1) {
                    collectArray.add(questionId);
                    record.setCollectArray(JSON.toJSONString(collectArray));
                }
            }
        } else {
            if (errorArray.indexOf(questionId) != -1) {
                errorArray.remove(questionId);
                record.setErrorArray(JSON.toJSONString(errorArray));
            }
        }
//        更新记录
        boolean b = questionRecordService.updateById(record);

        return new R<>(200, "", success);
    }


    @Override
    public List<Question> findAll(String questionBankId, String userId) {
        QueryWrapper<QuestionSetting> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        QuestionSetting setting = questionSettingService.getOne(queryWrapper);
        QueryWrapper<Question> questionQueryWrapper = new QueryWrapper<>();
        questionQueryWrapper.eq("question_bank_id", questionBankId);
        questionQueryWrapper.orderByAsc("state");
        List<Question> list = questionMapper.selectList(questionQueryWrapper);
        if (setting.getTitleChaos() == 1) {
            Collections.shuffle(list);
        }

        QueryWrapper<QuestionRecord> recordQueryWrapper = new QueryWrapper<>();
        recordQueryWrapper.eq("user_id", userId);
        recordQueryWrapper.eq("question_bank_id", questionBankId);
        QuestionRecord record = questionRecordService.getOne(recordQueryWrapper);

        //        完成的数组
        List<Accomplish> accomplishArray = JSON.parseArray((String) record.getAccomplishArray(), Accomplish.class);
//        错误的数组
        List<String> errorArray = JSON.parseArray((String) record.getErrorArray(), String.class);
//        收藏的数组
        List<String> collectArray = JSON.parseArray((String) record.getCollectArray(), String.class);


        switch (setting.getScope()) {
            case 1://收藏
                if (collectArray.isEmpty()) {
                    list = new ArrayList<>();
                    break;
                }
                list = questionMapper.selectBatchIds(collectArray);
                break;
            case 2://错误
                if (errorArray.isEmpty()) {
                    list = new ArrayList<>();
                    break;
                }
                list = questionMapper.selectBatchIds(errorArray);
                break;
            case 3://未作

                List<String> unique = new ArrayList<>();

                List<String> arrayList = new ArrayList<>();
                for (Accomplish a : accomplishArray) {
                    arrayList.add(a.getQuestionId());
                }
                for (Question question : list) {
                    if (!arrayList.contains(question.getQuestionId())) {
                        unique.add(question.getQuestionId());
                    }
                }
                if (unique.isEmpty()) {
                    list = new ArrayList<>();
                    break;
                }
                list = questionMapper.selectBatchIds(unique);
                break;
        }

        list = processingData(list, setting.getType());
        return list;
    }

    @Override
    public List<Question> findPeak(String questionBankId) {
        List<Question> list = questionMapper.selectList(new QueryWrapper<Question>().eq("question_bank_id", questionBankId));
        //        随机
        Collections.shuffle(list);
        //截取50个
        if (list.size() > 50) {
            list = list.subList(0, 50);
        }

        return list;
    }

    @Override
    public R upload(MultipartFile file, String questionBankId) {
        try (InputStream inputStream = file.getInputStream()) {
            // 创建 Workbook 对象
            Workbook book = new XSSFWorkbook(inputStream);
            Sheet sheet = book.getSheetAt(0);

            if (sheet == null) {
                throw new RuntimeException("Sheet 'Sheet1' not found");
            }

            for (Row row : sheet) {
                if (row.getRowNum() < 2) {
                    continue;
                }

                //题目类型
                Cell questType = row.getCell(1);
                if (questType == null || questType.getCellType() == CellType.BLANK) {
                    continue;
                }
                if (row.getCell(2) == null || row.getCell(2).getCellType() == CellType.BLANK) {
                    continue;
                }
                Question question = new Question();
                //题目标题
                question.setQuestionTitle(row.getCell(2).getStringCellValue());

                //题库id
                question.setQuestionBankId(questionBankId);
                //题目解析
                question.setAnalysis(row.getCell(11).getStringCellValue());
                //题目选项
                List<Option> options = new ArrayList<>();
                //题目答案
                Cell answer = row.getCell(10);

                switch (questType.getStringCellValue()) {
                    case "单选":
                        question.setState(0);
                        setOption(row, options);
                        question.setOptions(JSON.toJSONString(options));
                        switch (answer.getStringCellValue()) {
                            case "A":
                                question.setAnswer(getCellValue(row.getCell(3)));
                                break;
                            case "B":
                                question.setAnswer(getCellValue(row.getCell(4)));
                                break;
                            case "C":
                                question.setAnswer(getCellValue(row.getCell(5)));
                                break;
                            case "D":
                                question.setAnswer(getCellValue(row.getCell(6)));
                                break;
                            case "E":
                                question.setAnswer(getCellValue(row.getCell(7)));
                                break;
                            case "F":
                                question.setAnswer(getCellValue(row.getCell(8)));
                                break;
                            case "G":
                                question.setAnswer(getCellValue(row.getCell(9)));
                                break;
                        }
                        break;
                    case "多选":
                        question.setState(1);
                        options = new ArrayList<>();
                        setOption(row, options);
                        question.setOptions(JSON.toJSONString(options));
                        String[] answerList = answer.getStringCellValue().split("、");
                        List<String> list = new ArrayList<>();
                        for (String s : answerList) {
                            switch (s) {
                                case "A":
                                    list.add(getCellValue(row.getCell(3)));
                                    break;
                                case "B":
                                    list.add(getCellValue(row.getCell(4)));
                                    break;
                                case "C":
                                    list.add(getCellValue(row.getCell(5)));
                                    break;
                                case "D":
                                    list.add(getCellValue(row.getCell(6)));
                                    break;
                                case "E":
                                    list.add(getCellValue(row.getCell(7)));
                                    break;
                                case "F":
                                    list.add(getCellValue(row.getCell(8)));
                                    break;
                                case "G":
                                    list.add(getCellValue(row.getCell(9)));
                                    break;
                            }
                        }
                        question.setAnswer(JSON.toJSONString(list));
                        break;
                    case "判断":
                        question.setState(2);
                        options.add(new Option("0", "错"));
                        options.add(new Option("1", "对"));
                        question.setOptions(JSON.toJSONString(options));
                        question.setAnswer(answer.getStringCellValue());
                        break;
                    case "填空":

                        question.setState(3);
                        List<Option> options2 = new ArrayList<>();
                        options2.add(new Option("0", "测试"));
                        question.setOptions(JSON.toJSONString(options2));
                        String[] answerList1 = answer.getStringCellValue().split("、");
                        List<Option> options1 = new ArrayList<>();
                        for (int i = 0; i < answerList1.length; i++) {
                            options1.add(new Option("答" + (i + 1), answerList1[i]));
                        }
                        question.setAnswer(JSON.toJSONString(options1));
                        break;
                }
                questionMapper.insert(question);
            }
        } catch (IOException e) {
            return new R<>(-1, "上传失败", null);
        }

        return new R<>(200, "上传成功", null);
    }
    // 辅助方法，用于获取单元格的值
    private String getCellValue(Cell cell) {
        if (cell == null) {
            return "";
        }
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                return String.valueOf(cell.getNumericCellValue());
            default:
                return "";
        }
    }
    private void setOption(Row row, List<Option> options) {
        for (int i = 3; i < 10; i++) {
            Cell cell = row.getCell(i);
            if (cell != null && cell.getCellType() != CellType.BLANK) {
                System.out.println(cell);
                Option option = new Option();
                String content;
                switch (cell.getCellType()) {
                    case STRING:
                        content = cell.getStringCellValue();
                        break;
                    case NUMERIC:
                        content = String.valueOf(cell.getNumericCellValue());
                        break;
                    case BOOLEAN:
                        content = String.valueOf(cell.getBooleanCellValue());
                        break;
                    default:
                        content = "";
                        break;
                }
                option.setContent(content);
                switch (i) {
                    case 3:
                        option.setTitle("A");
                        break;
                    case 4:
                        option.setTitle("B");
                        break;
                    case 5:
                        option.setTitle("C");
                        break;
                    case 6:
                        option.setTitle("D");
                        break;
                    case 7:
                        option.setTitle("E");
                        break;
                    case 8:
                        option.setTitle("F");
                        break;
                    case 9:
                        option.setTitle("G");
                        break;
                }
                options.add(option);
            }
        }
    }


    List<Question> processingData(List<Question> list, int state) {
        if (state == 0) {
            return list;
        }
        if (list.isEmpty()) {
            return list;
        }
        List<Question> result = new ArrayList<>();
        for (Question question : list) {
            if (question.getState() == state - 1) {
                result.add(question);
            }
        }
        return result;
    }

}



