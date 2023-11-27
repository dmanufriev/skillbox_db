package skillbox.code.entity;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name="positions")
public class Position implements Serializable {
    @Id
    @Column(name="position_id")
    private int id;
    @Column(name="title")
    private String title;
    @Column(name="hour_salary")
    private int hourSalary;

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

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getHourSalary() {
        return hourSalary;
    }

    public void setHourSalary(int hourSalary) {
        this.hourSalary = hourSalary;
    }
}
