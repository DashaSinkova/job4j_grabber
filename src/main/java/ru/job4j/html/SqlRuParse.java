package ru.job4j.html;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.job4j.grabber.utils.SqlRuDateTimeParser;
import ru.job4j.models.Post;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class SqlRuParse {
    public static void main(String[] args) throws Exception {
        for (int page = 1; page <= 5; page++) {
            Document doc = Jsoup.connect("https://www.sql.ru/forum/job-offers/" + page).get();
            Elements row = doc.select(".postslisttopic");
            int count = 0;
            for (Element td : row) {
                Element href = td.child(0);
                System.out.println(href.attr("href"));
                System.out.println(href.text());
                System.out.println(doc.getElementsByAttribute("style").select(".altCol").get(count).text());
                count++;
                SqlRuDateTimeParser date = new SqlRuDateTimeParser();
                System.out.println("Дата " + date.parse(td.parent().child(5).text()).format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")));
                System.out.println(SqlRuParse.getPost(href.attr("href")));
                System.out.println();
            }
       }
    }

    public static Post getPost(String href) throws IOException {
        Document doc = Jsoup.connect(href).get();
        Elements descriptionRow = doc.select(".msgBody");
        Elements createdRow = doc.select(".msgFooter");
        Post res = new Post();
        LocalDateTime created = new SqlRuDateTimeParser().parse(createdRow.text().split(" \\[", 2)[0]);
        res.setCreated(created);
        res.setDescription(descriptionRow.get(1).text());
        return res;
    }
}

