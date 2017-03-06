package ee364e.happs;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;

public class FriendProfileViewActivity extends AppCompatActivity {

    ImageView mProfilePic;
    TextView mName;
    TextView mStatus;
    Profile mProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Intent intent = getIntent();
        mProfile = EventBus.getDefault().removeStickyEvent(Profile.class);
        mProfilePic = (ImageView) findViewById(R.id.ProfilePic);
        mName = (TextView) findViewById(R.id.Name);
        mStatus = (TextView) findViewById(R.id.status);

        mName.setText(mProfile.getName());
        mStatus.setText(mProfile.getDescription());
    }

}
