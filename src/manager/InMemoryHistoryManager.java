package manager;

import tasks.Task;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.HashMap;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    public LinkedList<Node> history = new LinkedList<>();

    public HashMap<Integer, Node> historyMap = new HashMap<>();

    @Override
    public void add(Task task) {
        if (historyMap.containsKey(task.id)) {
            remove(task.id);
        }
        Node newLast = new Node(null, null, task);
        if (historyMap.isEmpty()) {
            historyMap.put(task.id, newLast);
            history.add(historyMap.get(task.id));
        } else {
            int oldLast = history.getLast().task.id;
            historyMap.put(oldLast, history.getLast());
            historyMap.put(task.id, newLast);
            history.add(historyMap.get(task.id));
            linkLast(task.id, oldLast);
        }
    }

    @Override
    public void remove(int id) {
        if (historyMap.get(id) != null) {
            removeNode(historyMap.get(id));
        }
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    public void removeNode(Node node) {
        int id = node.task.id;
        if (historyMap.get(id).previous == null && historyMap.get(id).next != null) {
            historyMap.get(id).next.previous = null;
        } else if (historyMap.get(id).previous != null && historyMap.get(id).next == null) {
            historyMap.get(id).previous.next = null;
        } else if (historyMap.get(id).previous != null && historyMap.get(id).next != null) {
            historyMap.get(id).previous.next = historyMap.get(id).next;
            historyMap.get(id).next.previous = historyMap.get(id).previous;
        }
        historyMap.remove(id);
    }

    void linkLast(int newLast, int oldLast) {
        historyMap.get(oldLast).next = historyMap.get(newLast);
        historyMap.get(newLast).previous = historyMap.get(oldLast);
    }

    List<Task> getTasks() {
        List<Task> historyOut = new ArrayList<>();
        if (historyMap.isEmpty()) {
            return historyOut;
        }
        int nextID = -1;
        for (int id : historyMap.keySet()) {
            if (historyMap.get(id).previous == null) {
                historyOut.add(historyMap.get(id).task);
                if (historyMap.get(id).next != null) {
                    nextID = historyMap.get(id).next.task.id;
                } else {
                    return historyOut;
                }
                for (int i = 0; i < historyMap.size(); i++) {
                    historyOut.add(historyMap.get(nextID).task);
                    if (historyMap.get(nextID).next != null) {
                        nextID = historyMap.get(nextID).next.task.id;
                    } else {
                        return historyOut;
                    }
                }
            }
        }
        return historyOut;
    }

    @Override
    public void clearHistory() {
        history.clear();
        historyMap.clear();
    }
}
