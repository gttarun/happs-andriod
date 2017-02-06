package ee364e.happs;

import org.json.JSONException;
import org.json.JSONObject;

import android.net.Uri;

import com.google.android.gms.location.places.Place;

/**
 * Created by cykim on 2016-09-19.
 */

public class Event {

    public double getLongitude() {
        return longitude;
    }
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTime() {
        return time;
    }


    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddress() {
        return address;
    }

    public void setURI(Uri uri) {
        this.uri = uri;
    }

    public Uri getURI() {
        return this.uri;
    }
    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setdetails(String details) {
        this.details = details;
    }

    public String getdetails() {
        return details;
    }

    public int getId() {
        return this.id;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }


    public boolean isPublicEvent() {
        return publicEvent;
    }

    public boolean isInvites() {
        return invites;
    }

    public int getEndMinute() {
        return endMinute;
    }

    public int getEndHour() {
        return endHour;
    }

    public int getStartMinute() {
        return startMinute;
    }

    public int getStartHour() {
        return startHour;
    }

    public int getStartMonth() {
        return startMonth;
    }

    public int getStartYear() {
        return startYear;
    }

    public int getStartDay() {
        return startDay;
    }

    private boolean publicEvent;
    private boolean invites;

    private double longitude;
    private double latitude;
    private String name;
    private String username;
    private String time;
    private String address;
    private String placeName;
    private String details;
    private MyPlace place;
    private int startYear;
    private int startMonth;
    private int startDay;
    private int startHour;
    private int startMinute;
    private int endHour;
    private int endMinute;
    private Uri uri;
    private int id;


    public Event() {
    };

    public Event (JSONObject object) throws JSONException {
        id = object.getInt("id");
        longitude = Double.parseDouble(object.getString("longitude"));
        latitude = Double.parseDouble(object.getString("latitude"));
        name = object.getString("event_name");
        time = object.getString("time");
        username = object.getString("username");
    }

    public void setDate(int year, int month, int day, int hour, int minute, int hour2, int minute2) {
        startYear = year;
        startMonth = month;
        startDay = day;
        startHour = hour;
        startMinute = minute;
        endHour = hour2;
        endMinute = minute2;
    }

    public void setPublic(boolean bool) {
        publicEvent = bool;
    }

    public void setInvites(boolean bool) {invites = bool;}

    public void setPlace(MyPlace place) {
        this.place = place;
        this.longitude = place.getLongitude();
        this.latitude = place.getLatitude();
        this.placeName = place.getName();
        this.address = place.getAddress();
    }


}
