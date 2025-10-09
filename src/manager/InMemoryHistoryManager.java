package manager;

import tasks.Task;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.HashMap;

public class InMemoryHistoryManager implements HistoryManager{
    public LinkedList<Node> history = new LinkedList<>();

    public HashMap<Integer, Node> historyMap = new HashMap<>();

    @Override
    public void addTaskToHistory(Task task) {
        if (historyMap.containsKey(task.id)) {
            remove(task.id);
        }
        Node newLast = new Node(null, null, task);
        if (history.isEmpty()) {
            historyMap.put(task.id, newLast);
            history.add(historyMap.get(task.id));
        } else {
            Node oldLastNode = history.getLast();
            historyMap.put(task.id, newLast);
            history.add(historyMap.get(task.id));
            linkLast(task.id, oldLastNode.task.id);
        }
    }

    @Override
    public void remove(int id) {
        history.remove(historyMap.get(id));
        removeNode(historyMap.get(id));
    }

    @Override
    public ArrayList<Task> getHistory() {
        return getTasks();
    }

    public void removeNode(Node node) {
        int id = node.task.id;
        if (historyMap.get(id).previous == null) {
            historyMap.get(id).next.previous = null;
        } else if (historyMap.get(id).next == null) {
            historyMap.get(id).previous.next = null;
        } if (historyMap.get(id).previous != null && historyMap.get(id).next != null) {
            historyMap.get(id).previous.next = historyMap.get(id).next;
            historyMap.get(id).next.previous = historyMap.get(id).previous;
        }
        historyMap.remove(id);
    }

    void linkLast(int newLast, int oldLast) {
        historyMap.get(oldLast).next = historyMap.get(newLast);
        historyMap.get(newLast).previous = historyMap.get(oldLast);
    }

    ArrayList<Task> getTasks() {
        ArrayList<Task> list = new ArrayList<>();
        for (Node n : history) {
            list.add(n.task);
        }
        return list;
    }
}
