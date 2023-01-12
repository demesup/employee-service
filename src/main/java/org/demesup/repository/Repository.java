package org.demesup.repository;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.demesup.HibernateUtil;
import org.demesup.model.Model;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.demesup.AppController.Action.*;
import static org.demesup.HibernateUtil.getSession;
import static org.demesup.ModelType.modelTypeMap;

@Getter
@Slf4j
public class Repository {

    static SessionFactory factory = HibernateUtil.getSessionFactory();

    public <T extends Model> void save(T model) {
        Session session = getSession();
        session.beginTransaction();

        session.persist(model);

        session.getTransaction().commit();
        session.clear();
    }

    public <T extends Model> void update(T model) {
        Session session = getSession();
        Transaction transaction = session.beginTransaction();
        try {
            session.merge(model);
            transaction.commit();
            session.clear();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            System.out.println(e.getMessage());
        }
    }

    public <T extends Model> void updateAll(Class<T> cl, Map<String, String> newValues) {
        Session session = getSession();
        Transaction transaction = session.beginTransaction();
        String setPart = getSetPart(cl, newValues);
        try {
            session.createQuery(setPart, cl)
                    .setHint("jakarta.persistence.fetchgraph", modelTypeMap.get(cl).getHints())
                    .executeUpdate();
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
        }
    }

    public <T extends Model> void updateAllByFields(Class<T> cl,
                                                    Map<String, List<String>> oldValues,
                                                    Map<String, String> newValues) {
        Session session = getSession();
        Transaction transaction = session.beginTransaction();
        String setPart = getSetPart(cl, newValues);
        var query = getStrQuery(oldValues, setPart);
        try {
            session.createQuery(query, cl)
                    .setHint("jakarta.persistence.fetchgraph", modelTypeMap.get(cl).getHints())
                    .executeUpdate();
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
        }
    }

    private static <T extends Model> String getSetPart(Class<T> cl, Map<String, String> newValues) {
        return newValues.entrySet().stream().map(v -> "c." + v.getKey() + " = " + v.getValue()).collect(Collectors.joining(",", "update " + cl.getSimpleName() + " c set ", ""));
    }

    public <T extends Model> Optional<T> getById(Class<T> cl, long id) {
        T model = null;
        Session session = getSession();
        Transaction transaction = session.beginTransaction();
        try {
            model = session.find(cl, id, modelTypeMap.get(cl).getHints());
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
        }

        return Optional.ofNullable(model);
    }

    public <T extends Model> List<T> getAllByFields(Class<T> cl, Map<String, List<String>> values) {
        List<T> results = new ArrayList<>();
        Session session = getSession();
        Transaction transaction = session.beginTransaction();
        var query = getStrQuery(values, queryStartMap.get(SEARCH).apply(cl));
        System.out.println(query);
        try {
            results = session.createQuery(query, cl)
                    .setHint("jakarta.persistence.fetchgraph", modelTypeMap.get(cl).getHints())
                    .getResultList();

            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
        }
        return results;
    }

    private String getStrQuery(Map<String, List<String>> fieldValueMap, String prefix) {
        return fieldValueMap.entrySet().stream()
                .map(entry ->
                        entry.getKey() + " " + entry.getValue().stream().collect(Collectors.joining(
                                "','",
                                "in('",
                                "')")))
                .collect(Collectors.joining(
                        " and ", prefix, ""));
    }

    public <T extends Model> List<T> getAll(Class<T> cl) {
        Session session = getSession();
        Transaction transaction = session.beginTransaction();
        List<T> resultList = new ArrayList<>();
        try {
            resultList = session.createQuery("FROM " + cl.getSimpleName(), cl)
                    .setHint("jakarta.persistence.fetchgraph", modelTypeMap.get(cl).getHints()).getResultList();
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
        }
        return resultList;
    }


    public <T extends Model> void delete(T model) {
        Session session = getSession();
        Transaction transaction = session.beginTransaction();
        try {
            session.remove(model);

            transaction.commit();
            session.clear();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
        }
    }

    public <T extends Model> void deleteAllByFields(Class<T> cl, Map<String, List<String>> values) {
        Session session = getSession();
        Transaction transaction = session.beginTransaction();
        var query = getStrQuery(values, queryStartMap.get(DELETE).apply(cl));
        try {
            session.createQuery(query, cl)
                    .setHint("jakarta.persistence.fetchgraph", modelTypeMap.get(cl).getHints())
                    .executeUpdate();
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
        }
    }

    public <T extends Model> void deleteAll(Class<T> cl) {
        Session session = getSession();
        Transaction transaction = session.beginTransaction();
        try {
            session.createQuery("delete from " + cl.getSimpleName(), cl)
                    .setHint("jakarta.persistence.fetchgraph", modelTypeMap.get(cl).getHints())
                    .executeUpdate();
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
        }
    }
}
