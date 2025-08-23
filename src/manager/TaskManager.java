package manager;

import support.TaskStatus;
import tasks.*;

import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {

    public static int number = 0;

    public static int generateNumber() {
        return ++number;
    }

    public static HashMap<Integer, Task> tasks = new HashMap<>();
    public static HashMap<Integer, Subtask> subtasks = new HashMap<>();
    public static HashMap<Integer, Epic> epics = new HashMap<>();

    public static TaskStatus calculateStatus(Epic epic) {
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

    public static ArrayList<Task> returnAllTasks() {
        ArrayList<Task> allTasks = new ArrayList<>();
        for (int i : tasks.keySet()) {
            allTasks.add(tasks.get(i));
        }
        for (int i : epics.keySet()) {
            allTasks.add(epics.get(i));
            for (int j : subtasks.keySet()) {
                if (subtasks.get(j).epicID == epics.get(i).getNumberID()) {
                    allTasks.add(subtasks.get(j));
                }
            }
        }
        return allTasks;
    }

    public static void deleteAllTasks() {
        tasks.clear();
        epics.clear();
        subtasks.clear();
    }

    public static Task getByID(int numberID) {
        if (tasks.containsKey(numberID)) return tasks.get(numberID);
        if (epics.containsKey(numberID)) return epics.get(numberID);
        if (subtasks.containsKey(numberID)) return subtasks.get(numberID);
        return null;
    }

    public static void create(Task task) {
        if (task instanceof Epic) {
            Epic epic = new Epic(task.name, task.numberID, task.description, task.status);
            epic.epicSubtasks = ((Epic) task).epicSubtasks;
            epic.status = calculateStatus(epic);
            epics.put(epic.numberID, epic);
        } else if (task instanceof Subtask) {
            Subtask subtask = new Subtask(task.name, task.numberID, task.description, task.status,
                    ((Subtask) task).epicID);
            subtasks.put(subtask.numberID, subtask);
            epics.get(subtask.epicID).epicSubtasks.add(subtask.numberID);
            epics.get(subtask.epicID).status = calculateStatus(epics.get(subtask.epicID));
        } else if (task != null) {
            tasks.put(task.numberID, new Task(task.name, task.numberID, task.description, task.status));
        }
    }

    public static void update(Task task) {
        if (task instanceof Epic) {
            epics.get(task.numberID).description = task.description;
            epics.get(task.numberID).name = task.name;
            epics.get(task.numberID).epicSubtasks = ((Epic) task).epicSubtasks;
            epics.get(task.numberID).status = calculateStatus(epics.get(task.numberID));
        } else if (task instanceof Subtask) {
            subtasks.get(task.numberID).description = task.description;
            subtasks.get(task.numberID).status = task.status;
            subtasks.get(task.numberID).name = task.name;
            subtasks.get(task.numberID).epicID = ((Subtask) task).epicID;
            epics.get(((Subtask) task).epicID).status = calculateStatus(epics.get(((Subtask) task).epicID));
        } else if (task != null) {
            tasks.get(task.numberID).description = task.description;
            tasks.get(task.numberID).status = task.status;
            tasks.get(task.numberID).name = task.name;
        }
    }

    public static void deleteByID(int numberID) {
        if (tasks.containsKey(numberID)) {
            tasks.remove(numberID);
        } else if (subtasks.containsKey(numberID)) {
            subtasks.remove(numberID);
        } else {
            for (int i = 0; i < epics.get(numberID).epicSubtasks.size(); i++) {
                subtasks.remove(epics.get(numberID).epicSubtasks.get(i));
            }
            epics.remove(numberID);
        }
    }

    public static ArrayList<Subtask> getSubtasksByEpicID(int numberID) {
        ArrayList<Subtask> allTasks = new ArrayList<>();
        for (int i : epics.keySet()) {
            if (i == numberID) {
                for (int j : subtasks.keySet()) {
                    if (subtasks.get(j).epicID == epics.get(i).getNumberID()) {
                        allTasks.add(subtasks.get(j));
                    }
                }
            }
        }
        return allTasks;
    }
}
