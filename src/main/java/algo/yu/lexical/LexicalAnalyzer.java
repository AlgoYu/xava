package algo.yu.lexical;

import algo.yu.enums.OperatorEnum;
import algo.yu.enums.SeparatorEnum;
import algo.yu.enums.StateEnum;
import algo.yu.model.Element;
import algo.yu.enums.KeyWordEnum;
import algo.yu.model.Sentence;
import algo.yu.enums.TokenEnum;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LexicalAnalyzer {
    // 关键字
    private static final Map<String, TokenEnum> keywordMap = new HashMap<>() {
        {
            putAll(KeyWordEnum.getKeyWordTokenMap());
            putAll(OperatorEnum.getSeparatorTokenMap());
            putAll(SeparatorEnum.getSeparatorTokenMap());
        }
    };
    // 状态机
    private static final Map<StateEnum, TokenEnum> stateMap = new HashMap<>() {
        {
            put(StateEnum.S1, TokenEnum.IDENTIFIER);
            put(StateEnum.S2, TokenEnum.LITERAL);
            put(StateEnum.S3, TokenEnum.IDENTIFIER);
        }
    };

    public List<Element> analysis(String filePath) {
        List<Sentence> sentence = getSentence(filePath);
        if (sentence == null || sentence.isEmpty()) {
            return null;
        }
        return getElement(sentence);
    }

    private List<Element> getElement(List<Sentence> sentence) {
        List<Element> result = new ArrayList<>();
        for (Sentence sen : sentence) {
            String value = sen.getValue();
            if (value == null || value.length() == 0) {
                continue;
            }
            String[] stringArray = value.split(" ");
            for (String string : stringArray) {
                if (string == null || string.length() == 0) {
                    continue;
                }
                TokenEnum tokenEnum = keywordMap.get(string.toLowerCase());
                if (tokenEnum != null) {
                    result.add(new Element(sen.getRow(), tokenEnum, string));
                    continue;
                }
                result.addAll(wordAnalyzer(sen, string));
            }
        }
        return result;
    }

    private List<Element> wordAnalyzer(Sentence sentence, String string) {
        List<Element> result = new ArrayList<>();
        StateEnum state = StateEnum.INIT;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < string.length(); i++) {
            char ch = string.charAt(i);
            switch (state) {
                case INIT:
                    if (Character.isLetter(ch)) {
                        state = StateEnum.S1;
                        break;
                    }
                    if (Character.isDigit(ch)) {
                        state = StateEnum.S2;
                        break;
                    }
                    if (isSeparator(ch)) {
                        state = StateEnum.S3;
                    }
                    state = StateEnum.INVALID;
                    break;
                // 标识符
                case S1:
                    if (!Character.isLetterOrDigit(ch)) {
                        result.add(new Element(sentence.getRow(), TokenEnum.IDENTIFIER, sb.toString()));
                        state = StateEnum.S3;
                        sb.delete(0, sb.length());
                    }
                    break;
                // 字面值
                case S2:
                    if (Character.isLetter(ch)) {
                        state = StateEnum.S1;
                        break;
                    }
                    if (ch != '.' && !Character.isLetterOrDigit(ch)) {
                        result.add(new Element(sentence.getRow(), TokenEnum.KEYWORD, sb.toString()));
                        state = StateEnum.INIT;
                        sb.delete(0, sb.length());
                    }
                    break;
                // 分隔符
                case S3:
                    result.add(new Element(sentence.getRow(), TokenEnum.KEYWORD, sb.toString()));
                    sb.delete(0, sb.length());
                    if (Character.isLetter(ch)) {
                        state = StateEnum.S1;
                        break;
                    }
                    if (Character.isDigit(ch)) {
                        state = StateEnum.S2;
                        break;
                    }
                    break;
                case INVALID:
                    break;
                // 无效
                default:
                    state = StateEnum.INVALID;
                    break;
            }
            sb.append(ch);
        }
        if (state != StateEnum.INVALID && state != StateEnum.INIT) {
            result.add(new Element(sentence.getRow(), stateMap.getOrDefault(state, TokenEnum.UNKNOWN), sb.toString()));
        }
        return result;
    }

    private boolean isSeparator(char ch) {
        return ch == '.' || ch == '(' || ch == ')' || ch == '{' || ch == '}' || ch == '[' || ch == ']';
    }


    private List<Sentence> getSentence(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            return null;
        }
        try (FileInputStream fileInputStream = new FileInputStream(filePath)) {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));
            String str = null;
            int row = 0;
            List<Sentence> sentences = new ArrayList<>();
            while ((str = bufferedReader.readLine()) != null) {
                row++;
                // 行注释
                if (str.startsWith("//")) {
                    continue;
                }
                // 多行注释
                if (str.startsWith("/*")) {
                    String tmp = null;
                    while ((tmp = bufferedReader.readLine()) != null && !tmp.endsWith("*/")) {
                        row++;
                    }
                    continue;
                }
                // 代码
                sentences.add(new Sentence(row, str));
            }
            return sentences;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
