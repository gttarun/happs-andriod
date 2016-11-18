package ee364e.happs;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class EventLayout extends AppCompatActivity {

    List<Event> events = new ArrayList<Event>();
    String result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        result = intent.getStringExtra("data");
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
        setContentView(R.layout.event_layout);
        LinearLayoutManager lim = new LinearLayoutManager(getApplicationContext());
        RecyclerView rv = (RecyclerView)findViewById(R.id.rv);
        rv.setLayoutManager(lim);
        rv.setHasFixedSize(true);
        rv.setNestedScrollingEnabled(false);
        RVAdapter adapter = new RVAdapter(events);
        rv.setAdapter(adapter);

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

        else if (id == R.id.maps) {
            Intent intent = new Intent(this, MapsActivity.class);
            intent.putExtra("data", result);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }
}
