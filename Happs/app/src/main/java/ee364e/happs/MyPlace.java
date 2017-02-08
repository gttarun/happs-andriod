package ee364e.happs;

import com.google.android.gms.location.places.Place;

/**
 * Created by Young on 2016-11-19.
 */

public class MyPlace {

    String name = null;
    String address = null;
    double longitude;
    double latitude;
    boolean selected = false;
    Place googlePlace;

    public MyPlace(Place place) {
        super();
        googlePlace = place;
        this.name = place.getName().toString();
        this.address = place.getAddress().toString();
        this.longitude = place.getLatLng().longitude;
        this.latitude = place.getLatLng().latitude;
    }

    public String getAddress() {
        return address;
    }
    public String getName() {
        return name;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public Place getGooglePlace() {
        return googlePlace;
    }
    public boolean isSelected() {
        return selected;
    }
    public void setSelected(boolean selected) {
        this.selected = selected;
    }

}
