package fcu.edu.check_in.model;

public class FollowedTask {
    private String taskID;
    private String title;
    private String initiator;

    public FollowedTask() {
        // 空建構子（必要於Firebase反序列化）
    }

    public FollowedTask(String taskID, String title, String initiator) {
        this.taskID = taskID;
        this.title = title;
        this.initiator = initiator;
    }

    public String getTaskID() {
        return taskID;
    }

    public void setTaskID(String taskID) {
        this.taskID = taskID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getInitiator() {
        return initiator;
    }

    public void setInitiator(String initiator) {
        this.initiator = initiator;
    }
}
