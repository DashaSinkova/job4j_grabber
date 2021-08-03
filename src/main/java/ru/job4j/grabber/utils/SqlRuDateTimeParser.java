package ru.job4j.grabber.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class SqlRuDateTimeParser implements DateTimeParser {
    @Override
    public LocalDateTime parse(String parse) {
        String date = parse.split(",")[0];
        String time = parse.split(",")[1];
        if (date.contains("сегодня")) {
            date = LocalDate.now().format(DateTimeFormatter.ofPattern("d MMM yy"));
        } else if (date.contains("вчера")) {
            date = LocalDate.now().minusDays(1).format(DateTimeFormatter.ofPattern("d MMM yy"));
        } else {
            String[] dateArr = date.split(" ");
            if (dateArr[1].equals("май")) {
                dateArr[1] = "мая";
            } else {
                dateArr[1] = dateArr[1] + ".";
            }
            date = dateArr[0] + " " + dateArr[1] + " " + dateArr[2];
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMM yy HH:mm").withLocale(new Locale("ru"));
        String datetime = date + "" + time;
        LocalDateTime res = LocalDateTime.parse(datetime, formatter);
        return res;
    }
}
