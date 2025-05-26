package fcu.edu.check_in.model;

import java.util.List;

public class Person {
    private String nickName, bio;
    private final String email;

    private List<String> followPersonEmail;

    private List<MyTask> myTasks;

    public Person(String nickName, String bio, String email, List<String> followPersonEmail, List<MyTask> myTasks){
        this.nickName = nickName;
        this.bio = bio;
        this.email = email;
        this.followPersonEmail = followPersonEmail;
        this.myTasks = myTasks;
    }

    public String getNickName(){ return this.nickName;};
    public String getBio() { return bio; }
    public String getEmail() {return email;}
    public void setBio(String bio) { this.bio = bio; }
    public void setNickName(String nickName) { this.nickName = nickName; }
    public void addFollow(String followEmail){ this.followPersonEmail.add(followEmail); }
    public void addTask(MyTask newTask){ this.myTasks.add(newTask); }
}
