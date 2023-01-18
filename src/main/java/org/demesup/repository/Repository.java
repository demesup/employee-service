package org.demesup.repository;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.demesup.model.Model;
import org.demesup.model.field.Field;
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
            transaction.commit();
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

    public <T extends Model> void updateAll(Class<T> cl, Map<Field, String> newValues) {
        Session session = session();
        Transaction transaction = session.beginTransaction();
        String setPart = getSetPart(cl, newValues);
        try {
            session.createNativeQuery(setPart, cl)
                    .setHint("jakarta.persistence.fetchgraph", modelTypeMap.get(cl).getHints())
                    .executeUpdate();
            transaction.commit();
            log.info("All " + cl.getSimpleName() + "s are updated");
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
        }
    }

    public <T extends Model> void updateAllByFields(Class<T> cl,
                                                    Map<Field, List<String>> oldValues,
                                                    Map<Field, String> newValues) {
        Session session = session();
        Transaction transaction = session.beginTransaction();
        String strQuery = getStrQuery(oldValues, getSetPart(cl, newValues) + " where ", "c.");
        try {
            session.createNativeQuery(strQuery, cl)
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

    public <T extends Model> List<T> getAllByFields(Class<T> cl, Map<Field, List<String>> values) {
        List<T> results = new ArrayList<>();
        Session session = session();
        Transaction transaction = session.beginTransaction();
        String strQuery = getStrQuery(values, queryStartMap.get(SEARCH).apply(cl), "");
        try {
            results = session.createNativeQuery(strQuery, cl).getResultList();
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
        }
        return results;
    }

     /*In getAllByFields(...) can be also used:
                  var builder = session.getCriteriaBuilder();
                  JpaCriteriaQuery<T> criteriaQuery = builder.createQuery(cl);
                  Root<T> root = criteriaQuery.from(cl);
                  Predicate[] predicates = getPredicates(values, root);
                  results = session.createQuery(criteriaQuery.where(predicates)).getResultList();

    private static <T extends Model> Predicate[] getPredicates(Map<String, List<String>> values, Root<T> root) {
        Predicate[] predicates = new Predicate[values.size()];
        AtomicInteger i = new AtomicInteger(0);
        values.forEach((k, v) -> predicates[i.getAndIncrement()] = root.get(k.toLowerCase()).in(v));
        return predicates;
    }
     */

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
            session.createNativeQuery("truncate table " + cl.getSimpleName(), cl)
                    .executeUpdate();
            transaction.commit();
            log.info("All " + cl.getSimpleName() + "s are deleted");
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
        }
    }

    public <T extends Model> void deleteAllByFields(Class<T> cl, Map<Field, List<String>> values) {
        Session session = session();
        Transaction transaction = session.beginTransaction();
        var query = getStrQuery(values, queryStartMap.get(DELETE).apply(cl), "c.");
        try {
            session.createNativeQuery(query, cl)
                    .setHint("jakarta.persistence.fetchgraph", modelTypeMap.get(cl).getHints())
                    .executeUpdate();
            transaction.commit();
            log.info("All " + cl.getSimpleName() + "s with " + values + " are deleted");
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
        }
    }

    private static <T extends Model> String getSetPart(Class<T> cl, Map<Field, String> newValues) {
        return newValues.entrySet().stream()
                .map(v -> "c." + v.getKey().toString().toLowerCase() + " = '" + v.getValue() + "'")
                .collect(Collectors.joining(",", "update " + cl.getSimpleName() + " c set ", ""));
    }


    private String getStrQuery(Map<Field, List<String>> fieldValueMap, String prefix, String l) {
        return fieldValueMap.entrySet().stream()
                .map(entry ->
                        l + entry.getKey().toString().toLowerCase() + " " + entry.getValue().stream().collect(Collectors.joining(
                                "','",
                                "in('",
                                "')")))
                .collect(Collectors.joining(
                        " and ", prefix.endsWith("\\s") ? prefix : prefix + " ", ""));
    }

    public <T extends Model> void saveAll(List<T> list) {
        Session session = session();
        Transaction transaction = session.beginTransaction();
        try {
            list.forEach(session::persist);
            transaction.commit();
            log.info(list + " is saved");
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
        }
    }
}