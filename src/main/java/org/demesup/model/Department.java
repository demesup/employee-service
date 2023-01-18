package org.demesup.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.Collection;
import java.util.stream.Collectors;

@Entity
@Data
@Table
@NamedEntityGraph(
        name = "department-graph",
        attributeNodes = @NamedAttributeNode("employeesByDepId")
)
@RequiredArgsConstructor
@NoArgsConstructor
public class Department implements Model {
    @Id
    @Column(name = "dep_id")
    @GeneratedValue(generator = "sequence-generator")
    @GenericGenerator(
            name = "sequence-generator",
            strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
            parameters = {
                    @org.hibernate.annotations.Parameter(name = "sequence_name", value = "user_sequence"),
                    @org.hibernate.annotations.Parameter(name = "initial_value", value = "1"),
                    @org.hibernate.annotations.Parameter(name = "increment_size", value = "1")
            }
    )
    private int dep_id;
    @NonNull
    @Basic
    @Column(name = "name", nullable = false)
    private String name;
    @NonNull
    @Basic
    @Column(name = "location", nullable = false)
    private String location;
    @OneToMany(mappedBy = "department")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Collection<Employee> employeesByDepId;

    @Override
    public String toString() {
        return "Department{" +
                "dep_id=" + dep_id +
                ", name='" + name + '\'' +
                ", location='" + location + '\'' +
                '}';
    }

    public String moreInfo() {
        String employees = employeesByDepId.isEmpty() ? "no employees" : employeesByDepId.stream()
                .map(e -> e.getName() + " " + e.getSurname())
                .collect(Collectors.joining("}\n\t{", "\n\t{", "}"));
        return "Department{" +
                "dep_id=" + dep_id +
                ", name='" + name + '\'' +
                ", location='" + location + '\'' +
                ", employees: " + employees +
                '}';
    }

}
