package ee364e.happs;

import org.json.JSONException;
import org.json.JSONObject;

import android.net.Uri;

/**
 * Created by cykim on 2016-09-19.
 */

public class Event {



    private double longitude;

    public double getLongitude() {
        return longitude;
    }


    public double getLatitude() {
        return latitude;
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

    private double latitude;
    private String name;
    private String time;
    private String address;
    private String placeName;
    private String details;
    private Uri uri;

    public Event() {
    };

    public Event (JSONObject object) throws JSONException {
        //longitude = Double.parseDouble(object.getString("longitude"));
        //latitude = Double.parseDouble(object.getString("latitude"));
        longitude = 54;
        latitude = 62;
        name = object.getString("event_name");
        time = object.getString("time");
    }
}
