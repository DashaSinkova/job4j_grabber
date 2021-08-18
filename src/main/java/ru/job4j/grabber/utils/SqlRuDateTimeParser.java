package ru.job4j.grabber.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Locale;
import java.util.Map;
import static java.util.Map.entry;

public class SqlRuDateTimeParser implements DateTimeParser {
    private static final Map<String, String> MONTHS = Map.ofEntries(
            entry("май", "may"),
            entry("янв", "jan"),
            entry("фев", "feb"),
            entry("мар", "mar"),
            entry("апр", "apr"),
            entry("июн", "jun"),
            entry("июл", "jul"),
            entry("авг", "Aug"),
            entry("сен", "sep"),
            entry("окт", "oct"),
            entry("ноя", "nov"),
            entry("дек", "dec"));
    @Override
    public LocalDateTime parse(String parse) {
        String date = parse.split(",")[0];
        String time = parse.split(",")[1];
        if (date.contains("сегодня")) {
            date = LocalDate.now().format(DateTimeFormatter.ofPattern("d MMM yy", Locale.ENGLISH));
        } else if (date.contains("вчера")) {
            date = LocalDate.now().minusDays(1).format(DateTimeFormatter.ofPattern("d MMM yy", Locale.ENGLISH));
        } else {
            String[] dateArr = date.split(" ");
            dateArr[1] = MONTHS.get(dateArr[1]);
            date = dateArr[0] + " " + dateArr[1] + " " + dateArr[2];
        }
        System.out.println(date);
        DateTimeFormatterBuilder formatterBuilder = new DateTimeFormatterBuilder();
        formatterBuilder.parseCaseInsensitive();
        formatterBuilder.appendPattern("d MMM yy HH:mm");
        DateTimeFormatter formatter = formatterBuilder.toFormatter().withLocale(Locale.ENGLISH);
        return LocalDateTime.parse(date + "" + time, formatter);
    }
}
