package skillbox.code.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name="tasks")
public class Task implements Serializable {
    @Id
    @Column(name = "task_id")
    private int id;

    @Column(name = "title")
    private String title;

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Task)) {
            return false;
        }
        Task item = (Task) o;
        return Objects.equals(id, item.getId());
    }
}