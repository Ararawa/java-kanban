package tasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Task implements Comparable<Task> {
    public String name;

    public Duration duration;
    public LocalDateTime startTime;

    public int getID() {
        return id;
    }

    public int id;
    public String description;
    public TaskStatus status;

    public Task(String name, String description, TaskStatus status) {
        this.name = name;
        this.status = status;
        this.description = description;
    }

    public Task(String name, String description, TaskStatus status, LocalDateTime startTime, Duration duration) {
        this.name = name;
        this.status = status;
        this.description = description;
        this.startTime = startTime;
        this.duration = duration;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        if (startTime == null) {
            return null;
        }
        return startTime.plus(duration);
    }

    @Override
    public int compareTo(Task other) {
        return this.getStartTime().compareTo(other.getStartTime());
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
                ", duration= " + duration +
                ", startTime= " + startTime +
                '}';
    }
}
