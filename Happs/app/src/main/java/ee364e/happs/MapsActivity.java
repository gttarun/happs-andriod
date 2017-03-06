package ee364e.happs;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.support.design.widget.FloatingActionButton;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;

import java.util.ArrayList;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, NavigationView.OnNavigationItemSelectedListener,
        GoogleMap.OnInfoWindowClickListener {


    class MyInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

        private final View myContentsView;


        MyInfoWindowAdapter(){
            myContentsView = getLayoutInflater().inflate(R.layout.content_map_marker, null);
        }

        @Override
        public View getInfoContents(Marker marker) {
            int id = Integer.parseInt(marker.getTitle());
            Event mapEvent = null;
            for(Event event: events) {
                if(event.getId() == id) {
                    mapEvent = event;
                    break;
                }
            }
            TextView eventName = ((TextView)myContentsView.findViewById(R.id.event_name));
            TextView eventUserName = ((TextView)myContentsView.findViewById(R.id.event_username));
            ImageView eventPhoto = (ImageView) myContentsView.findViewById(R.id.event_photo);
            eventName.setText(mapEvent.getName());
            eventUserName.setText(mapEvent.getUsername());
            Glide.with(context).load("https://pbs.twimg.com/profile_images/447374371917922304/P4BzupWu.jpeg").centerCrop().placeholder(R.drawable.happs).crossFade().into(eventPhoto);
            //Glide.with(context).load(mapEvent.getURI()).centerCrop().placeholder(R.drawable.happs).crossFade().into(eventPhoto);


            return myContentsView;
        }

        @Override
        public View getInfoWindow(Marker marker) {
            // TODO Auto-generated method stub
            return null;
        }



    }


    @Override
    public void onInfoWindowClick(Marker marker) {
        int id = Integer.parseInt(marker.getTitle());
        Event clickedEvent = null;
        for(Event event: events) {
            if(event.getId() == id) {
                clickedEvent = event;
                break;
            }
        }
        Intent intent = new Intent(this, EventActivity.class);
        EventBus.getDefault().postSticky(clickedEvent);
        startActivity(intent);
    }






    private GoogleMap mMap;
    Location mLastLocation;
    private final String URL = "http://teamhapps.herokuapp.com/api/events/";
    private JsonArray result;
    private GoogleApiClient mGoogleApiClient;
    ArrayList<Event> events;
    Context context;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
        final MapsActivity maplayout = this;
        setContentView(R.layout.activity_event_map);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        context = this;
        Intent intent = getIntent();
         result = EventBus.getDefault().removeStickyEvent(JsonArray.class);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab2);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(maplayout, EventLayoutActivity.class);
                startActivity(intent);
            }
        });

        events = new ArrayList<Event>();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_manage) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
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
        mMap.setInfoWindowAdapter(new MyInfoWindowAdapter());
        mMap.setOnInfoWindowClickListener(this);
        mMap.setPadding(0,200,0,230);
        mMap.setMyLocationEnabled(true);
        try {
            for(int i = 0 ; i < result.size(); i++) {
                JsonElement object = result.get(i);
                Event event = new Event(object);
                events.add(event);
                double latitude1 = event.getLongitude();
                double longitude1 = event.getLatitude();
                LatLng location = new LatLng(longitude1,latitude1);
                mMap.addMarker(new MarkerOptions().position(location).title(String.valueOf(event.getId())));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        updateLocation();
    }



    @Override
    public void onConnectionSuspended(int i) {
        return;
    }


    @Override
    public void onConnectionFailed(ConnectionResult result) {
        return;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.add) {
            updateLocation();
            Intent intent = new Intent(this, CameraActivity.class);
            intent.putExtra("longitude", mLastLocation.getLongitude());
            intent.putExtra("latitude", mLastLocation.getLatitude());
            startActivity(intent);
        }

        else if (id == R.id.refresh) {
            refresh();
        }

        return super.onOptionsItemSelected(item);
    }

    public void refresh() {
        updateLocation();
        Ion.with(context)
                .load(URL)
                .asJsonArray()
                .setCallback(new FutureCallback<JsonArray>() {
                    @Override
                    public void onCompleted(Exception e, JsonArray results) {
                        mMap.clear();
                        for(int i = 0 ; i < result.size(); i++) {
                            JsonElement object = result.get(i);
                            try {
                                Event event = new Event(object);
                                events.add(event);
                                double latitude1 = event.getLongitude();
                                double longitude1 = event.getLatitude();
                                LatLng location = new LatLng(longitude1,latitude1);
                                mMap.addMarker(new MarkerOptions().position(location).title(String.valueOf(event.getId())));
                            } catch (JSONException e1) {
                                e1.printStackTrace();
                            }
                        }
                    }
                });
    }



    public void updateLocation() {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()), 15));
    }
}




































