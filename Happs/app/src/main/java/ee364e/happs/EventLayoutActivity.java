package ee364e.happs;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
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

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class EventLayoutActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    List<Event> events = new ArrayList<Event>();
    String result;
    private double longitude;
    private double latitude;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final EventLayoutActivity eventlayout = this;
        context = this;
        Intent intent = getIntent();
        result = intent.getStringExtra("data");
        longitude = intent.getDoubleExtra("longitude", -101);
        latitude = intent.getDoubleExtra("latitude", 123);
        try {
            JSONArray jObject = new JSONArray(result);
            for(int i = 0 ; i < jObject.length(); i++) {
                JSONObject object = jObject.getJSONObject(i);
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
                intent.putExtra("data", result);
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

   /* @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }*/

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
        new LongRunningGetIO().execute();
    }




    private class LongRunningGetIO extends AsyncTask<Void, Void, String> {

        private final String URL = "http://teamhapps.herokuapp.com/api/events/";

        protected String readIt(InputStream stream, int len) throws IOException {
            Reader reader = null;
            reader = new InputStreamReader(stream, "UTF-8");
            BufferedReader r = new BufferedReader(reader);
            StringBuilder total = new StringBuilder();
            String line;
            while ((line = r.readLine()) != null) {
                total.append(line);
            }
            return total.toString();
        }

        @Override
        protected String doInBackground(Void... params) {
            InputStream is = null;
            // Only display the first 500 characters of the retrieved
            // web page content.
            int len = 10000;

            try {
                URL url = null;
                try {
                    url = new URL(URL);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }


                HttpURLConnection conn = null;
                try {
                    conn = (HttpURLConnection) url.openConnection();
                } catch (IOException e) {
                    e.printStackTrace();
                }


                conn.setReadTimeout(10000 /* milliseconds */);
                conn.setConnectTimeout(15000 /* milliseconds */);

                try {
                    conn.setRequestMethod("GET");
                } catch (ProtocolException e) {
                    e.printStackTrace();
                }
                conn.setDoInput(true);
                // Starts the query
                try {
                    conn.connect();
                    int response = conn.getResponseCode();
                    is = conn.getInputStream();
                    String contentAsString = readIt(is, len);
                    return contentAsString;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                // Convert the InputStream into a string



                return "retrieval failed";

                // Makes sure that the InputStream is closed after the app is
                // finished using it.
            } finally {
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }



        protected void onPostExecute(String results) {
            events = new ArrayList<Event>();
            result =results;
            try {
                JSONArray jObject = new JSONArray(result);
                for(int i = 0 ; i < jObject.length(); i++) {
                    JSONObject object = jObject.getJSONObject(i);
                    Event event = new Event(object);
                    events.add(event);
                }
            } catch (JSONException e) {
                e.printStackTrace();
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
    }
}
