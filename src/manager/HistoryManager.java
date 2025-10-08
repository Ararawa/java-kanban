package manager;

import tasks.Task;

import java.util.List;

public interface HistoryManager {

    void addTaskToHistory(Task task);

    void remove(int id);

    List<Task> getHistory();
}
