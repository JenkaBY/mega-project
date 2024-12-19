package com.github.jenkaby.mega.config.db;

import jakarta.annotation.PostConstruct;
import liquibase.Scope;
import liquibase.UpdateSummaryEnum;
import liquibase.UpdateSummaryOutputEnum;
import liquibase.changelog.ChangeLogParameters;
import liquibase.command.CommandScope;
import liquibase.command.core.UpdateCommandStep;
import liquibase.command.core.helpers.DatabaseChangelogCommandStep;
import liquibase.command.core.helpers.DbUrlConnectionArgumentsCommandStep;
import liquibase.command.core.helpers.ShowSummaryArgument;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.Map;

@RequiredArgsConstructor
@Configuration
@EnableConfigurationProperties(LiquibaseProperties.class)
public class DBMigratorConfig {

    private final DataSource dataSource;
    private final LiquibaseProperties properties;

//    public void runMigrations(Datasource datasource) {
//        java.sql.Connection connection = datasource; //your openConnection logic here
//
//        Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection));
//
//        Liquibase liquibase = new liquibase.Liquibase("path/to/changelog.xml", new ClassLoaderResourceAccessor(), database);
//
//        liquibase.update(new Contexts(), new LabelExpression());
//    }

    @Bean
    String dummy() {
        System.out.println("properties: " + properties.getChangeLog());
        return "dummy";
    }

    @Bean
    public DbMigrator dbMigrator() {
        return new DbMigrator(dataSource, properties);
    }

    @RequiredArgsConstructor
    public static class DbMigrator {

        private final DataSource dataSource;
        private final LiquibaseProperties properties;

        public void applyLiquibaseChangelist() throws LiquibaseException {
            System.out.println("Apply LB changesets");
            try (var database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(dataSource.getConnection()))) {
                Map<String, Object> scopeObjects = Map.of(
                        Scope.Attr.database.name(), database,
                        Scope.Attr.resourceAccessor.name(), new ClassLoaderResourceAccessor(getClass().getClassLoader()));

                Scope.child(scopeObjects, (Scope.ScopedRunner<?>) () -> new CommandScope("update")
                        .addArgumentValue(DbUrlConnectionArgumentsCommandStep.DATABASE_ARG, database)
                        .addArgumentValue(UpdateCommandStep.CHANGELOG_FILE_ARG, properties.getChangeLog())
                        .addArgumentValue(DatabaseChangelogCommandStep.CHANGELOG_PARAMETERS, new ChangeLogParameters(database))
                        .addArgumentValue(ShowSummaryArgument.SHOW_SUMMARY, UpdateSummaryEnum.SUMMARY)
                        .addArgumentValue(ShowSummaryArgument.SHOW_SUMMARY_OUTPUT, UpdateSummaryOutputEnum.CONSOLE)
                        .execute());
            } catch (LiquibaseException e) {
                throw e;
            } catch (Exception e) {
                // AutoClosable.close() may throw Exception
                throw new LiquibaseException(e);
            }
        }
    }
}
