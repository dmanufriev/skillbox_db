package skillbox.code.entity;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name="employees")
public class Employee implements Serializable {
    @Id
    @Column(name = "employee_id")
    private int id;
    @Column(name = "name")
    private String name;
    @Column(name = "position_id")
    private int positionId;

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public int getPositionId() {
        return positionId;
    }
    public void setPositionId(int positionId) {
        this.positionId = positionId;
    }
}
