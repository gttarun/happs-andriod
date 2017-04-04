package ee364e.happs;

import android.graphics.Picture;
import android.media.Image;
import android.net.Uri;
import android.net.wifi.ScanResult;

import java.util.ArrayList;
import java.util.PriorityQueue;

/**
 * Created by Ali on 2/13/2017.
 */

public class Profile {

    private String name;
    private String userName;
    private String description;
    private String userID;
    private String authToken;
    private ArrayList<String> friends;
    private ArrayList<Event> events;
    private String profilePic;
    private String status;

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public ArrayList<Event> getEvents() {return events;}

    public void setEvents(ArrayList<Event> events) {this.events = events;}

    public ArrayList<String> getFriends() {
        return friends;
    }

    public void setFriends(ArrayList<String> friends) {
        this.friends = friends;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }



}
