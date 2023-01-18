package org.demesup.repository;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.demesup.NotEntityClassException;
import org.demesup.model.Model;
import org.demesup.model.field.Field;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static org.demesup.AppController.Action.*;
import static org.demesup.HibernateUtil.session;
import static org.demesup.ModelType.modelTypeMap;

@Getter
@Slf4j
public class Repository {
    Session session = session();

    public <T extends Model> void save(T model) {
        checkClass(model.getClass());
        execute(() -> session.persist(model), model + " is saved");
    }

    public <T extends Model> void update(T model) {
        checkClass(model.getClass());
        execute(() -> session.merge(model), model + " is updated");
    }

    public <T extends Model> void updateAll(Class<T> cl, Map<Field, String> newValues) {
        checkClass(cl);
        String setPart = getSetPart(cl, newValues);
        execute(() -> session.createNativeQuery(setPart, cl)
                .setHint("jakarta.persistence.fetchgraph", modelTypeMap.get(cl).getHints())
                .executeUpdate(), "All " + cl.getSimpleName() + "s are updated");
    }

    public <T extends Model> void updateAllByFields(Class<T> cl,
                                                    Map<Field, List<String>> oldValues,
                                                    Map<Field, String> newValues) {
        checkClass(cl);
        String strQuery = getStrQuery(oldValues, getSetPart(cl, newValues) + " where ", "c.");
        execute(() -> session.createNativeQuery(strQuery, cl)
                .setHint("jakarta.persistence.fetchgraph", modelTypeMap.get(cl).getHints())
                .executeUpdate(), "All departments with " + oldValues + " are updated to " + newValues);
    }

    public <T extends Model> Optional<T> getById(Class<T> cl, long id) {
        checkClass(cl);
        return execute(() -> session.find(cl, id, modelTypeMap.get(cl).getHints()));
    }

    public <T extends Model> List<T> getAll(Class<T> cl) {
        checkClass(cl);
        return execute(() ->
                session.createQuery("FROM " + cl.getSimpleName(), cl)
                        .setHint("jakarta.persistence.fetchgraph", modelTypeMap.get(cl).getHints())
                        .getResultList()).orElse(new LinkedList<>());
    }

    public <T extends Model> List<T> getAllByFields(Class<T> cl, Map<Field, List<String>> values) {
        checkClass(cl);
        String strQuery = getStrQuery(values, queryStartMap.get(SEARCH).apply(cl), "");
        return execute(() -> session.createNativeQuery(strQuery, cl).getResultList()).orElse(new LinkedList<>());
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
        checkClass(model.getClass());
        execute(() -> session.remove(model), model + " is deleted");
    }

    public <T extends Model> void deleteAll(Class<T> cl) {
        checkClass(cl);
        execute(() -> session.createNativeQuery("truncate table " + cl.getSimpleName(), cl)
                .executeUpdate(), "All " + cl.getSimpleName() + "s are deleted");
    }

    public <T extends Model> void deleteAllByFields(Class<T> cl, Map<Field, List<String>> values) {
        checkClass(cl);
        var query = getStrQuery(values, queryStartMap.get(DELETE).apply(cl), "c.");
        execute(() -> session.createNativeQuery(query, cl)
                .setHint("jakarta.persistence.fetchgraph", modelTypeMap.get(cl).getHints())
                .executeUpdate(), "All " + cl.getSimpleName() + "s with " + values + " are deleted");
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
        if (list.isEmpty()) return;
        checkClass(list.get(0).getClass());
        execute(() -> list.forEach(session::persist), list + " is saved");
    }

    private void execute(Runnable task, String messageIfCommitted) {
        Transaction transaction = session.beginTransaction();
        try {
            task.run();
            transaction.commit();
            log.info(messageIfCommitted);
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
        }
    }

    private <T> Optional<T> execute(Supplier<T> task) {
        Transaction transaction = session.beginTransaction();
        try {
            T result = task.get();
            transaction.commit();
            return Optional.ofNullable(result);
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
        }
        return Optional.empty();
    }

    private <T extends Model> void checkClass(Class<T> cl) {
        if (!cl.isAnnotationPresent(Entity.class)) throw new NotEntityClassException(cl);
    }
}