import java.util.ArrayList;

public class Epic extends Task{
    public Epic(String name, int numberID, String description, TaskStatus status) {
        super(name, numberID, description, status);
    }

    public ArrayList<Integer> epicSubtasks = new ArrayList<>();

    @Override
    public String toString() {
        return "Epic{" + super.toString() +
                " epicSubtasks=" + epicSubtasks +
                "}";
    }
}
