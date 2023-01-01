package org.demesup.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Collection;

@Entity
@Data
@Table
public class Department implements Model{
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "dep_id")
    private int dep_id;
    @Basic
    @Column(name = "name")
    private String name;
    @Basic
    @Column(name = "location")
    private String location;
    @OneToMany(mappedBy = "department")
    private Collection<Employee> employeesByDepId;

    @Override
    public String toString() {
        return "Department{" +
                "dep_id=" + dep_id +
                ", name='" + name + '\'' +
                ", location='" + location + '\'' +
                '}';
    }
}
