package ru.job4j.grabber;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import ru.job4j.grabber.utils.SqlRuDateTimeParser;
import ru.job4j.html.SqlRuParse;
import ru.job4j.models.Post;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

public class Grabber implements Grab {

    private final Properties cfg = new Properties();

    public Store store() throws SQLException {
        return new PsqlStore(cfg);
    }

    public Scheduler scheduler() throws SchedulerException {
        Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
        scheduler.start();
        return scheduler;
    }

    public void cfg() {
        try (InputStream input = new FileInputStream("./src/main/resources/app.properties")) {
            cfg.load(input);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void init(Parse parse, Store store, Scheduler scheduler) throws SchedulerException {
        JobDataMap data = new JobDataMap();
        data.put("parse", parse);
        data.put("store", store);
        JobDetail job = JobBuilder.newJob(GrabberJob.class).usingJobData(data).build();
        SimpleScheduleBuilder times = SimpleScheduleBuilder.simpleSchedule().withIntervalInSeconds(Integer.parseInt(cfg.getProperty("time"))).repeatForever();
        Trigger trigger = TriggerBuilder.newTrigger().withSchedule(times).startNow().build();
        scheduler.scheduleJob(job, trigger);
    }

    public static class GrabberJob implements Job {

        @Override
        public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
            JobDataMap map = jobExecutionContext.getJobDetail().getJobDataMap();
            Store store = (Store) map.get("store");
            Parse parse = (Parse) map.get("parse");
            try {
                List<Post> posts = parse.list("https://www.sql.ru/forum/job-offers/");
                List<Post> storePosts = store.getAll();
                for (Post post : posts) {
                    if (post.getTitle().toLowerCase().contains("java") & !storePosts.contains(post)) {
                        store.save(parse.detail(post.getLink()));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    public static void main(String[] args) throws Exception {
        Grabber grab = new Grabber();
        grab.cfg();
        Scheduler scheduler = grab.scheduler();
        Store store = grab.store();
        grab.init(new SqlRuParse(new SqlRuDateTimeParser()), store, scheduler);
    }
}
