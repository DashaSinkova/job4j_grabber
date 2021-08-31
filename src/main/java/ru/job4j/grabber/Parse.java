package ru.job4j.grabber;

import ru.job4j.models.Post;

import java.io.IOException;
import java.util.List;

public interface Parse {
    List<Post> list(String link) throws IOException; //загружает список всех постов
    Post detail(String link) throws IOException; //загружает все детали одного поста
}
