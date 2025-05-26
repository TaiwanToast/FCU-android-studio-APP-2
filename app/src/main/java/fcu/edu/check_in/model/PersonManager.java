package fcu.edu.check_in.model;

public class PersonManager {
    private static PersonManager instance;
    private Person currentPerson;

    private PersonManager(){}

    public static synchronized PersonManager getInstance(){
        if (instance == null) {
            instance = new PersonManager();
        }
        return instance;
    }

    public Person getCurrentPerson() {
        return currentPerson;
    }

    public void setCurrentPerson(Person currentPerson) {
        this.currentPerson = currentPerson;
    }
}
