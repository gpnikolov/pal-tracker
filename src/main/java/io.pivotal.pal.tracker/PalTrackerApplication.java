package io.pivotal.pal.tracker;


import com.mysql.cj.jdbc.MysqlDataSource;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import javax.sql.DataSource;

@SpringBootApplication
public class PalTrackerApplication {

    public static void main(String[] args) {
        SpringApplication.run(PalTrackerApplication.class, args);
    }

    @Bean
    public TimeEntryRepository getTimeEntryRepository(){
        //return new InMemoryTimeEntryRepository();
        MysqlDataSource dataSource = new MysqlDataSource();
        dataSource.setUrl(System.getenv("SPRING_DATASOURCE_URL"));
        return new JdbcTimeEntryRepository(dataSource);
    }

}
