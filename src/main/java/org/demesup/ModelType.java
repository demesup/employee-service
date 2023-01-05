package org.demesup;

import jakarta.persistence.EntityGraph;
import jakarta.persistence.EntityManager;
import lombok.Getter;
import org.demesup.controller.Controller;
import org.demesup.controller.DepartmentController;
import org.demesup.controller.EmployeeController;
import org.demesup.model.Department;
import org.demesup.model.Employee;
import org.demesup.model.Model;
import org.demesup.model.field.DepartmentField;
import org.demesup.model.field.EmployeeField;
import org.demesup.model.field.Field;

import java.util.HashMap;
import java.util.Map;

@Getter
public enum ModelType {
    EMPLOYEE(new EmployeeController(), Employee.class, EmployeeField.values()),
    DEPARTMENT(new DepartmentController(), Department.class, DepartmentField.values());
    final Controller controller;
    final Class<? extends Model> cl;
    final Field[] fields;

    <T extends Model> ModelType(Controller controller, Class<? extends Model> cl, Field[] values) {
        this.controller = controller;
        this.cl = cl;
        this.fields = values;
    }
    public static Map<Class<? extends Model>, Map> hintsMap = new HashMap<>();
    static {
        hintsMap.put(EMPLOYEE.cl, EMPLOYEE.getHints());
        hintsMap.put(DEPARTMENT.cl, DEPARTMENT.getHints());
    }
    public Map getHints() {
        EntityGraph<?> graph = getEntityGraph();

        Map hints = new HashMap();
        hints.put("javax.persistence.fetchgraph", graph);
        return hints;
    }

    public EntityGraph<?> getEntityGraph() {
        EntityManager entityManager = HibernateUtil.getSessionFactory().createEntityManager();
        return entityManager.getEntityGraph(this.cl.getSimpleName().toLowerCase() + "-graph");
    }

    public String hint1Parameter() {
        return "javax.persistence.fetchgraph";
    }
}
