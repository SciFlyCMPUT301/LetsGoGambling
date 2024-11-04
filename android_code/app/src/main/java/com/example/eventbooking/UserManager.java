package com.example.eventbooking;
/**
 * this is the custom UserManager class
 * its just for adjust the User's role
 * be careful when import, import this one follow the path
 * import the android one use Android.os.UserManager
 */
public class UserManager {
    private static UserManager instance;
    private User currentUser;
    private UserManager(){

    }
    public static synchronized UserManager getInstance(){
        if(instance == null){
            instance = new UserManager();
        }
        return instance;
    }
    public void setCurrentUser(User user){
        this.currentUser= user;

    }
    public User getCurrentUser(){
        return currentUser;
    }

}
