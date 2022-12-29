package org.demesup.controller;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import lombok.AllArgsConstructor;
import org.demesup.model.Field;
import org.demesup.model.Model;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
public class Controller {

    Session session;

    public <T extends Model> void save(T model) {
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();

            session.persist(model);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
        }
    }

    public <T extends Model> void update(Class<T> cl, T model) {
        Transaction transaction = null;
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
            transaction = session.beginTransaction();

            model = session.getReference(cl, id);

            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
        }
        return Optional.ofNullable(model);
    }

    public <T extends Model> List<T> getAll(Class<T> cl) {

        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();

            String s = "select s from " + cl.getSimpleName() + " s";
            System.out.println(s);
            List<T> student = session.getEntityManagerFactory().createEntityManager().createQuery(s, cl).getResultList();
            System.out.println(student);
            transaction.commit();
            return student;
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            return new ArrayList<>();
        }
    }

    public <T extends Model> List<T> getAllByField(Class<T> cl, Field field, String value) {
        String fieldStr = field.toString().toLowerCase();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();

            String hql = "FROM Employee E WHERE E." + fieldStr + " = :" + fieldStr;
            Query<T> query = session.createQuery(hql, cl);
            query.setParameter(fieldStr, value);

            List<T> list = query.getResultList();

//            CriteriaBuilder builder = session.getCriteriaBuilder();
//
//            CriteriaQuery<T> cr = builder.createQuery(cl);
//            Root<T> root = cr.from(cl);
//            cr.select(root).where(builder.equal(root.get(field.toString().toLowerCase()), value));  //here you pass a class field, not a table column (in this example they are called the same)
//
//            Query<T> query = session.createQuery(cr);
//            query.setMaxResults(1);
//            List<T> resultList = query.getResultList();
//            System.out.println(resultList);
            transaction.commit();
            return list;
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            return new ArrayList<>();
        }
    }

    public <T extends Model> void deleteByField(Class<T> cl, long id) {
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            session.remove(getById(cl, id));
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
        }
    }


    public <T extends Model> void delete(Class<T> cl, long id) {
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            session.remove(getById(cl, id));
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
        }
    }
}
