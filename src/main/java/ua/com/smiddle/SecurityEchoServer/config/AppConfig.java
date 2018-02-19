package ua.com.smiddle.SecurityEchoServer.config;

import com.sun.org.glassfish.gmbal.Description;
import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.core.env.Environment;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import ua.com.smiddle.SecurityEchoServer.core.model.Log;
import ua.com.smiddle.logger.produser.LogProducer;
import ua.com.smiddle.logger.produser.LogProducerImpl;
import ua.com.smiddle.logger.queue.LogQueue;
import ua.com.smiddle.logger.queue.LogQueueImpl;
import ua.com.smiddle.logger.settings.DebugLevelSource;
import ua.com.smiddle.logger.storage.LogStorage;
import ua.com.smiddle.security.config.DefaultSecurityConfig;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

/**
 * @author ksa on 2/19/18.
 * @project SecurityEchoServer
 */
@Configuration
@Import({DefaultSecurityConfig.class})
@EnableWebMvc
@ComponentScan("ua.com.smiddle")
@PropertySource("classpath:application.properties")
public class AppConfig {
    public static final String PROJECT = "SecurityEchoServer";
    @Autowired
    private Environment environment;

    @Bean
    @Description("Bean which wrap all exceptions from DAOs")
    public PersistenceExceptionTranslationPostProcessor persistenceExceptionTranslationPostProcessor() {
        return new PersistenceExceptionTranslationPostProcessor();
    }

    @Bean
    @Description("Set transactions management strategy")
    public PlatformTransactionManager transactionManager(EntityManagerFactory emf) {
        return new JpaTransactionManager(emf);
    }

    @Bean
    @Description("Need for EntityManager creation")
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource, JpaVendorAdapter adapter) {
        LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
        emf.setDataSource(dataSource);
        emf.setJpaVendorAdapter(adapter);
        emf.setPackagesToScan("ua.com.smiddle");
        return emf;
    }

    @Bean
    @Description("Setting JPA vendor adapter")
    public JpaVendorAdapter jpaVendorAdapter() {
        HibernateJpaVendorAdapter adapter = new HibernateJpaVendorAdapter();
        adapter.setDatabasePlatform(environment.getProperty("database.dialect"));
        adapter.setShowSql(Boolean.valueOf(environment.getProperty("database.showSQL")));
        adapter.setGenerateDdl(Boolean.valueOf(environment.getProperty("database.generateDDL")));
        return adapter;
    }

    @Bean(destroyMethod = "close")
    @Description("DataBaseService connection pool")
    public BasicDataSource dataSource() {
        BasicDataSource ds = new BasicDataSource();
        ds.setUrl(environment.getProperty("database.url"));
        ds.setUsername(environment.getProperty("database.username"));
        ds.setPassword(environment.getProperty("database.password"));
        ds.setDriverClassName(environment.getProperty("database.driver"));
        if (environment.getProperty("database.useSSL") != null)
            ds.addConnectionProperty("useSSL", environment.getProperty("database.useSSL"));
        if (environment.getProperty("database.useUnicode") != null)
            ds.addConnectionProperty("useUnicode", environment.getProperty("database.useUnicode"));
        if (environment.getProperty("database.autoReconnect") != null)
            ds.addConnectionProperty("autoReconnect", environment.getProperty("database.autoReconnect"));
        if (environment.getProperty("database.characterEncoding") != null)
            ds.addConnectionProperty("characterEncoding", environment.getProperty("database.characterEncoding"));
        if (environment.getProperty("database.connection.initsize") != null)
            ds.setInitialSize(Integer.valueOf(environment.getProperty("database.connection.initsize")));
        if (environment.getProperty("database.connection.minidle") != null)
            ds.setMinIdle(Integer.valueOf(environment.getProperty("database.connection.minidle")));
        if (environment.getProperty("database.connection.maxidle") != null)
            ds.setMaxIdle(Integer.valueOf(environment.getProperty("database.connection.maxidle")));
        if (environment.getProperty("database.connection.maxtotal") != null)
            ds.setMaxTotal(Integer.valueOf(environment.getProperty("database.connection.maxtotal")));
        ds.setLifo(true);
        ds.setRemoveAbandonedOnBorrow(true);
        ds.setRemoveAbandonedOnMaintenance(true);
        ds.setRemoveAbandonedTimeout(60 * 60);
        ds.setTestOnBorrow(true);
        ds.setValidationQuery("SELECT 1");
        return ds;
    }

    @Bean(name = "multipartResolver")
    @Description("Обертка для Apache multipart request")
    public CommonsMultipartResolver multipartResolver() {
        CommonsMultipartResolver resolver = new CommonsMultipartResolver();
        resolver.setMaxUploadSizePerFile(10000000);
        resolver.setDefaultEncoding("utf8");
        return resolver;
    }

    @Bean(name = "LogQueue")
    @org.springframework.context.annotation.Description("Queue for \"Producer -> Queue -> Consumer\" logging system.")
    public LogQueue getQueue(LogStorage db) {
        return new LogQueueImpl(db, Log.class, 20000);
    }

    @Bean(name = "Logger")
    @Description("Producer for \"Producer -> Queue -> Consumer\" logging system.")
    public LogProducer getLogger(DebugLevelSource settings, LogQueue queue) {
        return new LogProducerImpl(settings, queue, Log.class, "SecurityEchoServer");
    }
}
