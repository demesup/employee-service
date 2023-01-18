package org.demesup;

import jakarta.persistence.criteria.CriteriaBuilder;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateUtil {
    private static final ThreadLocal<Session> threadLocal = new ThreadLocal<>();

    private static final SessionFactory sessionFactory;

    static {
        sessionFactory = new Configuration().configure("hibernate.cfg.xml").buildSessionFactory();
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public static Session session() {
        Session session;
        if (threadLocal.get() == null) {
            session = sessionFactory.openSession();
            threadLocal.set(session);
        } else {
            session = threadLocal.get();
        }
        return session;
    }

    public static CriteriaBuilder criteriaBuilder() {
        return session().getCriteriaBuilder();
    }

    public static void finish() {
        sessionFactory.close();
    }
}
