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
        int preIndex = 0;
        for (int i = 0; i < string.length(); i++) {
            char ch = string.charAt(i);
            switch (state) {
                case INIT:
                    if ((ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z')) {
                        state = StateEnum.S1;
                        break;
                    }
                    if (ch >= '0' && ch <= '9') {
                        state = StateEnum.S2;
                        break;
                    }
                    state = StateEnum.INVALID;
                    break;
                // 标识符
                case S1:
                    if (ch == ';' || ch == '.') {
                        result.add(new Element(sentence.getRow(), TokenEnum.IDENTIFIER, string.substring(preIndex, i)));
                        result.add(new Element(sentence.getRow(), TokenEnum.SEPARATOR, String.valueOf(ch)));
                        state = StateEnum.INIT;
                    }
                    break;
                // 整数
                case S2:
                    if (ch == ';' || ch == '.') {
                        result.add(new Element(sentence.getRow(), TokenEnum.KEYWORD, string.substring(preIndex, i)));
                        result.add(new Element(sentence.getRow(), TokenEnum.SEPARATOR, String.valueOf(ch)));
                        state = StateEnum.INIT;
                    }
                    break;
                // 浮点数
                case S3:
                    break;
                case INVALID:
                    break;
                // 无效
                default:
                    state = StateEnum.INVALID;
                    break;
            }
        }
        if (state != StateEnum.INVALID) {
            result.add(new Element(sentence.getRow(), stateMap.getOrDefault(state, TokenEnum.IDENTIFIER), string.substring(preIndex)));
        }
        return result;
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
