package manager;

import tasks.Task;

public class Node {
    public Node next;
    public Node previous;
    public Task task;

    public Node(Node next, Node previous, Task task) {
        this.next = next;
        this.previous = previous;
        this.task = task;
    }
}
