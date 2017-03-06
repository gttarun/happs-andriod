package ee364e.happs;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.greenrobot.eventbus.EventBus;
import java.io.File;

public class OverviewActivity extends AppCompatActivity {
    TextView name;
    TextView placeName;
    TextView address;
    TextView details;
    ImageView picture;
    TextView endTime;
    TextView startTime;
    TextView startDate;
    TextView privateOrPublic;
    TextView invitesEnabled;
    Event event;
    Context context;
    int id = -1;


    File finalFile;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overview);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        name = (TextView) findViewById(R.id.nameOverView);
        placeName = (TextView) findViewById(R.id.placenameOverView);
        address = (TextView) findViewById(R.id.addressOverView);
        details = (TextView) findViewById(R.id.detailsOverView);
        picture = (ImageView) findViewById(R.id.imageViewOverView);
        endTime = (TextView) findViewById(R.id.endTimeOverView);
        startTime = (TextView) findViewById(R.id.startTimeOverView);
        startDate = (TextView) findViewById(R.id.dateOverView);
        privateOrPublic = (TextView) findViewById(R.id.privateOverView);
        invitesEnabled = (TextView) findViewById(R.id.invitesEnableOverView);
        context = this;
        event = EventBus.getDefault().removeStickyEvent(Event.class);

        String names = event.getName();
        name.setText(names);
        placeName.setText(event.getPlaceName());
        address.setText(event.getAddress());
        details.setText(event.getdetails());
        startDate.setText(Integer.toString(event.getStartYear()) + "--" + Integer.toString(event.getStartMonth() + 1) + "--" + Integer.toString(event.getStartDay()));
        if(event.isInvites()) {
            invitesEnabled.setText("Invites enabled");
        } else {
            invitesEnabled.setText("Invites disabled");
        }
        if(event.isPublicEvent()) {
            privateOrPublic.setText("Public event");
        } else {
            privateOrPublic.setText("Private event");
        }
        picture.setImageURI(event.getURI());
        String time = "AM";
        int showHour = event.getStartHour();
        if(showHour >= 12) {
            time = "PM";
        }
        if(showHour > 12) {
            showHour = showHour % 12;
        } else if(showHour == 0) {
            showHour = 12;
        }
        startTime.setText(Integer.toString(showHour) + " : " + Integer.toString(event.getStartMinute()) + " " + time);
        time = "AM";
        showHour = event.getEndHour();
        if(showHour >= 12) {
            time = "PM";
        }
        if(showHour > 12) {
            showHour = showHour % 12;
        } else if(showHour == 0) {
            showHour = 12;
        }
        endTime.setText(Integer.toString(showHour) + " : " +Integer.toString(event.getEndMinute()) + " " + time);
        finalFile = new File(event.getURI().getPath());
    }







    public void EventSubmit(View view) {
        String URL = "http://teamhapps.herokuapp.com/api/events/";
        Ion.with(context)
                .load("POST", URL)
                    .setMultipartParameter("name", event.getName())
                    .setMultipartParameter("longitude", String.valueOf(event.getLongitude()))
                    .setMultipartParameter("latitude", String.valueOf(event.getLatitude()))
                    .setMultipartParameter("time", "2016-09-19T18:30:00")
                    .setMultipartParameter("user", "http://teamhapps.herokuapp.com/api/users/Santi/")
                    .setMultipartFile("datafile", "image/jpeg", finalFile)
                    .asJsonObject()
                    .setCallback(new FutureCallback< JsonObject>() {

                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        JsonObject result2 = result;
                        Intent intent = new Intent(getApplicationContext(), EventLayoutActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(intent);
                        finish();
                    }
                });
    }


    @Override
    public boolean onSupportNavigateUp(){
        onBackPressed();
        return true;
    }

}
