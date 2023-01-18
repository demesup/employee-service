package org.demesup.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.demesup.model.field.Gender;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.Parameter;

@Entity
@Data
@Table
@NamedEntityGraph(
        name = "employee-graph",
        attributeNodes = @NamedAttributeNode("department"))
@RequiredArgsConstructor
@NoArgsConstructor
public class Employee implements Model {
    @Id
    @Column(name = "emp_id")
    @GeneratedValue(generator = "sequence-generator")
    @GenericGenerator(
            name = "sequence-generator",
            strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
            parameters = {
                    @Parameter(name = "sequence_name", value = "user_sequence"),
                    @Parameter(name = "initial_value", value = "1"),
                    @Parameter(name = "increment_size", value = "1")
            }
    )
    private int empId;
    @Basic
    @Column(name = "name", nullable = false, length = 30)
    @NonNull
    private String name;
    @Basic
    @Column(name = "surname", nullable = false)
    @NonNull
    private String surname;
    @Enumerated(EnumType.STRING)
    @Column(name = "gender")
    @NonNull
    private Gender gender;
    @Basic
    @Column(name = "salary")
    @NonNull
    private Integer salary;
    @Basic
    @Column(name = "email", unique = true, nullable = false)
    @NonNull
    private String email;
    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @OnDelete(action = OnDeleteAction.NO_ACTION)
    @JoinColumn(name = "dep_id", referencedColumnName = "dep_id")
    @NonNull
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
