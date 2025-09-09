package cn.zfizwy.killquestionend.controller;


import cn.zfizwy.killquestionend.common.R;
import cn.zfizwy.killquestionend.entity.Question;
import cn.zfizwy.killquestionend.service.QuestionService;
import cn.zfizwy.killquestionend.utils.UserContext;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/question")
public class QuestionController {
    @Autowired
    QuestionService questionService;


    @PostMapping("/create")
    public R<Question> createQuestion(@RequestBody Question question) {
        boolean save = questionService.save(question);
        if (save) {
            return new R<>(200, "保存成功", question);
        } else {
            return new R<>(-1, "保存失败", null);
        }
    }

    @PostMapping("/delete")
    public R<Object> delete(@RequestBody List<Question> list) {
        return questionService.delete(list);
    }

    @PostMapping("/update")
    public R<Object> update(@RequestBody Question question) {
        return questionService.update(question);
    }

    @GetMapping("/search")
    public List<Question> search(@RequestParam("questionTitle") String questionTitle, @RequestParam("questionBankId") String questionBankId) {
        return questionService.list(new QueryWrapper<Question>().like("question_title", questionTitle).eq("question_bank_id", questionBankId));
    }

    @GetMapping("/findAll")
    public List<Question> findAll(@RequestParam("questionBankId") String questionBankId) {

        return questionService.findAll(questionBankId, UserContext.get());
    }

    @GetMapping("/findPeak")
    public List<Question> findPeak(@RequestParam("questionBankId") String questionBankId) {
        return questionService.findPeak(questionBankId);
    }

    @GetMapping("/cms/findAll")
    public List<Question> fundAllCMS(@RequestParam("questionBankId") String questionBankId) {
        return questionService.list(new QueryWrapper<Question>().eq("question_bank_id", questionBankId).orderByAsc("state"));
    }

    @GetMapping("/judgement")
    public R<Object> judge(@RequestParam("questionId") String questionId, @RequestParam("answer") String answer, @RequestParam("peak") Boolean peak, @RequestParam("questionBankId") String questionBankId) {

        return questionService.judge(questionId, UserContext.get(), answer, peak, questionBankId);
    }

    @GetMapping("/download")
    public void download(HttpServletResponse response) {
//        File f = new File("/opt/template/小闽助手刷题模板.xlsx");
        File f = new File("D:\\template\\小闽助手刷题模板.xlsx");
        String fileName = f.getName();
        System.out.println(fileName);
        try {
            fileName = new String(fileName.getBytes("UTF-8"), "ISO8859-1");
            BufferedInputStream br = new BufferedInputStream(new FileInputStream(f));
            byte[] buf = new byte[1024];
            int len = 0;
            response.reset();
            response.setContentType("application/x-msdownload");
            response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
            OutputStream out = response.getOutputStream();
            while ((len = br.read(buf)) > 0)
                out.write(buf, 0, len);
            br.close();
            out.close();
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @PostMapping("/upload")
    public R upload(@RequestParam("file") MultipartFile file ,@RequestParam("questionBankId") String  questionBankId) throws IOException {
        return questionService.upload(file,questionBankId);
    }

}
