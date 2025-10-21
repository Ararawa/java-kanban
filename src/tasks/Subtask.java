package tasks;

import java.time.Duration;
import java.time.LocalDateTime;

public class Subtask extends Task {

    public int epicID;

    public Subtask(String name, String description, TaskStatus status, int epicID) {
        super(name, description, status);
        this.epicID = epicID;
    }

    public Subtask(String name, String description, TaskStatus status,, int epicID,
                   LocalDateTime startTime, Duration duration) {
        super(name, description, status, startTime, duration);
        this.epicID = epicID;
    }

    @Override
    public String toString() {
        return "tasks.Subtask{" +
                "epicID=" + epicID +
                "} " + super.toString();
    }
}
