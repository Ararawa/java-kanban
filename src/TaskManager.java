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
        if (done == epic.epicSubtasks.size()) {
            return TaskStatus.DONE;
        } else if (news == epic.epicSubtasks.size()) {
            return TaskStatus.NEW;
        } else {
            return TaskStatus.IN_PROGRESS;
        }
    }

    //Получение списка всех задач.
    public static void printAllTasks() {
        for (int i : tasks.keySet()) {
            System.out.println(tasks.get(i).toString());
        }
        for (int i : epics.keySet()) {
            System.out.println(epics.get(i).toString());
            for (int j : subtasks.keySet()) {
                if (subtasks.get(j).epicID == epics.get(i).getNumberID()) {
                    System.out.println(subtasks.get(j).toString());
                }
            }
        }
    }

    //Удаление всех задач.
    public static void deleteAllTasks() {
        tasks.clear();
        epics.clear();
        subtasks.clear();
    }

    //Получение по идентификатору.
    public static void getByID(int numberID) {
        for (int i : tasks.keySet()) {
            if (i == numberID) {
                System.out.println(tasks.get(i).toString());
            }
        }
        for (int i : epics.keySet()) {
            if (i == numberID) {
            System.out.println(epics.get(i).toString());
                for (int j : subtasks.keySet()) {
                    if (subtasks.get(j).epicID == epics.get(i).getNumberID()) {
                        System.out.println(subtasks.get(j).toString());
                    }
                }
            }
        }
    }

    //Создание. Сам объект должен передаваться в качестве параметра.
    static void create(Object object) {
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

    //Обновление. Новая версия объекта с верным идентификатором передаётся в виде параметра.
    static void update(Object object) {
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

    //Удаление по идентификатору.
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

    //Получение списка всех подзадач определённого эпика.
    public static void getSubtasksByEpicID(int numberID) {
        for (int i : epics.keySet()) {
            if (i == numberID) {
                System.out.println(epics.get(i).toString());
                for (int j : subtasks.keySet()) {
                    if (subtasks.get(j).epicID == epics.get(i).getNumberID()) {
                        System.out.println(subtasks.get(j).toString());
                    }
                }
            }
        }
    }
}
