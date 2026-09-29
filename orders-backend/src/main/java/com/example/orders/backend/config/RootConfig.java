package com.example.orders.backend.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.core.io.ClassPathResource;
import org.springframework.lang.NonNull;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement
@ComponentScan({
        "com.example.orders.backend.service",
        "com.example.orders.backend.repository",
        "com.example.orders.backend.soap"
})
public class RootConfig {

        @Bean
        @NonNull
        public DataSource dataSource() {
                DriverManagerDataSource dataSource = new DriverManagerDataSource();
                dataSource.setDriverClassName("org.h2.Driver");
                dataSource.setUrl(System.getProperty("orders.db.url", "jdbc:h2:file:./data/orders;AUTO_SERVER=TRUE"));
                dataSource.setUsername("sa");
                dataSource.setPassword("");
                return dataSource;
        }

        @Bean
        public JdbcTemplate jdbcTemplate(@NonNull DataSource dataSource) {
                return new JdbcTemplate(dataSource);
        }

        @Bean
        public DataSourceInitializer dataSourceInitializer(@NonNull DataSource dataSource) {
                ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
                populator.addScript(new ClassPathResource("schema.sql"));
                DataSourceInitializer initializer = new DataSourceInitializer();
                initializer.setDataSource(dataSource);
                initializer.setDatabasePopulator(populator);
                return initializer;
        }

        @Bean
        public PlatformTransactionManager transactionManager(@NonNull DataSource dataSource) {
                return new DataSourceTransactionManager(dataSource);
        }
}