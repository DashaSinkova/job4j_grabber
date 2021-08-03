package ru.job4j.html;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.job4j.grabber.utils.SqlRuDateTimeParser;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class SqlRuParse {
    public static void main(String[] args) throws Exception {
        Document doc = Jsoup.connect("https://www.sql.ru/forum/job-offers").get();
        List<Element> row = doc.select(".postslisttopic").subList(0, 5); //полечаем нужный тег
        int count = 0;
        for (Element td : row) {
            Element href = td.child(0);
            System.out.println(href.attr("href"));
            System.out.println(href.text());
            System.out.println(doc.getElementsByAttribute("style").select(".altCol").get(count).text());
            count++;
            SqlRuDateTimeParser date = new SqlRuDateTimeParser();
            System.out.println("Дата " + date.parse(td.parent().child(5).text()).format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")));
            System.out.println();
        }
    }
}

