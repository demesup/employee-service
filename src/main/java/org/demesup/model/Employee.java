package org.demesup.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Data
@Table
@NamedEntityGraph(
        name = "employee-graph",
        attributeNodes = @NamedAttributeNode("department")
)
public class Employee implements Model {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "emp_id")
    private int empId;
    @Basic
    @Column(name = "name", nullable = false, length = 30)
    private String name;
    @Basic
    @Column(name = "surname")
    private String surname;
    @Basic
    @Column(name = "gender")
    private Object gender;
    @Basic
    @Column(name = "salary", precision = 2)
    private Double salary;
    @Basic
    @Column(name = "email", unique = true, nullable = false)
    private String email;
    @ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST,CascadeType.MERGE})
    @OnDelete(action = OnDeleteAction.NO_ACTION)
    @JoinColumn(name = "dep_id", referencedColumnName = "dep_id")
    private Department department;

    @Override
    public String toString() {
        return "Employee{" +
                "empId=" + empId +
                ", name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", gender=" + gender +
                ", salary=" + salary +
                ", email='" + email + '\'' +
                ", department=" + department +
                '}';
    }
}
