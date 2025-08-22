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

    static void create(Task object) {
        if (object.getClass() == Task.class) {
            Task obj = (Task) object;
            Task task = new Task(obj.name, obj.numberID, obj.description, obj.status);
            tasks.put(task.numberID, task);
        } else if (object.getClass() == Epic.class) {
            Epic obj = (Epic) object;
            Epic epic = new Epic(obj.name, obj.numberID, obj.description, obj.status);
            epic.epicSubtasks = obj.epicSubtasks;
            epic.status = calculateStatus(epic);
            epics.put(epic.numberID, epic);
        } else if (object.getClass() == Subtask.class) {
            Subtask obj = (Subtask) object;
            Subtask subtask = new Subtask(obj.name, obj.numberID, obj.description, obj.status, obj.epicID);
            subtasks.put(subtask.numberID, subtask);
            epics.get(subtask.epicID).epicSubtasks.add(subtask.numberID);
            epics.get(subtask.epicID).status = calculateStatus(epics.get(subtask.epicID));
        }
    }

    static void update(Task object) {
        if (object.getClass() == Task.class) {
            Task obj = (Task) object;
            tasks.get(obj.numberID).description = obj.description;
            tasks.get(obj.numberID).status = obj.status;
            tasks.get(obj.numberID).name = obj.name;
        } else if (object.getClass() == Epic.class) {
            Epic obj = (Epic) object;
            epics.get(obj.numberID).description = obj.description;
            epics.get(obj.numberID).name = obj.name;
            epics.get(obj.numberID).epicSubtasks = obj.epicSubtasks;
            epics.get(obj.numberID).status = calculateStatus(epics.get(obj.numberID));
        } else if (object.getClass() == Subtask.class) {
            Subtask obj = (Subtask) object;
            subtasks.get(obj.numberID).description = obj.description;
            subtasks.get(obj.numberID).status = obj.status;
            subtasks.get(obj.numberID).name = obj.name;
            subtasks.get(obj.numberID).epicID = obj.epicID;
            epics.get(obj.epicID).status = calculateStatus(epics.get(obj.epicID));
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
