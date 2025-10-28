package manager;

import tasks.Subtask;
import tasks.Task;

import java.util.List;

public interface TaskManager {

    List<Task> getAllTasks();

    void deleteAllTasks();

    Task getByID(int id);

    void create(Task task);

    void update(Task task);

    void deleteByID(int id);

    List<Subtask> getSubtasksByEpicID(int epicID);

    List<Task> getHistory();

    List<Task> getPrioritizedTasks();
}
