package ee364e.happs;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import org.greenrobot.eventbus.EventBus;

public class EventDetailsActivity extends AppCompatActivity {
    EditText details;
    EditText name;
    Event event;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);
        event = EventBus.getDefault().removeStickyEvent(Event.class);
        name = (EditText) findViewById(R.id.editText6);
        details = (EditText) findViewById(R.id.editText8);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.camera_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.next) {
            Intent intent = new Intent(this, OverviewActivity.class);
            event.setName(name.getText().toString());
            event.setdetails(details.getText().toString());
            EventBus.getDefault().postSticky(event);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }
}
