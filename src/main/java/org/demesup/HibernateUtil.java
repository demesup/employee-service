package org.demesup;

import jakarta.persistence.EntityManager;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateUtil {
    public static SessionFactory sessionFactory;
    static EntityManager entityManager;


    static {
        sessionFactory = new Configuration().configure().buildSessionFactory();
        entityManager = sessionFactory.createEntityManager();
    }

    public static Session session(){
        return sessionFactory.openSession();
    }

    public static void finish(){
        sessionFactory.close();
    }
}
