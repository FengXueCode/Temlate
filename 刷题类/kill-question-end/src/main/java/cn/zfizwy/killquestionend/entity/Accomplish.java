package cn.zfizwy.killquestionend.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString
public class Accomplish {
    private String questionId;
    private String answer;
    private boolean correct ;
}
