package ee364e.happs;

import org.json.JSONException;
import org.json.JSONObject;

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

    private double latitude;
    private String name;
    private String time;

    public Event (JSONObject object) throws JSONException {
        //longitude = Double.parseDouble(object.getString("longitude"));
        //latitude = Double.parseDouble(object.getString("latitude"));
        longitude = 54;
        latitude = 62;
        name = object.getString("event_name");
        time = object.getString("time");
    }
}
