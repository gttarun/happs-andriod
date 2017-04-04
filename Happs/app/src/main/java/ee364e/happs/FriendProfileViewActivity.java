package ee364e.happs;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;


public class FriendProfileViewActivity extends AppCompatActivity {

    ImageView mProfilePic;
    TextView mName;
    TextView mStatus;
    TextView mUserName;
    Profile mProfile;
    String status;
    String username;
    String myName;
    Button button;
    Context context;
    ProgressBar spinner;
    boolean requestSent;
    boolean requestReceived;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_view);
        context = this;
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        spinner = (ProgressBar)findViewById(R.id.progressBar1);
        spinner.setVisibility(View.GONE);
        Intent intent = getIntent();
        mProfile = new Profile();
        mProfile.setUserName(intent.getStringExtra("username"));
        username = mProfile.getUserName();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        myName = preferences.getString("username", "");
        status = intent.getStringExtra("status");
        mProfilePic = (ImageView) findViewById(R.id.profilePic);
        mName = (TextView) findViewById(R.id.Name);
        mUserName = (TextView) findViewById(R.id.userName);
        mStatus = (TextView) findViewById(R.id.status);
        button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createFriendship(username);
            }
        });
        mUserName.setText(mProfile.getUserName());
        if(status != null) {
            mStatus.setText(mProfile.getStatus());
            changeButton(status);
        }
        inflateView();
    }

    void inflateView() {
        spinner.setVisibility(View.VISIBLE);
        String URL = "https://uthapps-backend.herokuapp.com/api/users/?username=" + username;
        Ion.with(context)
                .load(URL)
                .asJsonArray()
                .setCallback(new FutureCallback<JsonArray>() {
                    @Override
                    public void onCompleted(Exception e, JsonArray results) {
                        if(results.size() != 0) {
                            JsonObject result = results.get(0).getAsJsonObject();
                            mProfile.setName(result.get("name").getAsString());
                            mProfile.setProfilePic(result.get("picture").getAsString());
                            Glide.with(context).load(mProfile.getProfilePic()).centerCrop().placeholder(R.drawable.com_facebook_profile_picture_blank_portrait).into(mProfilePic);
                            mName.setText(mProfile.getName());
                            if(mProfile.getStatus() == null) {
                                getFriendshipStatus();
                            } else {
                                spinner.setVisibility(View.GONE);
                            }
                        } else {
                            Toast.makeText(context, "Error on loading user data", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    void getFriendshipStatus() {
        String URL = "https://uthapps-backend.herokuapp.com/api/friendships/?person=" + myName + "&friend=" + username;
        Ion.with(context)
                .load(URL)
                .asJsonArray()
                .setCallback(new FutureCallback<JsonArray>() {
                    @Override
                    public void onCompleted(Exception e, JsonArray results) {
                        if(results.size() != 0) {
                            requestReceived = true;
                            getFriendshipStatus2();
                        } else {
                            requestReceived = false;
                            getFriendshipStatus2();
                        }
                    }
                });
    }

    void getFriendshipStatus2() {
        String URL = "https://uthapps-backend.herokuapp.com/api/friendships/?person=" + username + "&friend=" + myName;
        Ion.with(context)
                .load(URL)
                .asJsonArray()
                .setCallback(new FutureCallback<JsonArray>() {
                    @Override
                    public void onCompleted(Exception e, JsonArray results) {
                        if(results.size() != 0) {
                            requestSent = true;
                        } else {
                            requestSent = false;
                        }
                        if(requestSent & requestReceived) {
                            mProfile.setStatus("friends");
                        } else if (requestSent) {
                            mProfile.setStatus("request sent");
                        } else if (requestReceived) {
                            mProfile.setStatus("request received");
                        } else {
                            mProfile.setStatus("strangers");
                        }
                        mStatus.setText(mProfile.getStatus());
                        status = mProfile.getStatus();
                        changeButton(status);
                        spinner.setVisibility(View.GONE);
                    }
                });
    }

    void changeButton(String status) {
        if(status.equals("friends")) {
            button.setText("Undo Friendship");
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteFriendship(username);
                }
            });
        } else if (status.equals("request sent")) {
            button.setText("Cancel Request");
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteFriendship(username);
                }
            });
        } else if (status.equals("request received")) {
            button.setText("Accept Request");
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    createFriendship(username);
                }
            });
        } else {
            button.setText("Send Request");
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    createFriendship(username);
                }
            });
        }
    }


    void createFriendship(String username) {
        String URL = "https://uthapps-backend.herokuapp.com/api/friendships/";
        JsonObject json = new JsonObject();
        json.addProperty("status", "Accepted");
        json.addProperty("person", myName );
        json.addProperty("friend", username);
        Ion.with(context)
                .load("POST", URL)
                .setJsonObjectBody(json)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject results) {
                        inflateView();
                        Toast.makeText(context, "Request Sent", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    void deleteFriendship(String username) {
        String URL = "https://uthapps-backend.herokuapp.com/api/friendships/?person=" + myName + "&friend=" + username;
        Ion.with(context)
                .load(URL)
                .asJsonArray()
                .setCallback(new FutureCallback<JsonArray>() {
                    @Override
                    public void onCompleted(Exception e, JsonArray results) {
                        if(results == null) {
                            Toast.makeText(context, "Server Error. Please try again later", Toast.LENGTH_SHORT).show();
                        } else if (results.size() == 0) {
                            return;
                        } else {
                            JsonObject result = results.get(0).getAsJsonObject();
                            int id = result.get("id").getAsInt();
                            makeDeleteRequest(id);
                        }
                    }
                });
    }

    void makeDeleteRequest(int id ) {
        String URL = "https://uthapps-backend.herokuapp.com/api/friendships/" + id + "/destroy";
        Ion.with(context)
                .load("DELETE", URL)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject results) {
                        inflateView();
                        Toast.makeText(context, "Request Deleted", Toast.LENGTH_SHORT).show();
                    }
                });
    }




    @Override
    public boolean onSupportNavigateUp(){
        onBackPressed();
        return true;
    }

}
