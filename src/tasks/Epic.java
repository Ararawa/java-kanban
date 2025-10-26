package tasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    public Epic(String name, String description, TaskStatus status) {
        super(name, description, status);
    }

    private List<Integer> epicSubtasks = new ArrayList<>();

    public LocalDateTime endTime;

    @Override
    public String toString() {
        return "tasks.Epic{" + super.toString() +
                " epicSubtasks=" + epicSubtasks +
                "}";
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    public List<Integer> getEpicSubtasks() {
        return epicSubtasks;
    }

    public void setEpicSubtasks(List<Integer> epicSubtasks) {
        this.epicSubtasks = epicSubtasks;
    }
}
