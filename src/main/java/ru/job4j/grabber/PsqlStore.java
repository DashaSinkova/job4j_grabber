package ru.job4j.grabber;

import ru.job4j.models.Post;

import java.sql.*;
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
//        try (PreparedStatement statement = cnn.prepareStatement("insert into post(title, link, description, created, changetime) values (?, ?, ?, ?, ?)")) {
//            statement.setString();
//            statement.setString();
//            statement.setString();
//            statement.setTimestamp(4, Timestamp.valueOf(post.getCreated()));
//            statement.setTimestamp(5, Timestamp.valueOf(post.getChangeTime()));
//
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    @Override
    public List<Post> getAll() {
        return null;
    }

    @Override
    public Post findById(int id) {
        return null;
    }

    public static void main(String[] args) {

    }
}
