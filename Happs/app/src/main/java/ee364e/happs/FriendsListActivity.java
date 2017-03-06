package ee364e.happs;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

public class FriendsListActivity extends AppCompatActivity {
    private ArrayList<Profile> mFriends;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_list);


        //replace following code to pull friends list from server
        mFriends = new ArrayList<Profile>();
        for (int i = 0; i < 10; i++){
            Profile p = new Profile();
            p.setName("name" + i);
            p.setUserName("Uname" + i );
            p.setDescription(i + "'s Status");
            mFriends.add(p);
        }

        mRecyclerView = (RecyclerView) findViewById(R.id.friendsList_recycler_view);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new FriendsListRVAdapter(mFriends, new FriendsListRVAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Profile profile) {
                Intent intent = new Intent(getApplicationContext(), FriendProfileViewActivity.class);
                EventBus.getDefault().postSticky(profile);
                startActivity(intent);
                //Toast.makeText(getApplicationContext(),"you have clicked " + profile.getName(),Toast.LENGTH_SHORT).show();

            }
        });
        mRecyclerView.setAdapter(mAdapter);
    }

}
