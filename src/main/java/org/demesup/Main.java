package org.demesup;

import org.demesup.model.Department;
import org.demesup.model.Employee;
import org.utils.Utils;

import java.io.IOException;
import java.util.Map;

import static org.demesup.controller.Controller.repository;


public class Main {

    public static void main(String[] args) throws IOException {
        System.out.println(Utils.listInSeparatedLines(repository.getByFields(Employee.class, Map.of("salary","1000","gender","FEMALE"))));
//        System.out.println(String.valueOf(EmployeeField.DEPARTMENT.valueFromUser()));
        System.out.println(repository.getById(Department.class, 1));
        AppController.start();
    }

}