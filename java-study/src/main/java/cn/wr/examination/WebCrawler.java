package cn.wr.examination;

/**
 * @author : WangRui
 * @date : 2023/3/31
 */

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class WebCrawler {
    public static void main(String[] args) {
        String url = "https://www.baidu.com/s?wd=java";
        try {
            Document doc = Jsoup.connect(url).get();
            Elements results = doc.select("div.result");
            for (Element result : results) {
                Element title = result.selectFirst("h3");
                Element link = result.selectFirst("a");
                System.out.println("Title: " + title.text());
                System.out.println("Link: " + link.attr("href"));
                System.out.println();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

