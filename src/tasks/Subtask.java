package tasks;

public class Subtask extends Task {

    public int epicID;

    public Subtask(String name, String description, TaskStatus status, int epicID) {
        super(name, description, status);
        this.epicID = epicID;
    }

    @Override
    public String toString() {
        return "tasks.Subtask{" +
                "epicID=" + epicID +
                "} " + super.toString();
    }
}
