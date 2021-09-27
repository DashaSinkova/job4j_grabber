package ru.job4j.html;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.job4j.grabber.Parse;
import ru.job4j.grabber.utils.DateTimeParser;
import ru.job4j.grabber.utils.SqlRuDateTimeParser;
import ru.job4j.models.Post;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class SqlRuParse implements Parse {
    private final DateTimeParser dateTimeParser;

    public SqlRuParse(DateTimeParser dateTimeParser) {
        this.dateTimeParser = dateTimeParser;
    }

    public static void main(String[] args) throws Exception {
        SqlRuParse sqlRuParse = new SqlRuParse(new SqlRuDateTimeParser());
        System.out.println(sqlRuParse.list("https://www.sql.ru/forum/job-offers/").toString());
        System.out.println(sqlRuParse.detail("https://www.sql.ru/forum/1338923/razrabotchik-bigdata-data-engineer"));
    }

    @Override
    public List<Post> list(String link) throws IOException {
        List<Post> result = new ArrayList<>();

        Document document = Jsoup.connect(link).get();
        Elements row = document.select(".postslisttopic");
        for (Element td : row) {
            Post post = new Post();
            Element href = td.child(0);
            post.setTitle(href.text());
            post.setLink(href.attr("href"));
            post.setChangeTime(dateTimeParser.parse(td.parent().child(5).text()));
            result.add(post);
        }
        return result;
    }

    @Override
    public Post detail(String link) throws IOException {
        Post post = new Post();
        post.setLink(link);
        Document doc = Jsoup.connect(link).get();
        Elements descriptionRow = doc.select(".msgBody");
        Elements createdRow = doc.select(".msgFooter");
        LocalDateTime created = dateTimeParser.parse(createdRow.text().split(" \\[", 2)[0]);
        LocalDateTime changeTime = dateTimeParser.parse(createdRow.last().text().split(" \\[", 2)[0]);
        post.setCreated(created);
        post.setChangeTime(changeTime);
        post.setDescription(descriptionRow.get(1).text());
        post.setTitle(doc.select("title").text().split(" /")[0]);
        return post;
    }
}

