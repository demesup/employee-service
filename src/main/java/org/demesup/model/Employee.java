package org.demesup.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table
public class Employee
//        extends PanacheEntity
        implements Model {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "emp_id")
    private int empId;
    @Basic
    @Column(name = "name")
    private String name;
    @Basic
    @Column(name = "surname")
    private String surname;
    @Basic
    @Column(name = "gender")
    private Object gender;
    @Basic
    @Column(name = "salary")
    private Integer salary;
    @Basic
    @Column(name = "email")
    private String email;
    @Basic
//    @Column(name = "dep_id")
    @Column(name = "dep_id")
    private Integer dep_id;
    @ManyToOne
    @JoinColumn(name = "dep_id", referencedColumnName = "dep_id", insertable = false, updatable = false)
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
