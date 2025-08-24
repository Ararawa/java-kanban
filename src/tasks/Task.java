package tasks;

import java.util.Objects;

public class Task {
    public String name;

    public int getID() {
        return id;
    }

    public final int id;
    public String description;
    public TaskStatus status;

    public Task(String name, int id, String description, TaskStatus status) {
        this.name = name;
        this.id = id;
        this.status = status;
        this.description = description;
    }

    @Override
    public int hashCode() {
        return 31 * Objects.hash(id);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj == null) {
            return false;
        } else if (this.getClass() != obj.getClass()) {
            return false;
        }
        Task anObj = (Task) obj;
        return id == anObj.id;
    }

    @Override
    public String toString() {
        return "tasks.Task{" +
                "name='" + name + '\'' +
                ", id=" + id +
                ", description='" + description + '\'' +
                ", status=" + status +
                '}';
    }
}
