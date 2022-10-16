package algo.yu.lexical;

import algo.yu.model.Element;
import algo.yu.model.KeyWordEnum;
import algo.yu.model.Sentence;
import algo.yu.model.Token;

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
    private static final Map<String, Token> keywordMap = new HashMap<>() {
        {
            putAll(KeyWordEnum.getKeyWordTokenMap());
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
                Token token = keywordMap.get(string.toLowerCase());
                if (token != null) {
                    result.add(new Element(sen.getRow(), token, string));
                    continue;
                }
                result.addAll(wordAnalyzer(sen, string));
            }
        }
        return result;
    }

    private List<Element> wordAnalyzer(Sentence sentence, String string) {
        List<Element> result = new ArrayList<>();
        result.add(new Element(sentence.getRow(), Token.UNKNOWN, string));
        return result;
        /*char[] chars = string.toCharArray();
        for (int i = 0; i < chars.length; i++) {

        }*/
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
                    }
                    continue;
                }
                // 正文
                if (!str.startsWith("/*") && str.endsWith("*/") && !str.startsWith("//")) {
                    sentences.add(new Sentence(row, str));
                }
            }
            return sentences;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
