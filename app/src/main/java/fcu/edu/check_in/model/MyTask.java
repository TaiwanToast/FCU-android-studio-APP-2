package fcu.edu.check_in.model;

public class MyTask {
    private final String title;
    private final String initiator;

    public MyTask(String title, String initiator) {
        this.title = title;
        this.initiator = initiator;
    }

    public String getTitle() {
        return title;
    }

    public String getInitiator() {
        return initiator;
    }
}
