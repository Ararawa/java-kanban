package tasks;

public class Subtask extends Task{

    public int epicID;

    public Subtask(String name, int id, String description, TaskStatus status, int epicID) {
        super(name, id, description, status);
        this.epicID = epicID;
    }

    @Override
    public String toString() {
        return "tasks.Subtask{" +
                "epicID=" + epicID +
                "} " + super.toString();
    }
}
