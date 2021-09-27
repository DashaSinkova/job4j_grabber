package ru.job4j.grabber;

import ru.job4j.grabber.utils.SqlRuDateTimeParser;
import ru.job4j.html.SqlRuParse;
import ru.job4j.models.Post;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class PsqlStore implements Store, AutoCloseable {
    private Connection cnn;

    public PsqlStore(Properties conf) throws SQLException {
        try {
            Class.forName(conf.getProperty("jdbc.driver"));
        } catch (Exception e) {
            throw new IllegalStateException();
        }
        cnn = DriverManager.getConnection(conf.getProperty("jdbc.url"), conf.getProperty("jdbc.username"), conf.getProperty("jdbc.password"));
    }
    @Override
    public void close() throws Exception {
        if (cnn != null) {
            cnn.close();
        }
    }

    @Override
    public void save(Post post) {
        try (PreparedStatement statement = cnn.prepareStatement("insert into post(title, link, description, created, changetime) values (?, ?, ?, ?, ?)")) {
            statement.setString(1, post.getTitle());
            statement.setString(2, post.getLink());
            statement.setString(3, post.getDescription());
            statement.setTimestamp(4, Timestamp.valueOf(post.getCreated()));
            statement.setTimestamp(5, Timestamp.valueOf(post.getChangeTime()));
            statement.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Post> getAll() {
        List<Post> res = new ArrayList<>();
        try (PreparedStatement statement = cnn.prepareStatement("select * from post")) {
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    res.add(new Post(
                            resultSet.getInt(1),
                            resultSet.getString(2),
                            resultSet.getString(3),
                            resultSet.getString(4),
                            resultSet.getTimestamp(5).toLocalDateTime(),
                            resultSet.getTimestamp(6).toLocalDateTime()
                    ));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    @Override
    public Post findById(int id) {
        Post res = null;
        try (PreparedStatement statement = cnn.prepareStatement("select * from post where id = ?")) {
            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    res = new Post(
                            resultSet.getInt(1),
                            resultSet.getString(2),
                            resultSet.getString(3),
                            resultSet.getString(4),
                            resultSet.getTimestamp(5).toLocalDateTime(),
                            resultSet.getTimestamp(6).toLocalDateTime()
                    );
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    public static void main(String[] args) throws Exception {
        Properties properties = new Properties();
        properties.load(new FileInputStream("C:\\Projects\\job4j_grabber\\src\\main\\resources\\app.properties"));
        PsqlStore store = new PsqlStore(properties);
        Post post = new Post(0, "test", "test6", "test", new SqlRuDateTimeParser().parse("21 сен 21, 12:20"), new SqlRuDateTimeParser().parse("21 сен 21, 12:20"));
        store.save(post);
        System.out.println(store.getAll().toString());
        System.out.println(store.findById(5));
    }
}
