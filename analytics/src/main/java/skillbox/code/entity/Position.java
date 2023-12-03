package skillbox.code.entity;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name="positions")
public class Position implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="position_id", nullable = false)
    private Integer id;
    @Column(name="title", unique = true, nullable = false, length = 100)
    private String title;
    @Column(name="hour_salary", nullable = false)
    private Integer hourSalary;

    public Position() {
        this.id = 0;
        this.title = "";
        this.hourSalary = 0;
    }

    public Position(String title, int hourSalary) {
        this.id = 0;
        this.title = title;
        this.hourSalary = hourSalary;
    }

    public Integer getId() {
        return id;
    }
    public void setId(Integer id) { this.id = id; }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getHourSalary() {
        return hourSalary;
    }

    public void setHourSalary(Integer hourSalary) {
        this.hourSalary = hourSalary;
    }
}
