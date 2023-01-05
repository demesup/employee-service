package org.demesup.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.demesup.HibernateUtil;
import org.demesup.ModelType;
import org.demesup.model.Model;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.util.*;
import java.util.stream.Collectors;

@Getter
@Slf4j
public class Repository {

    static SessionFactory factory = HibernateUtil.getSessionFactory();

    public <T extends Model> void save(T model) {
        Session session = factory.openSession();
        session.beginTransaction();

        session.persist(model);

        session.getTransaction().commit();
        session.close();
    }

    public <T extends Model> void update(T model) {
        Session session = factory.openSession();
        session.beginTransaction();

        System.out.println(session.merge(model));

        session.getTransaction().commit();
        session.close();

    }

    public <T extends Model> Optional<T> getById(ModelType type, Class<T> cl, long id) {
        Session session = factory.openSession();
        session.beginTransaction();


        var model = session.find(type.getCl(), id, type.getHints());

        session.getTransaction().commit();
        session.close();

        return (Optional<T>) Optional.ofNullable(model);
    }

    public <T extends Model> List<T> getByFields(ModelType type, Class<T> cl, Map<String, String> fieldValueMap) {
        Session session = factory.openSession();
        session.beginTransaction();

        String strQuery = getStrQuery(fieldValueMap, cl);
        List<T> results = session.createQuery(strQuery, cl)
                .setHint(type.hint1Parameter(), type.getEntityGraph()).getResultList();

        session.getTransaction().commit();
        session.close();
        return results;
    }

    private <T extends Model> String getStrQuery(Map<String, String> fieldValueMap, Class<T> cl) {
        return fieldValueMap.entrySet().stream()
                .map(entry -> entry.getKey() + " = '" + entry.getValue() + "'")
                .collect(Collectors.joining(
                        " and ",
                        "select c from " + cl.getSimpleName() + " c where ",
                        ""));
    }


    public <T extends Model> List<T> getAll(ModelType type) {
        Session session = factory.openSession();
        session.beginTransaction();

        List<T> resultList;

        EntityManager entityManager = factory.createEntityManager();
        TypedQuery<? extends Model> q = entityManager.createQuery("SELECT a FROM " + type.getCl().getSimpleName() + " a ", type.getCl())
                .setHint("javax.persistence.fetchgraph", type.getHints());
        resultList = (List<T>) q.getResultList();

        session.getTransaction().commit();
        session.close();
        return resultList;
    }


    public <T extends Model> void delete(T model) {
        Session session = factory.openSession();
        session.beginTransaction();
        session.remove(model);
        session.flush();

        session.getTransaction().commit();
        session.close();
    }
}
