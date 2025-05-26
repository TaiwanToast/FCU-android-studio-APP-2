package fcu.edu.check_in.model;

import java.util.List;
import java.util.Map;

public class Person {

    private String nickName, bio;
    private final String email;

    private List<String> followPersonEmail;
    private Map<String, Map<String, String>> followingTaskID;

    public Person(String nickName, String bio, String email, List<String> followPersonEmail, Map<String, Map<String, String>> followingTaskID){
        this.nickName = nickName;
        this.bio = bio;
        this.email = email;
        this.followPersonEmail = followPersonEmail;
        this.followingTaskID = followingTaskID;
    }

    public String getNickName(){ return this.nickName;};
    public String getBio() { return bio; }
    public String getEmail() {return email;}

    public Map<String, Map<String, String>> getfollowingTaskID(){
        return followingTaskID;
    }

    public List<String> getFollowPersonEmail() {
        return followPersonEmail;
    }

    public void setBio(String bio) { this.bio = bio; }
    public void setNickName(String nickName) { this.nickName = nickName; }
    public void addFollow(String followEmail){ this.followPersonEmail.add(followEmail); }
}
