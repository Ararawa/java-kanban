import manager.TaskManager;
import support.TaskStatus;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");
        Task task1 = new Task("name1", TaskManager.generateNumber(), "description1", TaskStatus.NEW);
        TaskManager.create(task1);
        Task task2 = new Task("name2", TaskManager.generateNumber(), "description1", TaskStatus.NEW);
        TaskManager.create(task2);
        Epic epic1 = new Epic("name3", TaskManager.generateNumber(), "description1", TaskStatus.NEW);
        TaskManager.create(epic1);
        System.out.println(TaskManager.getByID(3));
        Subtask subtask1 = new Subtask("name4", TaskManager.generateNumber(), "description1", TaskStatus.DONE, epic1.getID());
        TaskManager.create(subtask1);
        Epic epic2 = new Epic("name5", TaskManager.generateNumber(), "description1", TaskStatus.NEW);
        TaskManager.create(epic2);
        Subtask subtask2 = new Subtask("name6", TaskManager.generateNumber(), "description1", TaskStatus.IN_PROGRESS, epic2.getID());
        TaskManager.create(subtask2);
        Subtask subtask3 = new Subtask("name7", TaskManager.generateNumber(), "description1", TaskStatus.NEW, epic2.getID());
        TaskManager.create(subtask3);
        System.out.println("---");
        ArrayList<Task> arrayList = TaskManager.returnAllTasks();
        for (int i = 0; i < arrayList.size(); i++) {
            System.out.println(arrayList.get(i).toString());
        }
        System.out.println("---");
        System.out.println(TaskManager.getByID(5));
        System.out.println("---");
        Subtask subtask4 = new Subtask("n7", subtask2.id, "destion1", TaskStatus.DONE, epic2.getID());
        TaskManager.update(subtask4);
        System.out.println(TaskManager.getByID(5));
        TaskManager.deleteByID(5);
        System.out.println("---");
        ArrayList<Task> arrayList2 = TaskManager.returnAllTasks();
        for (int i = 0; i < arrayList2.size(); i++) {
            System.out.println(arrayList2.get(i).toString());
        }
        System.out.println("---");
        System.out.println(TaskManager.getSubtasksByEpicID(3));
        TaskManager.deleteAllTasks();
        ArrayList<Task> arrayList3 = TaskManager.returnAllTasks();
        for (int i = 0; i < arrayList3.size(); i++) {
            System.out.println(arrayList3.get(i).toString());
        }
        System.out.println("---");
    }
}
