package org.demesup.repository;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.demesup.model.Model;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.demesup.AppController.Action.*;
import static org.demesup.HibernateUtil.session;
import static org.demesup.ModelType.modelTypeMap;

@Getter
@Slf4j
public class Repository {

    public <T extends Model> void save(T model) {
        Session session = session();
        Transaction transaction = session.beginTransaction();
        try {
            session.persist(model);
            log.info(model + " is saved");
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
        }
    }

    public <T extends Model> void update(T model) {
        Session session = session();
        Transaction transaction = session.beginTransaction();
        try {
            session.merge(model);
            transaction.commit();
            log.info(model + " is updated");
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
        }
    }

    public <T extends Model> void updateAll(Class<T> cl, Map<String, String> newValues) {
        Session session = session();
        Transaction transaction = session.beginTransaction();
        String setPart = getSetPart(cl, newValues);
        try {
            session.createQuery(setPart, cl)
                    .setHint("jakarta.persistence.fetchgraph", modelTypeMap.get(cl).getHints())
                    .executeUpdate();
            transaction.commit();
            log.info("All " + cl.getSimpleName() + "s are updated");
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
        }
    }

    public <T extends Model> void updateAllByFields(Class<T> cl,
                                                    Map<String, List<String>> oldValues,
                                                    Map<String, String> newValues) {
        Session session = session();
        Transaction transaction = session.beginTransaction();
        String setPart = getSetPart(cl, newValues);
        var query = getStrQuery(oldValues, setPart);
        try {
            session.createQuery(query, cl)
                    .setHint("jakarta.persistence.fetchgraph", modelTypeMap.get(cl).getHints())
                    .executeUpdate();
            transaction.commit();
            log.info("All departments with " + oldValues + " are updated to " + newValues);
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
        }
    }

    public <T extends Model> Optional<T> getById(Class<T> cl, long id) {
        T model = null;
        Session session = session();
        Transaction transaction = session.beginTransaction();
        try {
            model = session.find(cl, id, modelTypeMap.get(cl).getHints());
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
        }
        return Optional.ofNullable(model);
    }

    public <T extends Model> List<T> getAll(Class<T> cl) {
        Session session = session();
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

    public <T extends Model> List<T> getAllByFields(Class<T> cl, Map<String, List<String>> values) {
        List<T> results = new ArrayList<>();
        Session session = session();
        Transaction transaction = session.beginTransaction();
        var query = getStrQuery(values, queryStartMap.get(SEARCH).apply(cl));
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

    public <T extends Model> void delete(T model) {
        Session session = session();
        Transaction transaction = session.beginTransaction();
        try {
            session.remove(model);

            transaction.commit();
            log.info(model + " is deleted");
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
        }
    }

    public <T extends Model> void deleteAll(Class<T> cl) {
        Session session = session();
        Transaction transaction = session.beginTransaction();
        try {
            session.createQuery("delete from " + cl.getSimpleName(), cl)
                    .setHint("jakarta.persistence.fetchgraph", modelTypeMap.get(cl).getHints())
                    .executeUpdate();
            transaction.commit();
            log.info("All " + cl.getSimpleName() + "s are deleted");
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
        }
    }

    public <T extends Model> void deleteAllByFields(Class<T> cl, Map<String, List<String>> values) {
        Session session = session();
        Transaction transaction = session.beginTransaction();
        var query = getStrQuery(values, queryStartMap.get(DELETE).apply(cl));
        try {
            session.createQuery(query, cl)
                    .setHint("jakarta.persistence.fetchgraph", modelTypeMap.get(cl).getHints())
                    .executeUpdate();
            transaction.commit();
            log.info("All " + cl.getSimpleName() + "s with " + values + " are deleted");
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
        }
    }

    private static <T extends Model> String getSetPart(Class<T> cl, Map<String, String> newValues) {
        return newValues.entrySet().stream().map(v -> "c." + v.getKey().toLowerCase() + " = " + v.getValue()).collect(Collectors.joining(",", "update " + cl.getSimpleName() + " c set ", ""));
    }


    private String getStrQuery(Map<String, List<String>> fieldValueMap, String prefix) {
        return fieldValueMap.entrySet().stream()
                .map(entry ->
                        entry.getKey().toLowerCase() + " " + entry.getValue().stream().collect(Collectors.joining(
                                "','",
                                "in('",
                                "')")))
                .collect(Collectors.joining(
                        " and ", prefix, ""));
    }
}