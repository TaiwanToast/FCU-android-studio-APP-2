package fcu.edu.check_in.adapter;

import java.util.List;
import java.util.Map;

import fcu.edu.check_in.model.Person;

public class PersonToFirebaseAdapter {
    String nickName, bio;
    List<String> followPersinEmail;
    Map<String, Map<String, String>> followingTaskID;

    public PersonToFirebaseAdapter(Person person){
        nickName = person.getNickName();
        followPersinEmail = person.getFollowPersonEmail();
        bio = person.getBio();
        followingTaskID = person.getfollowingTaskID();
    }
}
