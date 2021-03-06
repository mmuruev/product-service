package net.nowhere.service.product.environment;

import com.google.inject.AbstractModule;
import com.google.inject.Provider;
import com.google.inject.Scopes;
import org.h2.jdbcx.JdbcConnectionPool;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class DatabaseModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(JdbcTemplate.class).toProvider(DatabaseProvider.class).in(Scopes.SINGLETON);
    }

    private static class DatabaseProvider implements Provider<JdbcTemplate> {
        private static final String PROPERTIES_FILE = "/database.properties";
        private static final String DATABASE_URL = "database.url";
        private static final String DATABASE_USER = "database.user";
        private static final String DATABASE_PASSWORD = "database.password";

        private final String url;
        private final String user;
        private final String password;

        private DatabaseProvider() {
            final Properties properties = properties(PROPERTIES_FILE);
            url = properties.getProperty(DATABASE_URL);
            user = properties.getProperty(DATABASE_USER);
            password = properties.getProperty(DATABASE_PASSWORD);
        }

        @Override
        public JdbcTemplate get() {
            return new JdbcTemplate(JdbcConnectionPool.create(url, user, password));
        }

        private Properties properties(final String propertiesName) {
            final Properties properties = new Properties();
            try (final InputStream stream = getClass().getResourceAsStream(propertiesName)) {
                properties.load(stream);
                return properties;
            } catch (IOException e) {
                throw new IllegalArgumentException("Cannot load properties", e);
            }
        }
    }
}
