import manager.InMemoryTaskManager;
import tasks.TaskStatus;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
        InMemoryTaskManager InMemoryTaskManager = new InMemoryTaskManager();
        System.out.println("Поехали!");
        Task task1 = new Task("name1", InMemoryTaskManager.generateNumber(), "description1", TaskStatus.NEW);
        InMemoryTaskManager.create(task1);
        Task task2 = new Task("name2", InMemoryTaskManager.generateNumber(), "description1", TaskStatus.NEW);
        InMemoryTaskManager.create(task2);
        Epic epic1 = new Epic("name3", InMemoryTaskManager.generateNumber(), "description1", TaskStatus.NEW);
        InMemoryTaskManager.create(epic1);
        System.out.println(InMemoryTaskManager.getByID(3));
        Subtask subtask1 = new Subtask("name4", InMemoryTaskManager.generateNumber(), "description1", TaskStatus.DONE, epic1.getID());
        InMemoryTaskManager.create(subtask1);
        Epic epic2 = new Epic("name5", InMemoryTaskManager.generateNumber(), "description1", TaskStatus.NEW);
        InMemoryTaskManager.create(epic2);
        Subtask subtask2 = new Subtask("name6", InMemoryTaskManager.generateNumber(), "description1", TaskStatus.IN_PROGRESS, epic2.getID());
        InMemoryTaskManager.create(subtask2);
        Subtask subtask3 = new Subtask("name7", InMemoryTaskManager.generateNumber(), "description1", TaskStatus.NEW, epic2.getID());
        InMemoryTaskManager.create(subtask3);
        System.out.println("---");
        ArrayList<Task> arrayList = InMemoryTaskManager.returnAllTasks();
        for (int i = 0; i < arrayList.size(); i++) {
            System.out.println(arrayList.get(i).toString());
        }
        System.out.println("---");
        System.out.println(InMemoryTaskManager.getByID(5));
        System.out.println("---");
        Subtask subtask4 = new Subtask("n7", subtask2.id, "destion1", TaskStatus.DONE, epic2.getID());
        InMemoryTaskManager.update(subtask4);
        System.out.println(InMemoryTaskManager.getByID(5));
        InMemoryTaskManager.deleteByID(5);
        System.out.println("---");
        ArrayList<Task> arrayList2 = InMemoryTaskManager.returnAllTasks();
        for (int i = 0; i < arrayList2.size(); i++) {
            System.out.println(arrayList2.get(i).toString());
        }
        System.out.println("---");
        System.out.println(InMemoryTaskManager.getSubtasksByEpicID(3));
        InMemoryTaskManager.deleteAllTasks();
        ArrayList<Task> arrayList3 = InMemoryTaskManager.returnAllTasks();
        for (int i = 0; i < arrayList3.size(); i++) {
            System.out.println(arrayList3.get(i).toString());
        }
        System.out.println("---");
    }
}
