package org.demesup.repository;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.demesup.HibernateUtil;
import org.demesup.model.Model;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.MutationQuery;

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
public class RepositoryImpl implements Repository {

    static SessionFactory factory = HibernateUtil.getSessionFactory();

    public <T extends Model> void save(T model) {
        checkClass(model.getClass());
        Session session = getSession();
        Transaction transaction = session.beginTransaction();
        try {
            session.persist(model);
            transaction.commit();
            log.info(model + " is saved");
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            log.warn(e.getMessage());
        }
        session.clear();
    }

    public <T extends Model> void update(T model) {
        checkClass(model.getClass());
        Session session = getSession();
        Transaction transaction = session.beginTransaction();
        try {
            session.merge(model);
            transaction.commit();
            log.info(model + " is updated");
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            log.debug(e.getMessage());
        }
        session.clear();
    }

    public <T extends Model> void updateAll(Class<T> cl, Map<String, String> newValues) {
        checkClass(cl);
        Session session = getSession();
        Transaction transaction = session.beginTransaction();
        String setPart = getSetPart(cl, newValues);
        try {
            MutationQuery query = session.createMutationQuery(setPart);
            query.setHint("jakarta.persistence.fetchgraph", modelTypeMap.get(cl).getHints());
            query.executeUpdate();
            transaction.commit();
            log.info("All " + cl.getSimpleName() + "s are updated");
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            log.debug(e.getMessage());
        }
        session.clear();
    }

    private static <T extends Model> void checkClass(Class<T> cl) {
        if (!cl.isAnnotationPresent(Entity.class)) throw new RuntimeException("Not entity class");
    }

    public <T extends Model> void updateAllByFields(Class<T> cl,
                                                    Map<String, List<String>> oldValues,
                                                    Map<String, String> newValues) {
        checkClass(cl);
        Session session = getSession();
        Transaction transaction = session.beginTransaction();
        String setPart = getSetPart(cl, newValues);
        try {
            MutationQuery query = session.createMutationQuery(getStrQuery(oldValues, setPart + " where "));
            query.setHint("jakarta.persistence.fetchgraph", modelTypeMap.get(cl).getHints());
            query.executeUpdate();
            transaction.commit();
            log.info("All " + cl.getSimpleName() + "s with " + oldValues + " are updated");
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            log.debug(e.getMessage());
        }
        session.clear();
    }

    public <T extends Model> Optional<T> getById(Class<T> cl, long id) {
        T model = null;
        checkClass(cl);
        Session session = getSession();
        Transaction transaction = session.beginTransaction();
        try {
            model = session.find(cl, id, modelTypeMap.get(cl).getHints());
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            log.debug(e.getMessage());
        }
        session.clear();
        return Optional.ofNullable(model);
    }

    public <T extends Model> List<T> getAllByFields(Class<T> cl, Map<String, List<String>> values) {
        checkClass(cl);
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
            log.debug(e.getMessage());
        }
        session.clear();
        return results;
    }

    public <T extends Model> List<T> getAll(Class<T> cl) {
        checkClass(cl);
        Session session = getSession();
        Transaction transaction = session.beginTransaction();
        List<T> resultList = new ArrayList<>();
        try {
            resultList = session.createQuery("FROM "+ cl.getSimpleName(), cl)
                    .setHint("jakarta.persistence.fetchgraph", modelTypeMap.get(cl).getHints())
                    .getResultList();
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            log.debug(e.getMessage());
        }
        session.clear();
        return resultList;
    }


    public <T extends Model> void delete(T model) {
        checkClass(model.getClass());
        Session session = getSession();
        Transaction transaction = session.beginTransaction();
        try {
            session.remove(model);

            transaction.commit();
            log.info(model + " is deleted");
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            log.debug(e.getMessage());
        }
        session.clear();
    }

    public <T extends Model> void deleteAllByFields(Class<T> cl, Map<String, List<String>> values) {
        checkClass(cl);
        Session session = getSession();
        Transaction transaction = session.beginTransaction();
        try {
            MutationQuery query = session.createMutationQuery(getStrQuery(values, queryStartMap.get(DELETE).apply(cl)));
            query.setHint("jakarta.persistence.fetchgraph", modelTypeMap.get(cl).getHints());
            query.executeUpdate();
            transaction.commit();
            log.info("All " + cl.getSimpleName() + "s with " + values + " are deleted");
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            log.debug(e.getMessage());
        }
        session.clear();
    }

    public <T extends Model> void deleteAll(Class<T> cl) {
        checkClass(cl);
        Session session = getSession();
        Transaction transaction = session.beginTransaction();
        try {
            MutationQuery query = session.createMutationQuery("delete from " + cl.getSimpleName());
            query.setHint("jakarta.persistence.fetchgraph", modelTypeMap.get(cl).getHints());
            query.executeUpdate();
            transaction.commit();
            log.info("All rows from " + cl.getSimpleName() + " are deleted");
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            log.debug(e.getMessage());
        }
        session.clear();
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

    private static <T extends Model> String getSetPart(Class<T> cl, Map<String, String> newValues) {
        return newValues.entrySet().stream().map(v -> "c." + v.getKey() + " = '" + v.getValue() + "'").collect(Collectors.joining(",", "update " + cl.getSimpleName() + " c set ", ""));
    }

}
