import manager.HistoryManager;
import manager.InMemoryTaskManager;
import manager.Managers;
import manager.TaskManager;
import tasks.TaskStatus;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
        TaskManager manager = Managers.getDefault();
        HistoryManager managerH = Managers.getDefaultHistory();
        System.out.println("Поехали!");
        Task task1 = new Task("name1", "description1", TaskStatus.NEW);
        manager.create(task1);
        Task task2 = new Task("name2", "description1", TaskStatus.NEW);
        manager.create(task2);
        Epic epic1 = new Epic("name3", "description1", TaskStatus.NEW);
        manager.create(epic1);
        System.out.println(manager.getByID(3)); //1
        Subtask subtask1 = new Subtask("name4", "description1", TaskStatus.DONE, 3);
        manager.create(subtask1);
        Epic epic2 = new Epic("name5", "description1", TaskStatus.NEW);
        manager.create(epic2);
        Subtask subtask2 = new Subtask("name6", "description1", TaskStatus.IN_PROGRESS, 5);
        manager.create(subtask2);
        Subtask subtask3 = new Subtask("name7", "description1", TaskStatus.NEW, 5);
        manager.create(subtask3);
        System.out.println("---");
        ArrayList<Task> arrayList = manager.returnAllTasks();
        for (int i = 0; i < arrayList.size(); i++) {
            System.out.println(arrayList.get(i).toString());
        }
        System.out.println("---");
        System.out.println(manager.getByID(5)); //2
        System.out.println("---");
        Subtask subtask4 = new Subtask("n7", "destion1", TaskStatus.DONE, 5);
        subtask4.setId(6);
        manager.update(subtask4);
        System.out.println(manager.getByID(5)); //3
        manager.deleteByID(5);
        System.out.println("---");
        ArrayList<Task> arrayList2 = manager.returnAllTasks();
        for (int i = 0; i < arrayList2.size(); i++) {
            System.out.println(arrayList2.get(i).toString());
        }
        System.out.println("---");
        System.out.println(manager.getSubtasksByEpicID(3));
        manager.deleteAllTasks();
        System.out.println("---");
        ArrayList<Task> arrayList3 = manager.returnAllTasks();
        for (int i = 0; i < arrayList3.size(); i++) {
            System.out.println(arrayList3.get(i).toString());
        }
        System.out.println("---");
        System.out.println("---");
        System.out.println("---");
        manager.getByID(1); //1
        manager.getByID(2); //2
        manager.getByID(3); //3
        manager.getByID(2); //4
        manager.getByID(3); //5
        manager.getByID(1); //6
        ArrayList<Task> history1 = managerH.getHistory();
        for (Task task : history1) {
            System.out.println(task);
        }
        System.out.println("---");
        manager.getByID(1); //7
        manager.getByID(1); //8
        manager.getByID(1); //9
        manager.getByID(1); //10
        manager.getByID(1); //11
        manager.getByID(1); //12
        ArrayList<Task> history2 = managerH.getHistory();
        for (Task task : history2) {
            System.out.println(task);
        }
    }
}
