package com.github.jenkaby.mega.steps;

import com.github.jenkaby.mega.ComponentTests;
import com.github.jenkaby.mega.config.db.DBMigratorConfig;
import io.cucumber.java.AfterAll;
import io.cucumber.java.BeforeAll;
import liquibase.exception.LiquibaseException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

//@Component
@RequiredArgsConstructor
public class DBSteps implements ApplicationListener<ContextRefreshedEvent> {

    public static DBMigratorConfig.DbMigrator dbMigrator;

    @Autowired
    public void setDbMigrator(DBMigratorConfig.DbMigrator dbMigrator) {
        this.dbMigrator = dbMigrator;
    }


    @BeforeAll
    public static void initDbStep() throws LiquibaseException {
        System.out.println("init DB");

    }

//    @AfterAll
   public static void stopDb(){
        System.out.println("stop DB");
        if (ComponentTests.Bootstrap.postgres != null) {
            ComponentTests.Bootstrap.postgres.stop();
        }
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        System.out.println("onApplicationEvent "  + event);
        try {
            dbMigrator.applyLiquibaseChangelist();
        } catch (LiquibaseException e) {
            throw new RuntimeException(e);
        }
    }
}
