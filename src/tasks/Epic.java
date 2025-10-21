package tasks;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class Epic extends Task {
    public Epic(String name, String description, TaskStatus status) {
        super(name, description, status);
    }

    private ArrayList<Integer> epicSubtasks = new ArrayList<>();

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

    public ArrayList<Integer> getEpicSubtasks() {
        return epicSubtasks;
    }

    public void setEpicSubtasks(ArrayList<Integer> epicSubtasks) {
        this.epicSubtasks = epicSubtasks;
    }
}
