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
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class SqlRuParse implements Parse {
    private final DateTimeParser dateTimeParser;
    private static List<Post> postList;

    public SqlRuParse(DateTimeParser dateTimeParser) {
        this.dateTimeParser = dateTimeParser;
    }

    public static void main(String[] args) throws Exception {
        SqlRuParse sqlRuParse = new SqlRuParse(new SqlRuDateTimeParser());
        postList = sqlRuParse.list("https://www.sql.ru/forum/job-offers/");

        System.out.println(postList.toString());
        System.out.println(sqlRuParse.detail("https://www.sql.ru/forum/1338061/mes-engineer-with-english"));
    }

    @Override
    public List<Post> list(String link) throws IOException {
        List<Post> result = new ArrayList<>();
        Post post = new Post();
        Document document = Jsoup.connect(link).get();
        Elements row = document.select(".postslisttopic");
        int id = 0;
        for (Element td : row) {
            Element href = td.child(0);
            post.setId(id);
            post.setTitle(href.text());
            post.setLink(href.attr("href"));
            post.setChangeTime(dateTimeParser.parse(td.parent().child(5).text()));
            id++;
            result.add(post);
        }
        return result;
    }

    @Override
    public Post detail(String link) throws IOException {
        Post post = new Post();
        post.setLink(link);
        int index = postList.indexOf(post);
        post = postList.get(index);
        Document doc = Jsoup.connect(link).get();
        Elements descriptionRow = doc.select(".msgBody");
        Elements createdRow = doc.select(".msgFooter");
        LocalDateTime created = new SqlRuDateTimeParser().parse(createdRow.text().split(" \\[", 2)[0]);
        post.setCreated(created);
        post.setDescription(descriptionRow.get(1).text());
        postList.set(index, post);
        return post;
    }
}

