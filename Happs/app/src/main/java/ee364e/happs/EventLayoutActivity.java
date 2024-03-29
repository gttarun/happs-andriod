package ee364e.happs;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
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
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.facebook.login.LoginManager;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class EventLayoutActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private final String URL = "https://uthapps-backend.herokuapp.com/api/events/";
    List<Event> events = new ArrayList<Event>();
    List<String> invitations = new ArrayList();
    JsonArray result;
    String invite;
    Context context;
    String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        username = preferences.getString("username", "");
        super.onCreate(savedInstanceState);
        final EventLayoutActivity eventlayout = this;
        context = this;
        setContentView(R.layout.activity_event_layout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        LinearLayoutManager lim = new LinearLayoutManager(getApplicationContext());
        RecyclerView rv = (RecyclerView) findViewById(R.id.rv);
        rv.setLayoutManager(lim);
        rv.setHasFixedSize(true);
        rv.setNestedScrollingEnabled(false);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(eventlayout, MapsActivity.class);
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
        View hView = navigationView.getHeaderView(0);
        setDrawer(hView);
        Intent intent = getIntent();
        if (intent != null) {
            invite = intent.getStringExtra("event_id");
        }
    }

    void setDrawer(View hView) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String uName = preferences.getString("username", "");
        String Name = preferences.getString("name", "");
        String picture = preferences.getString("picture", "");
        ImageView photo = (ImageView) hView.findViewById(R.id.nav_header_profile_image);
        TextView username = (TextView) hView.findViewById(R.id.nav_header_username);
        TextView name = (TextView) hView.findViewById(R.id.nav_header_userid);
        username.setText(uName);
        name.setText(Name);
        Glide.with(context).load(picture).centerCrop().placeholder(R.drawable.happs).crossFade().into(photo);
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
         if (id == R.id.nav_logout) {
             SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
             String username = preferences.getString("username", "");
             SharedPreferences.Editor editor = preferences.edit();
             editor.putString("user_id", "");
             editor.putString("authentication_token", "");
             editor.putString("picture", "");
             editor.putString("username", "");
             editor.putString("name", "");
             editor.apply();
             LoginManager.getInstance().logOut();
             FirebaseMessaging.getInstance().unsubscribeFromTopic(username);
             Intent intent = new Intent(this, LoginActivity.class);
             intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
             startActivity(intent);
             finish();
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


         else if (id == R.id.nav_events){
             Intent intent = new Intent(this, MyEventsActivity.class);
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
            startActivity(intent);
        }

        else if (id == R.id.refresh) {
            refresh();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        setIntent(intent);
        invite = intent.getStringExtra("event_id");

    }

    public void refresh() {
        invitations.clear();
        String URLInvites = "https://uthapps-backend.herokuapp.com/api/invitation/?username=" + username;
        Ion.with(context)
                .load(URLInvites)
                .asJsonArray()
                .setCallback(new FutureCallback<JsonArray>() {
                    @Override
                    public void onCompleted(Exception e, JsonArray results) {
                        if(results == null) {
                            refreshP2();
                        } else if (results.size() ==0) {
                            refreshP2();
                        } else {
                            for(int i = 0; i < results.size(); i++) {
                                invitations.add(results.get(i).getAsJsonObject().get("event_id").getAsString());
                            }
                            refreshP2();
                        }
                    }
                });

    }

    public void refreshP2() {
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
                                if(event.isPublicEvent()) {
                                    events.add(event);
                                } else {
                                    String id = event.getId();
                                    int index = id.lastIndexOf("/", id.length() - 2 );
                                    String idNumber =id.substring(index +1);
                                    idNumber = idNumber.replace("/" , "");
                                    if(invitations.contains(idNumber) || username.equals(event.getUsername())) {
                                        events.add(event);
                                    }
                                }
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

                        if(invite != null) {
                            for(Event event : events){
                                if(event.getId().equals(invite)){
                                    Intent newIntent = new Intent(getApplicationContext(), EventActivity.class);
                                    EventBus.getDefault().postSticky(event);
                                    startActivity(newIntent);
                                    invite = null;
                                    break;
                                }
                            }
                        }
                    }
                });
    }
}
