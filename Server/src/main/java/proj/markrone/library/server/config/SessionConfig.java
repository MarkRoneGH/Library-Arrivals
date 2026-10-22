package proj.markrone.library.server.config;

import proj.markrone.library.server.model.entities.*;

import lombok.Getter;

import org.hibernate.SessionFactory;
import org.hibernate.HibernateException;
import org.hibernate.cfg.Configuration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


@Getter
public class SessionConfig {
    private static final Logger logger = LogManager.getLogger(SessionConfig.class);
    private static volatile SessionConfig sessionConfig;
    private final SessionFactory sessionFactory;

    /**
     * Приватный конструктор для инициализации SessionFactory.\
     *
     * @throws HibernateException если произошла ошибка при создании SessionFactory
     */
    private SessionConfig() throws HibernateException {
        try {
            logger.info("Инициализация SessionFactory...");
            sessionFactory = new Configuration()
                    .addAnnotatedClass(User.class)
                    .addAnnotatedClass(Person.class)
                    .addAnnotatedClass(Role.class)
                    .addAnnotatedClass(Book.class)
                    .addAnnotatedClass(Arrival.class)
                    .addAnnotatedClass(BookCopy.class)
                    .addAnnotatedClass(Issue.class)
                    .buildSessionFactory();
            logger.info("SessionFactory успешно инициализирована");
        } catch (HibernateException e) {
            logger.fatal("Ошибка при создании SessionFactory", e);
            throw new HibernateException("Не удалось создать SessionFactory", e);
        }
    }

    /**
     * Возвращает экземпляр SessionConfig (реализация синглтона с двойной проверкой блокировки).
     *
     * @return единственный экземпляр SessionConfig
     * @throws IllegalStateException если при создании экземпляра произошла ошибка
     */
    public static SessionConfig getInstance() {
        if (sessionConfig == null) {
            synchronized (SessionConfig.class) {
                if (sessionConfig == null) {
                    try {
                        sessionConfig = new SessionConfig();
                    } catch (HibernateException e) {
                        throw new IllegalStateException("Не удалось инициализировать SessionConfig", e);
                    }
                }
            }
        }
        return sessionConfig;
    }

}