package fcu.edu.check_in.model;

public class MyTask {
    private final String title;
    private final String initiator;
    private final String taskID;

    public MyTask(String title, String initiator, String taskID) {
        this.title = title;
        this.initiator = initiator;
        this.taskID = taskID;
    }

    public String getTitle() {
        return title;
    }

    public String getInitiator() {
        return initiator;
    }

    public String getTaskID() {
        return taskID;
    }
}
