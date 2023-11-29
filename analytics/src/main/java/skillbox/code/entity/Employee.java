package skillbox.code.entity;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name="employees")
public class Employee implements Serializable {
    //TODO Ссылка на настройку аннотаций: https://javarush.com/quests/lectures/questhibernate.level09.lecture01
    //TODO Как работать с ID: https://habr.com/ru/companies/haulmont/articles/653843/ В статье выше есть видео обзор

    // TODO Ссылка на id другого класса https://javarush.com/quests/lectures/questhibernate.level10.lecture02
    /* @ManyToOne
        @JoinColumn(name="employee_id", nullable=true)
        public Employee employee;
    */

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
