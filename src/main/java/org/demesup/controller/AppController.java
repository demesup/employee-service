package org.demesup.controller;

import org.demesup.model.Department;
import org.demesup.model.Employee;
import org.demesup.model.EmployeeField;
import org.demesup.model.Gender;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import static org.utils.Utils.listInSeparatedLines;

public class AppController {
    static SessionFactory sessionFactory;
    public static Session session;
    public static Controller controller;

    static {
        sessionFactory = new Configuration().configure().buildSessionFactory();
        session = sessionFactory.openSession();
        controller = new Controller(session);

    }

    public static void main(String[] args) {

        System.out.println(listInSeparatedLines(controller.getAll(Department.class)));
        System.out.println("asdflfklsfa");

        System.out.println(listInSeparatedLines(controller.getAllByField(Employee.class, EmployeeField.GENDER, Gender.FEMALE.toString())));
        session.close();
    }
}
