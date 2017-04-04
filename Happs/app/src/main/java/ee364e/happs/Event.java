package ee364e.happs;

import org.json.JSONException;
import org.json.JSONObject;

import android.net.Uri;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

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

    public String getId() {
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

    public String getCover() {
        return cover;
    }

    public String getDate() {
        return date;
    }
    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    private boolean publicEvent;
    private boolean invites;

    private double longitude;
    private double latitude;
    private String name;
    private String username;
    private String startTime;
    private String endTime;
    private String address;
    private String placeName;
    private String details;
    private String cover;
    private String date;
    private MyGooglePlaces place;
    private int startYear;
    private int startMonth;
    private int startDay;
    private int startHour;
    private int startMinute;
    private int endHour;
    private int endMinute;
    private Uri uri;
    private String id;


    public Event() {
    };

    public Event (JSONObject object) throws JSONException {
        id = object.getString("url");
        date = object.getString("date");
        longitude = Double.parseDouble(object.getString("longitude"));
        latitude = Double.parseDouble(object.getString("latitude"));
        name = object.getString("event_name");
        startTime = object.getString("start_time");
        endTime = object.getString("end_time");
        address = object.getString("address");
        details = object.getString("description");
        String host = object.getString("host");
        int index = host.lastIndexOf("/", host.length() - 1);
        username = host.substring(index + 1).replace("/", "");
        cover = object.getString("picture");
        publicEvent = !object.getBoolean("private");
        placeName = object.getString("place_name");
        invites = object.getBoolean("invites_enabled");
    }

    public Event (JsonElement object) throws JSONException {
        try {
            JsonObject event = object.getAsJsonObject();
            id = event.get("url").getAsString();
            longitude = event.get("longitude").getAsDouble();
            latitude = event.get("latitude").getAsDouble();
            date = event.get("date").getAsString();
            name = event.get("event_name").getAsString();
            startTime = event.get("start_time").getAsString();
            endTime = event.get("end_time").getAsString();
            address = event.get("address").getAsString();
            details = event.get("description").getAsString();
            if (details == null) {
                details = "";
            }
            String host = event.get("host").getAsString();
            int index = host.lastIndexOf("/", host.length() - 2);
            username = host.substring(index + 1).replace("/", "");
            cover = event.get("picture").getAsString();
            placeName = event.get("place_name").getAsString();
            publicEvent = !event.get("private").getAsBoolean();
            invites = event.get("invites_enabled").getAsBoolean();
        } catch (UnsupportedOperationException exception) {
        }
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

    public void setPlace(MyGooglePlaces place) {
        this.place = place;
        this.longitude = place.getLongitude();
        this.latitude = place.getLatitude();
        this.placeName = place.getName();
        this.address = place.getAddress();
    }


}
