package ee364e.happs;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;


/***
 * created by Chan-Young Kim
 *
 * Note for Ali:
 * Implement the attend button -- todo: waiting for back end
 * implement the picture changing on swipe (see below)todo: waiting for backend
 * implement invite people (use popUpAttendees as a reference) -- not doing anymore (as of now)
 * Actually make use of the Login Activity (currently, not being used. Probably make the login the --- done!
    * opening screen and then move onto MainActivity
 * Use preferences (shared preferences)(google android preferences) to store user data.
     * user data should include :
         * username
         * name
         * profile picture (image uri)
         * login (status true or false) -- i dont think we need this
         * password
 ***/

public class EventActivity extends AppCompatActivity  {

    Event event;
    TextView eventName;
    TextView userName;
    ImageView eventImage;
    LinearLayout eventActivity;
    String id;
    Context context;
    private GestureDetector detector;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_event);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        event = EventBus.getDefault().removeStickyEvent(Event.class);
        eventName = (TextView) findViewById(R.id.eventName);
        userName = (TextView) findViewById(R.id.userName);
        eventImage = (ImageView) findViewById(R.id.eventImage);
        eventActivity = (LinearLayout) findViewById(R.id.event_activity);
        eventName.setText(event.getName());
        actionBar.setTitle(event.getName());
        context = this;
        userName.setText("Created by " + event.getUsername());
        Glide.with(this).load("https://scontent-dft4-2.xx.fbcdn.net/v/t1.0-1/p240x240/16406708_221242205005925_2427821695388626742_n.jpg?oh=246493437d8a886c0e94673456c5cf6e&oe=5902F043").placeholder(R.drawable.happs).into(eventImage);
        id = String.valueOf(event.getId());
        detector = new GestureDetector(context, new OnSwipeListener() {

            @Override
            public boolean onSwipe(Direction direction) {

                // Replace these to change images on image View
                if (direction == Direction.left ) {
                    Toast.makeText(context, "left", Toast.LENGTH_SHORT).show();
                    return true;
                } else if (direction == Direction.right) {
                    Toast.makeText(context, "right", Toast.LENGTH_SHORT).show();
                    return true;
                }

                return super.onSwipe(direction);
            }
        });
        View.OnTouchListener swipeListener = new View.OnTouchListener()
        {
            public boolean onTouch(View v, MotionEvent event)
            {
                if (detector.onTouchEvent(event))
                {
                    return true;
                }
                else{
                    return false;
                }
            }
        };
        eventImage.setOnTouchListener(swipeListener);




    }


    public void openChat(View v) {
        Intent intent = new Intent(this, FirebaseChat.class);
        intent.putExtra("id", id);
        startActivity(intent);
    }

    public void openAttendees(View v){
        final View popupView = LayoutInflater.from(context).inflate(R.layout.popup_recylerview, null);
        final PopupWindow popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, 1200);

        ImageButton btn = (ImageButton) popupView.findViewById(R.id.popup_close);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });

        RecyclerView recyclerView = (RecyclerView) popupView.findViewById(R.id.popup_recyle);
        ArrayList<String> data = new ArrayList<>();
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        ///////////  Should get the attendees data into data!
        data.add("my data");
        data.add("my test data");
        data.add("my test data4");
        data.add("my test data2");
        data.add("my test data5");
        data.add("my test data6");
        data.add("my test data7");
        data.add("my test data8");
        data.add("my test data9");
        data.add("my test data5");
        PopupAttendeesAdapter adapter = new PopupAttendeesAdapter(context,data);
        recyclerView.setAdapter(adapter);

        popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);


    }


    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.event_menu, menu);
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        boolean handled = super.dispatchTouchEvent(event);
        detector.onTouchEvent(event);
        return handled;

    }
}
