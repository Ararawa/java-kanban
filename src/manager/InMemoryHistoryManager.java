package manager;

import tasks.Task;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class InMemoryHistoryManager implements HistoryManager{
    public ArrayList<Task> history = new ArrayList<>();
    public static final int HYSTORY_SIZE = 10;

    public LinkedHashMap<Integer, Task> historyMap = new LinkedHashMap<>();

    @Override
    public void addTaskToHistory(Task task) {
        historyMap.remove(task.id);
        historyMap.put(task.id, task);
    }

    @Override
    public void remove(int id) {
        historyMap.remove(id);
    }

    @Override
    public ArrayList<Task> getHistory() {
        history.clear();
        for (int id : historyMap.keySet()) {
            history.add(historyMap.get(id));
        }
        return history;
    }
}
