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
            put(StateEnum.IDENTIFIER, TokenEnum.IDENTIFIER);
            put(StateEnum.NUMBER, TokenEnum.LITERAL);
            put(StateEnum.STRING, TokenEnum.LITERAL);
            put(StateEnum.SEPARATOR, TokenEnum.SEPARATOR);
            put(StateEnum.OPERATOR, TokenEnum.OPERATOR);
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
            String[] stringArray = splitWhite(sen.getValue());
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
                        state = StateEnum.IDENTIFIER;
                    } else if (Character.isDigit(ch)) {
                        state = StateEnum.NUMBER;
                    } else if (ch == '"') {
                        state = StateEnum.STRING;
                    } else if (isSeparator(ch)) {
                        state = StateEnum.SEPARATOR;
                    } else if (isOperator(ch)) {
                        state = StateEnum.OPERATOR;
                    } else {
                        state = StateEnum.INVALID;
                    }
                    break;
                // 标识符
                case IDENTIFIER:
                    if (Character.isLetterOrDigit(ch)) {
                        break;
                    }
                    result.add(generateElement(sentence.getRow(), sb, keywordMap.getOrDefault(sb.toString(), TokenEnum.IDENTIFIER)));
                    if (isOperator(ch)) {
                        state = StateEnum.OPERATOR;
                    } else if (isSeparator(ch)) {
                        state = StateEnum.SEPARATOR;
                    } else {
                        state = StateEnum.INVALID;
                    }
                    break;
                // 数字
                case NUMBER:
                    if (Character.isDigit(ch) || ch == '.') {
                        break;
                    }
                    result.add(generateElement(sentence.getRow(), sb, TokenEnum.LITERAL));
                    if (isSeparator(ch)) {
                        state = StateEnum.SEPARATOR;
                    } else if (Character.isLetter(ch)) {
                        state = StateEnum.IDENTIFIER;
                    } else if (isOperator(ch)) {
                        state = StateEnum.OPERATOR;
                    } else {
                        state = StateEnum.INVALID;
                    }
                    break;
                // 字符串
                case STRING:
                    break;
                // 分隔符
                case SEPARATOR:
                    result.add(generateElement(sentence.getRow(), sb, TokenEnum.SEPARATOR));
                    if (isSeparator(ch)) {
                        break;
                    }
                    if (Character.isLetter(ch)) {
                        state = StateEnum.IDENTIFIER;
                        break;
                    } else if (Character.isDigit(ch)) {
                        state = StateEnum.NUMBER;
                        break;
                    } else {
                        state = StateEnum.INVALID;
                    }
                    break;
                // 操作符
                case OPERATOR:
                    result.add(generateElement(sentence.getRow(), sb, TokenEnum.OPERATOR));
                    if (isOperator(ch)) {
                        break;
                    }
                    if (Character.isDigit(ch)) {
                        state = StateEnum.NUMBER;
                    } else {
                        state = StateEnum.INVALID;
                    }
                    break;
                case INVALID:
                    throw new RuntimeException(String.format("无法识别%d行字符%s", sentence.getRow(), ch));
                    // 无效
                default:
                    state = StateEnum.INVALID;
                    break;
            }
            sb.append(ch);
        }
        if (state != StateEnum.INVALID && state != StateEnum.INIT) {
            TokenEnum tokenEnum = stateMap.get(state);
            if (tokenEnum == null) {
                throw new RuntimeException(String.format("无法识别%d行字符%s", sentence.getRow(), sb.toString()));
            }
            result.add(generateElement(sentence.getRow(), sb, tokenEnum));
        }
        return result;
    }

    private String[] splitWhite(String word) {
        StringBuilder sb = new StringBuilder();
        List<String> result = new ArrayList<>();
        for (int i = 0; i < word.length(); i++) {
            char c = word.charAt(i);
            if (c == ' ') {
                if (sb.length() > 0) {
                    result.add(sb.toString());
                    sb.setLength(0);
                }
            } else if (c == '"') {
                sb.append(c);
                i++;
                while (i < word.length() && word.charAt(i) != '"') {
                    sb.append(word.charAt(i));
                    i++;
                }
                if (i < word.length()) {
                    sb.append(word.charAt(i));
                    result.add(sb.toString());
                    sb.setLength(0);
                } else {
                    throw new RuntimeException("找不到字符串末尾");
                }
            } else {
                sb.append(c);
            }
        }
        if (sb.length() > 0) {
            result.add(sb.toString());
        }
        String[] strings = new String[result.size()];
        return result.toArray(strings);
    }

    private boolean isSeparator(char ch) {
        return ch == ';' || ch == ',' || ch == '.' || ch == '(' || ch == ')' || ch == '{' || ch == '}' || ch == '[' || ch == ']';
    }

    private boolean isOperator(char ch) {
        return ch == '=' || ch == '+' || ch == '-' || ch == '*' || ch == '/' || ch == '>' || ch == '<';
    }

    private Element generateElement(int line, StringBuilder sb, TokenEnum tokenEnum) {
        Element element = new Element(line, tokenEnum, sb.toString());
        sb.setLength(0);
        return element;
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
