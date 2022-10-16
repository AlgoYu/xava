package algo.yu;

import algo.yu.lexical.LexicalAnalyzer;
import algo.yu.model.Element;
import com.google.gson.Gson;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        Gson gson = new Gson();

        LexicalAnalyzer lexicalAnalyzer = new LexicalAnalyzer();
        List<Element> analysis = lexicalAnalyzer.analysis("/Users/xiaoyu/JavaProject/xava/source/test1.xava");

        System.out.println(gson.toJson(analysis));
    }
}
