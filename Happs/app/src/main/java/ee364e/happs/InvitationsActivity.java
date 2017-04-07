package ee364e.happs;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;


import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.ArrayList;

public class InvitationsActivity extends AppCompatActivity {

    Context context;
    ArrayList<User> invitees = new ArrayList<>();
    ArrayList<String> requestReceived = new ArrayList<>();
    ArrayList<String> requestsSent = new ArrayList<>();

    MyCustomAdapter dataAdapter = null;
    ProgressBar progress;
    String username;
    String eventID;
    String eventName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.activity_invitations);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        progress = (ProgressBar) findViewById(R.id.progressBar2);
        progress.setVisibility(View.INVISIBLE);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        username = preferences.getString("username", "");
        Intent intent = getIntent();
        eventID = intent.getStringExtra("event_id");
        eventName = intent.getStringExtra("event_name");
        getMyFriendRequests();
    }



    private class User {
        String user;
        boolean check;

        public User(String user) {
            this.user = user;
            this.check = false;
        }

        public void setCheck(boolean check) {
            this.check = check;
        }

    }


    private class MyCustomAdapter extends ArrayAdapter<User> {

        private ArrayList<User> userList;

        public MyCustomAdapter(Context context, int textViewResourceId,
                               ArrayList<User> userList) {
            super(context, textViewResourceId, userList);
            this.userList = new ArrayList<User>();
            this.userList.addAll(userList);
        }

        private class ViewHolder {
            CheckBox name;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder = null;

            if (convertView == null) {
                LayoutInflater vi = (LayoutInflater) getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE);
                convertView = vi.inflate(R.layout.user, null);

                holder = new ViewHolder();
                holder.name = (CheckBox) convertView.findViewById(R.id.checkBox1);
                convertView.setTag(holder);

                holder.name.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        CheckBox cb = (CheckBox) v;
                        User user = (User) cb.getTag();
                        user.setCheck(cb.isChecked());
                    }
                });
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            User user = userList.get(position);
            holder.name.setText(user.user);
            holder.name.setChecked(user.check);
            holder.name.setTag(user);

            return convertView;

        }

    }

    private void checkButtonClick() {


        Button myButton = (Button) findViewById(R.id.findSelected);
        myButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                progress.setVisibility(View.VISIBLE);

                ArrayList<User> userList = dataAdapter.userList;
                for (int i = 0; i < userList.size(); i++) {
                    User user = userList.get(i);
                    if (user.check) {
                        sendInvitation(user.user);
                    }
                }
                progress.setVisibility(View.INVISIBLE);
                Toast.makeText(context, "Invitations Sent", Toast.LENGTH_LONG).show();
                finish();
            }
        });

    }

    synchronized void sendInvitation(String username) {
        JsonObject hey = new JsonObject();
        hey.addProperty("title", "You are invited to " + eventName);
        hey.addProperty("body", "invitation from " + username );
        hey.addProperty("eventURL", eventID);
        JsonObject element = new JsonObject();
        element.addProperty("to", "/topics/" + username);
        element.add("data", hey);
        String token = "key=AAAAdipRNIw:APA91bFpongxwJ0ngbSAJEE7d_JFrexKa06aSVjy0jYHJ6XURU5wLgsk2mYBKMb4MM7Qzg0bL_CM0yfw2juECVvijtEakTpN0kwjmUF1XfvJPc3EQKdW9tuZD-slqQTsbXFBWIc7Q8gE";
        Ion.with(this).load("https://fcm.googleapis.com/fcm/send").setHeader("Authorization", token).setHeader("Content-Type", "application/json").setJsonObjectBody(element)
        .asJsonObject().setCallback(new FutureCallback< JsonObject>() {
            @Override
            public void onCompleted(Exception e, JsonObject result) {
            }
        });
        int index = eventID.lastIndexOf("/", eventID.length() - 2 );
        String idNumber =eventID.substring(index +1);
        idNumber = idNumber.replace("/" , "");
        JsonObject invitation = new JsonObject();
        invitation.addProperty("status", "attend");
        invitation.addProperty("username", eventName);
        invitation.addProperty("event_id", idNumber);
        Ion.with(this).load("https://uthapps-backend.herokuapp.com/api/invitation").setJsonObjectBody(invitation)
                .asJsonObject().setCallback(new FutureCallback< JsonObject>() {
            @Override
            public void onCompleted(Exception e, JsonObject result) {
            }
        });
    }











    void getMyFriendRequests() {
        requestReceived.clear();
        requestsSent.clear();
        invitees.clear();
        String URL = "https://uthapps-backend.herokuapp.com/api/friendships/?person=" + username;
        Ion.with(context)
                .load(URL)
                .asJsonArray()
                .setCallback(new FutureCallback<JsonArray>() {
                    @Override
                    public void onCompleted(Exception e, JsonArray results) {
                        if(results == null) {
                            Toast.makeText(context, "Server Error, please try again later", Toast.LENGTH_LONG).show();
                            finish();
                        }
                        if(results.size() != 0) {
                            requestsSent.clear();
                            for(int i = 0; i < results.size(); i++) {
                                JsonObject result = results.get(i).getAsJsonObject();
                                requestsSent.add(result.get("friend").getAsString());
                            }
                            getRequestsReceived();
                        } else {
                            getRequestsReceived();
                        }
                    }
                });

    }





    void getRequestsReceived() {
        String URL = "https://uthapps-backend.herokuapp.com/api/friendships/?friend=" + username;
        Ion.with(context)
                .load(URL)
                .asJsonArray()
                .setCallback(new FutureCallback<JsonArray>() {
                    @Override
                    public void onCompleted(Exception e, JsonArray results) {
                        if(results == null) {
                            Toast.makeText(context, "Server Error, please try again later", Toast.LENGTH_LONG).show();
                            finish();
                        }
                        if(results.size() != 0) {
                            requestReceived.clear();
                            invitees.clear();
                            for(int i = 0; i < results.size(); i++) {
                                JsonObject result = results.get(i).getAsJsonObject();
                                String person = result.get("person").getAsString();
                                if(requestsSent.remove(person)) {
                                    invitees.add(new User(person));
                                } else {
                                    requestReceived.add(person);
                                }
                            }
                            setFriendList();
                        } else {
                            setFriendList();
                        }
                    }
                });
    }

    void setFriendList() {
        checkButtonClick();
        //create an ArrayAdaptar from the String Array
        dataAdapter = new MyCustomAdapter(this,
                R.layout.user, invitees);
        ListView listView = (ListView) findViewById(R.id.listView1);
        // Assign adapter to ListView
        listView.setAdapter(dataAdapter);
    }












    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
}









