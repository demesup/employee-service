package org.demesup.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class Employee implements Model {
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
    @ManyToOne
    @JoinColumn(name = "dep_id", referencedColumnName = "dep_id")
    private Department department;
}
