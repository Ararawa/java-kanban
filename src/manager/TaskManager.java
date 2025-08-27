package manager;

import tasks.Subtask;
import tasks.Task;

import java.util.ArrayList;

public interface TaskManager {

    ArrayList<Task> returnAllTasks();

    void deleteAllTasks();

    Task getByID(int id);

    void create(Task task);

    void update(Task task);

    void deleteByID(int id);

    ArrayList<Subtask> getSubtasksByEpicID(int epicID);

    ArrayList<Task> getHistory();
}
