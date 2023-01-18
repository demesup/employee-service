package org.demesup.model;

import jakarta.persistence.*;
import lombok.*;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "dep_id")
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
        return "Department{" +
                "dep_id=" + dep_id +
                ", name='" + name + '\'' +
                ", location='" + location + '\'' +
                ", employees: " +
                employeesByDepId.stream()
                        .map(e -> e.getName() + " " + e.getSurname())
                        .collect(Collectors.joining("}\n\t{", "\n\t{", "}")) +
                '}';
    }

}
