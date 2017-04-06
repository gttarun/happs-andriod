package ee364e.happs;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class MyEventsActivity extends AppCompatActivity {

    List<Event> events = new ArrayList<Event>();
    ArrayList<Integer> eventIDs = new ArrayList<Integer>();
    Context context;
    String username;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_events);
        context = this;
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        username = preferences.getString("username", "");
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        LinearLayoutManager lim = new LinearLayoutManager(getApplicationContext());
        RecyclerView rv = (RecyclerView) findViewById(R.id.rv);
        rv.setLayoutManager(lim);
        rv.setHasFixedSize(true);
        rv.setNestedScrollingEnabled(false);



    }




    public void pullMyEvents() {
        String URL="https://uthapps-backend.herokuapp.com/api/attendees/?attendees=" + username;
        Ion.with(context)
                .load(URL)
                .asJsonArray()
                .setCallback(new FutureCallback<JsonArray>() {
                    @Override
                    public void onCompleted(Exception e, JsonArray results) {
                        JsonArray result = results;
                        if(result == null) {
                            return;
                        }
                        if(result.size() == 0) {
                            return;
                        }
                        eventIDs.clear();
                        for(int i = 0; i < result.size() ; i++) {
                            JsonObject attendence = result.get(i).getAsJsonObject();
                            if(attendence.get("event_id") != null) {
                                eventIDs.add(attendence.get("event_id").getAsInt());
                            }
                        }
                        refresh();
                    }
                });

    }





    public void refresh() {
        String URL="https://uthapps-backend.herokuapp.com/api/events/";
        Ion.with(context)
                .load(URL)
                .asJsonArray()
                .setCallback(new FutureCallback<JsonArray>() {
                    @Override
                    public void onCompleted(Exception e, JsonArray results) {
                        JsonArray result = results;
                        if(result == null) {
                            return;
                        }
                        if(result.size() == 0) {
                            return;
                        }
                        events = new ArrayList<Event>();
                        for(int i = 0 ; i < results.size(); i++) {
                            JsonElement object = results.get(i);
                            try {
                                String id = object.getAsJsonObject().get("url").getAsString();
                                int index = id.lastIndexOf("/", id.length() - 2 );
                                String idNumber =id.substring(index +1);
                                idNumber = idNumber.replace("/" , "");
                                if(eventIDs.contains(Integer.valueOf(idNumber))) {
                                    Event event = new Event(object);
                                    events.add(event);
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

                    }
                });

    }




    @Override
    public boolean onSupportNavigateUp(){
        onBackPressed();
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
        pullMyEvents();
    }
}
