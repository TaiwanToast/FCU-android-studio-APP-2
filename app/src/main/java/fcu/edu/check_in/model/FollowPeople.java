package fcu.edu.check_in.model;

// 可以用Person取代
public class FollowPeople {
    private final String followName;
    public FollowPeople(String followName) {
        this.followName = followName;
    }

    public String getName() {
        return followName;
    }

}
