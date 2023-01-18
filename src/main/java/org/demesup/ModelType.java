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
    EMPLOYEE(new EmployeeController(), EmployeeField.values()),
    DEPARTMENT(new DepartmentController(), DepartmentField.values());
    final Controller controller;
    final Field[] fields;
    public static final Map<Class<? extends Model>, ModelType> modelTypeMap = Map.of(
            Employee.class, ModelType.EMPLOYEE,
            Department.class, ModelType.DEPARTMENT
    );

    ModelType(Controller controller, Field[] values) {
        this.controller = controller;
        this.fields = values;
    }

    public Map<String, Object> getHints() {
        EntityGraph<?> graph = getEntityGraph();

        Map<String, Object> hints = new HashMap<>();
        hints.put("jakarta.persistence.fetchgraph", graph);
        return hints;
    }

    public EntityGraph<?> getEntityGraph() {
        try (EntityManager entityManager = HibernateUtil.getSessionFactory().createEntityManager()) {
            return entityManager.getEntityGraph(this.toString().toLowerCase() + "-graph");
        }
    }
}
