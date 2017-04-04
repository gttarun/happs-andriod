package ee364e.happs;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

public class FriendsListActivity extends AppCompatActivity {
    private ArrayList<Profile> mFriends;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;


    String username;
    Context context;
    ArrayList<String> requestReceived = new ArrayList<String>();
    ArrayList<String> requestsSent = new ArrayList<String>();
    ArrayList<String> friendships = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_list);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        username = preferences.getString("username", "");
        context = this;



        //replace following code to pull friends list from server
        mFriends = new ArrayList<Profile>();


        mRecyclerView = (RecyclerView) findViewById(R.id.friendsList_recycler_view);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new FriendsListRVAdapter(mFriends, new FriendsListRVAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Profile profile) {
                Intent intent = new Intent(getApplicationContext(), FriendProfileViewActivity.class);
                EventBus.getDefault().postSticky(profile);
                startActivity(intent);
            }
        });
        mRecyclerView.setAdapter(mAdapter);
        getMyFriendRequests();
    }


    void getMyFriendRequests() {
        mFriends.clear();
        String URL = "https://uthapps-backend.herokuapp.com/api/friendships/?person=" + username;
        Ion.with(context)
                .load(URL)
                .asJsonArray()
                .setCallback(new FutureCallback<JsonArray>() {
                    @Override
                    public void onCompleted(Exception e, JsonArray results) {
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
                        if(results.size() != 0) {
                            requestReceived.clear();
                            friendships.clear();
                            for(int i = 0; i < results.size(); i++) {
                                JsonObject result = results.get(i).getAsJsonObject();
                                String person = result.get("person").getAsString();
                                if(requestsSent.remove(person)) {
                                    friendships.add(person);
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
        for(String friend: requestsSent) {
            Profile profile = new Profile();
            profile.setUserName(friend);
            profile.setStatus("request sent");
            mFriends.add(profile);
        }

        for(String friend: requestReceived) {
            Profile profile = new Profile();
            profile.setUserName(friend);
            profile.setStatus("request received");
            mFriends.add(profile);
        }

        for(String friend: friendships) {
            Profile profile = new Profile();
            profile.setUserName(friend);
            profile.setStatus("friends");
            mFriends.add(profile);
        }

        mAdapter = new FriendsListRVAdapter(mFriends, new FriendsListRVAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Profile profile) {
                Intent intent = new Intent(getApplicationContext(), FriendProfileViewActivity.class);
                intent.putExtra("username", profile.getUserName());
                intent.putExtra("status", profile.getStatus());
                startActivity(intent);
            }
        });
        mRecyclerView.setAdapter(mAdapter);
    }



    @Override
    public boolean onSupportNavigateUp(){
        onBackPressed();
        return true;
    }

}
