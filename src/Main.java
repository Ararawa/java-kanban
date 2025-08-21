public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");
        Task task1 = new Task("name1", TaskManager.generateNumber(), "description1", TaskStatus.NEW);
        TaskManager.create(task1);
        Task task2 = new Task("name2", TaskManager.generateNumber(), "description1", TaskStatus.NEW);
        TaskManager.create(task2);
        Epic epic1 = new Epic("name3", TaskManager.generateNumber(), "description1", TaskStatus.NEW);
        TaskManager.create(epic1);
        Subtask subtask1 = new Subtask("name4", TaskManager.generateNumber(), "description1", TaskStatus.DONE, epic1.getNumberID());
        TaskManager.create(subtask1);
        Epic epic2 = new Epic("name5", TaskManager.generateNumber(), "description1", TaskStatus.NEW);
        TaskManager.create(epic2);
        Subtask subtask2 = new Subtask("name6", TaskManager.generateNumber(), "description1", TaskStatus.IN_PROGRESS, epic2.getNumberID());
        TaskManager.create(subtask2);
        Subtask subtask3 = new Subtask("name7", TaskManager.generateNumber(), "description1", TaskStatus.NEW, epic2.getNumberID());
        TaskManager.create(subtask3);
        System.out.println("---");
        TaskManager.printAllTasks();
        System.out.println("---");
        TaskManager.getByID(5);
        System.out.println("---");
        Subtask subtask4 = new Subtask("n7", subtask2.numberID, "destion1", TaskStatus.DONE, epic2.getNumberID());
        TaskManager.update(subtask4);
        TaskManager.getByID(5);
        TaskManager.deleteByID(5);
        System.out.println("---");
        TaskManager.printAllTasks();
        System.out.println("---");
        TaskManager.getSubtasksByEpicID(3);
        TaskManager.deleteAllTasks();
        TaskManager.printAllTasks();
        System.out.println("---");
    }
}
