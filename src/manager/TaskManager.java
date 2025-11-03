package manager;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.List;

public interface TaskManager {

    List<Task> getAllTasks();

    void deleteAllTasks();

    Task getByID(int id);

    boolean create(Task task);

    boolean update(Task task);

    void deleteByID(int id);

    List<Subtask> getSubtasksByEpicID(int epicID);

    List<Task> getHistory();

    List<Task> getPrioritizedTasks();

    List<Task> getTasks();

    List<Subtask> getSubtasks();

    List<Epic> getEpics();

    void clearHistory();
}
