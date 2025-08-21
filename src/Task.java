import java.util.Objects;

public class Task {
    String name;

    public int getNumberID() {
        return numberID;
    }

    final int numberID;
    String description;
    TaskStatus status;

    public Task(String name, int numberID, String description, TaskStatus status) {
        this.name = name;
        this.numberID = numberID;
        this.status = status;
        this.description = description;
    }

    @Override
    public int hashCode() {
        return 31 * Objects.hash(numberID);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (this.getClass() != obj.getClass()) return false;
        Task anObj = (Task) obj;
        return numberID == anObj.numberID;
    }

    @Override
    public String toString() {
        return "Task{" +
                "name='" + name + '\'' +
                ", numberID=" + numberID +
                ", description='" + description + '\'' +
                ", status=" + status +
                '}';
    }
}
