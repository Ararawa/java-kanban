package manager;

import tasks.*;

import java.util.ArrayList;
import java.util.HashMap;

public class InMemoryTaskManager implements TaskManager {

    public int number = 0;

    public HashMap<Integer, Task> tasks = new HashMap<>();
    public HashMap<Integer, Subtask> subtasks = new HashMap<>();
    public HashMap<Integer, Epic> epics = new HashMap<>();

    public HistoryManager historyManager = Managers.getDefaultHistory();

    public int generateNumber() {
        return ++number;
    }

    TaskStatus calculateStatus(Epic epic) {
        int inProgress = 0;
        int done = 0;
        int news = 0;
        for (int i : epic.epicSubtasks) {
            if (subtasks.get(i).status == TaskStatus.IN_PROGRESS) {
                inProgress++;
            } else if (subtasks.get(i).status == TaskStatus.DONE) {
                done++;
            } else if (subtasks.get(i).status == TaskStatus.DONE) {
                news++;
            }
        }
        if (news == epic.epicSubtasks.size()) {
            return TaskStatus.NEW;
        } else if (done == epic.epicSubtasks.size()) {
            return TaskStatus.DONE;
        } else {
            return TaskStatus.IN_PROGRESS;
        }
    }


    @Override
    public ArrayList<Task> getAllTasks() {
        ArrayList<Task> allTasks = new ArrayList<>();
        for (int i : tasks.keySet()) {
            allTasks.add(tasks.get(i));
        }
        for (int i : epics.keySet()) {
            allTasks.add(epics.get(i));
            for (int j : subtasks.keySet()) {
                if (subtasks.get(j).epicID == epics.get(i).getID()) {
                    allTasks.add(subtasks.get(j));
                }
            }
        }
        return allTasks;
    }

    @Override
    public void deleteAllTasks() {
        for (int id : tasks.keySet()) {
            historyManager.remove(id);
        }
        for (int id : epics.keySet()) {
            historyManager.remove(id);
        }
        for (int id : subtasks.keySet()) {
            historyManager.remove(id);
        }
        tasks.clear();
        epics.clear();
        subtasks.clear();
    }

    @Override
    public Task getByID(int id) {
        if (tasks.containsKey(id)) {
            historyManager.addTaskToHistory(tasks.get(id));
            return tasks.get(id);
        } else if (epics.containsKey(id)) {
            historyManager.addTaskToHistory(epics.get(id));
            return epics.get(id);
        } else if (subtasks.containsKey(id)) {
            historyManager.addTaskToHistory(subtasks.get(id));
            return subtasks.get(id);
        }
        return null;
    }

    @Override
    public void create(Task task) {
        if (task instanceof Epic) {
            Epic epic = new Epic(task.name, task.description, task.status);
            epic.setId(generateNumber());
            epic.epicSubtasks = ((Epic) task).epicSubtasks;
            epic.status = calculateStatus(epic);
            epics.put(epic.id, epic);
        } else if (task instanceof Subtask) {
            Subtask subtask = new Subtask(task.name, task.description, task.status, ((Subtask) task).epicID);
            subtask.setId(generateNumber());
            subtasks.put(subtask.id, subtask);
            epics.get(subtask.epicID).epicSubtasks.add(subtask.id);
            epics.get(subtask.epicID).status = calculateStatus(epics.get(subtask.epicID));
        } else if (task != null) {
            Task task1 = new Task(task.name, task.description, task.status);
            task1.setId(generateNumber());
            tasks.put(task1.id, task1);
        }
    }

    @Override
    public void update(Task task) {
        if (task instanceof Epic) {
            epics.get(task.id).description = task.description;
            epics.get(task.id).name = task.name;
            epics.get(task.id).epicSubtasks = ((Epic) task).epicSubtasks;
            epics.get(task.id).status = calculateStatus(epics.get(task.id));
        } else if (task instanceof Subtask) {
            subtasks.get(task.id).description = task.description;
            subtasks.get(task.id).status = task.status;
            subtasks.get(task.id).name = task.name;
            subtasks.get(task.id).epicID = ((Subtask) task).epicID;
            epics.get(((Subtask) task).epicID).status = calculateStatus(epics.get(((Subtask) task).epicID));
        } else if (task != null) {
            tasks.get(task.id).description = task.description;
            tasks.get(task.id).status = task.status;
            tasks.get(task.id).name = task.name;
        }
    }

    @Override
    public void deleteByID(int id) {
        if (tasks.containsKey(id)) {
            tasks.remove(id);
        } else if (subtasks.containsKey(id)) {
            subtasks.remove(id);
        } else {
            for (int i = 0; i < epics.get(id).epicSubtasks.size(); i++) {
                subtasks.remove(epics.get(id).epicSubtasks.get(i));
            }
            epics.remove(id);
        }
        historyManager.remove(id);
    }

    @Override
    public ArrayList<Subtask> getSubtasksByEpicID(int epicID) {
        ArrayList<Subtask> allTasks = new ArrayList<>();
        for (int i : epics.keySet()) {
            if (i == epicID) {
                for (int j : subtasks.keySet()) {
                    if (subtasks.get(j).epicID == epics.get(i).getID()) {
                        allTasks.add(subtasks.get(j));
                    }
                }
            }
        }
        return allTasks;
    }

    @Override
    public ArrayList<Task> getHistory() {
        return (ArrayList<Task>) historyManager.getHistory();
    }
}
