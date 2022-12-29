package org.demesup.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;

@Entity
@Data
@NoArgsConstructor
public class Department implements Model{
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "dep_id")
    private int depId;
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
                "depId=" + depId +
                ", name='" + name + '\'' +
                ", location='" + location + '\'' +
                '}';
    }
}
