package fcu.edu.check_in.model;

public class CheckTask {
    private String taskId;
    private String startTime;
    private String week;

    public CheckTask(String taskId, String startTime, String week) {
        this.taskId = taskId;
        this.startTime = startTime;
        this.week = week;
    }

    public String getTaskId() {
        return taskId;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getWeek() {
        return week;
    }
}
