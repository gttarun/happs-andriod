package ee364e.happs;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class EventLayoutActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private final String URL = "http://teamhapps.herokuapp.com/api/events/";
    List<Event> events = new ArrayList<Event>();
    JsonArray result;
    private double longitude;
    private double latitude;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final EventLayoutActivity eventlayout = this;
        context = this;
        Intent intent = getIntent();
        result = EventBus.getDefault().removeStickyEvent(JsonArray.class);
        longitude = intent.getDoubleExtra("longitude", -101);
        latitude = intent.getDoubleExtra("latitude", 123);
        try {
            for(int i = 0 ; i < result.size(); i++) {
                JsonElement object = result.get(i);
                Event event = new Event(object);
                events.add(event);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        setContentView(R.layout.activity_event_layout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        LinearLayoutManager lim = new LinearLayoutManager(getApplicationContext());
        RecyclerView rv = (RecyclerView)findViewById(R.id.rv);
        rv.setLayoutManager(lim);
        rv.setHasFixedSize(true);
        rv.setNestedScrollingEnabled(false);
        RVAdapter adapter = new RVAdapter(events, new RVAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Event event) {
                Intent intent = new Intent(getApplicationContext(), EventActivity.class);
                EventBus.getDefault().postSticky(event);
                startActivity(intent);
            }
        }, this);
        rv.setAdapter(adapter);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(eventlayout, MapsActivity.class);
                EventBus.getDefault().postSticky(result);
                startActivity(intent);
            }
        });

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

         else if (id == R.id.nav_friends){
            // Toast.makeText(getApplicationContext(), "you pressed friends", Toast.LENGTH_SHORT).show();
             Intent intent = new Intent(this, FriendsListActivity.class);
             startActivity(intent);
         }

         else if (id == R.id.nav_profile){
             Intent intent = new Intent(this, MainUserProfileActivity.class);
             startActivity(intent);
         }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
        refresh();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.add) {
            Intent intent = new Intent(this, CameraActivity.class);
            intent.putExtra("longitude", longitude);
            intent.putExtra("latitude", latitude);
            startActivity(intent);
        }

        else if (id == R.id.refresh) {
            refresh();
        }

        return super.onOptionsItemSelected(item);
    }

    public void refresh() {

        Ion.with(context)
                .load(URL)
                .asJsonArray()
                .setCallback(new FutureCallback<JsonArray>() {
                    @Override
                    public void onCompleted(Exception e, JsonArray results) {
                        result = results;
                        events = new ArrayList<Event>();
                            for(int i = 0 ; i < result.size(); i++) {
                                JsonElement object = result.get(i);
                                try {
                                    Event event = new Event(object);
                                    events.add(event);
                                } catch (JSONException e1) {
                                    e1.printStackTrace();
                                }
                            }
                        RecyclerView rv = (RecyclerView)findViewById(R.id.rv);
                        RVAdapter adapter = new RVAdapter(events, new RVAdapter.OnItemClickListener() {
                            @Override
                            public void onItemClick(Event event) {
                                Intent intent = new Intent(getApplicationContext(), EventActivity.class);
                                EventBus.getDefault().postSticky(event);
                                startActivity(intent);
                            }
                        }, context);
                        rv.setAdapter(adapter);

                    }
                });

    }
}
