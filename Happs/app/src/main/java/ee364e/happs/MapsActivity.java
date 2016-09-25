package ee364e.happs;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private double longitude;
    private double latitude;
    private String result;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        Intent intent = getIntent();
        result = intent.getStringExtra("data");
        longitude = intent.getDoubleExtra("long", -101);
        latitude = intent.getDoubleExtra("lat", 123);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        try {
            JSONArray jObject = new JSONArray(result);
            for(int i = 0 ; i < jObject.length(); i++) {
                JSONObject object = jObject.getJSONObject(i);
                Event event = new Event(object);
                double latitude1 = event.getLongitude();
                double longitude1 = event.getLatitude();
                LatLng location = new LatLng(longitude1,latitude1);
                mMap.addMarker(new MarkerOptions().position(location).title(event.getName()));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        LatLng here = new LatLng(latitude, longitude);
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(here).title("Marker on me~ :D"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(here, 15));
    }

    public void startCreateEventActivity(View view){
        Intent intent = new Intent(this, DefineEvent.class);
        intent.putExtra("long", longitude);
        intent.putExtra("lat", latitude);
        startActivity(intent);
    }

}




































