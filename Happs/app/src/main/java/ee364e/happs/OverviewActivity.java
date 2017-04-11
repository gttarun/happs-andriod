package ee364e.happs;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
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
    String pictureURL;
    String username;
    ProgressDialog dialog;
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
        dialog = ProgressDialog.show(this, "",
                "Please wait for few seconds...", true);
        StorageReference mStorageRef  = FirebaseStorage.getInstance().getReference();
        Uri file = event.getURI();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        username = preferences.getString("username", "anonymous");
        StorageReference picRef = mStorageRef.child( username +"/" + file.getLastPathSegment());

        picRef.putFile(file)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Get a URL to the uploaded content
                        Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        pictureURL = downloadUrl.toString();
                        eventPost();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        dialog.dismiss();
                        Toast.makeText(context,R.string.picture_upload_error, Toast.LENGTH_LONG).show();
                    }
                });


    }




    public void eventPost() {
        JsonObject json = new JsonObject();
        json.addProperty("event_name",event.getName());
        json.addProperty("longitude", String.valueOf(event.getLongitude()));
        json.addProperty("latitude", String.valueOf(event.getLatitude()));
        json.addProperty("address", event.getAddress());
        json.addProperty("date", event.getStartYear() + "-" + event.getStartMonth() + "-" + event.getStartDay());
        json.addProperty("start_time", event.getStartHour() + ":" + event.getStartMinute() + ":" + "00");
        json.addProperty("end_time", event.getEndHour() + ":" + event.getEndMinute() + ":" + "00");
        if(event.getdetails().isEmpty()) {
            event.setdetails("details");
        }
        json.addProperty("description", event.getdetails());
        json.addProperty("picture", pictureURL);
        json.addProperty("place_name", event.getPlaceName());
        username = "https://uthapps-backend.herokuapp.com/api/users/" + username + "/";


        json.addProperty("host", username );
        json.addProperty("private", !event.isPublicEvent());
        json.addProperty("invites_enabled", event.isInvites());

            String URL = "https://uthapps-backend.herokuapp.com/api/events/";
            Ion.with(context)
                    .load("POST", URL)
                    .setJsonObjectBody(json)
                    .asJsonObject()
                    .setCallback(new FutureCallback< JsonObject>() {

                        @Override
                        public void onCompleted(Exception e, JsonObject result) {
                            String id = result.get("url").getAsString();
                            picturePost(id);
                        }
                    });

    }

    void picturePost(String id) {
        JsonObject json = new JsonObject();
        json.addProperty("username",username);
        json.addProperty("datafile", pictureURL);
        json.addProperty("event_id", id);
        String URL = "https://uthapps-backend.herokuapp.com/api/photos/";
        Ion.with(context)
                .load("POST", URL)
                .setJsonObjectBody(json)
                .asJsonObject()
                .setCallback(new FutureCallback< JsonObject>() {

                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        Intent intent = new Intent(getApplicationContext(), EventLayoutActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        dialog.dismiss();
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
