package manager;

import tasks.Task;

import java.util.ArrayList;

public class InMemoryHistoryManager implements HistoryManager{
    public ArrayList<Task> history = new ArrayList<>();
    public static final int HYSTORY_SIZE = 10;

    @Override
    public void addTaskToHistory(Task task) {
        if (history.size() == HYSTORY_SIZE) {
            history.removeFirst();
        }
        history.add(task);
    }

    @Override
    public ArrayList<Task> getHistory() {
        return history;
    }
}
