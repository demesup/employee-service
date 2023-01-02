package org.demesup.repository;

import lombok.Getter;
import org.demesup.HibernateUtil;
import org.demesup.model.Model;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.demesup.HibernateUtil.session;


@Getter
public class Repository {
    public <T extends Model> void save(T model) {
        Transaction transaction = null;
        Session session = session();
        try {
            transaction = session.beginTransaction();
            session.persist(model);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
        }
    }

    public <T extends Model> void update(T model) {
        Transaction transaction = null;
        Session session = session();
        try {
            transaction = session.beginTransaction();
            session.merge(model);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
        }
    }

    public <T extends Model> Optional<T> getById(Class<T> cl, long id) {
        T model = null;

        Transaction transaction = null;
        try {
            Session session = session();
            transaction = session.beginTransaction();

            model = session.getReference(cl, id);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
        }
        return Optional.ofNullable(model);
    }

    public <T extends Model> List<T> getByFields(Class<T> cl, Map<String, String> fieldValueMap) {
        Transaction transaction = null;
        try {
            transaction = session().beginTransaction();

            String strQuery = getStrQuery(fieldValueMap, cl);
            System.out.println(strQuery);
            List<T> results = session().createQuery(strQuery, cl).getResultList();
            transaction.commit();
            return results;
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
        }
        return new ArrayList<>();
    }

    private <T extends Model> String getStrQuery(Map<String, String> fieldValueMap, Class<T> cl) {
        return fieldValueMap.entrySet().stream()
                .map(entry -> entry.getKey() + " = '" + entry.getValue() + "'")
                .collect(Collectors.joining(
                        " and ",
                        "select c from " + cl.getSimpleName() + " c where ",
                        ""));
    }


    public <T extends Model> List<T> getAll(Class<T> cl) {

        Transaction transaction = null;
        try {
            transaction = session().beginTransaction();
            String s = "select s from " + cl.getSimpleName() + " s";
            List<T> resultList = HibernateUtil.sessionFactory.createEntityManager().createQuery(s, cl).getResultList();
            transaction.commit();
            return resultList;
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            return new ArrayList<>();
        }
    }


    public <T extends Model> void delete(T model) {
        Transaction transaction = null;
        Session session = session();
        try {
            transaction = session.beginTransaction();
            session().remove(model);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
        }
    }
}
