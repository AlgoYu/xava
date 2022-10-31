package algo.yu;

import algo.yu.lexical.LexicalAnalyzer;
import algo.yu.model.Element;
import com.google.gson.Gson;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        Gson gson = new Gson();

        LexicalAnalyzer lexicalAnalyzer = new LexicalAnalyzer();
        List<Element> analysis = lexicalAnalyzer.analysis("/Users/xiaoyu/JavaProject/xava/source/test1.xava");

        String json = gson.toJson(analysis);
        System.out.println(json);
        Clipboard systemClipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        systemClipboard.setContents(new StringSelection(json), null);
    }
}
