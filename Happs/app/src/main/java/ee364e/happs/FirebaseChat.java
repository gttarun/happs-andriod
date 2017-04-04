package ee364e.happs;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseChat extends AppCompatActivity {

    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        public TextView message;
        public TextView username;
        public LinearLayout messageHolder;


        public MessageViewHolder(View v) {
            super(v);
            message = (TextView) itemView.findViewById(R.id.message_message);
            username = (TextView) itemView.findViewById(R.id.message_username);
            messageHolder = (LinearLayout) itemView.findViewById(R.id.message_holder);
        }
    }

    private String MESSAGES_CHILD;
    private FirebaseRecyclerAdapter<Message, MessageViewHolder> mFirebaseAdapter;
    private DatabaseReference mFirebaseDatabaseReference;
    private  String username = "";
    private RecyclerView mMessageRecyclerView;
    private ImageButton mSendButton;
    private EditText mMessageEditText;
    private LinearLayoutManager mLinearLayoutManager;
    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firebasechat);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        id = intent.getStringExtra("id");

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        username = preferences.getString("username", "");


        if(id == null) {
            MESSAGES_CHILD = "messages";
        } else {
            MESSAGES_CHILD = id;
        }

        mMessageRecyclerView = (RecyclerView) findViewById(R.id.message_recycler_view);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setStackFromEnd(true);


        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();

        mFirebaseAdapter = new FirebaseRecyclerAdapter<Message, MessageViewHolder>(
                Message.class,
                R.layout.message,
                MessageViewHolder.class,
                mFirebaseDatabaseReference.child(MESSAGES_CHILD)

        ) {

            @Override
            protected Message parseSnapshot(DataSnapshot snapshot) {
                Message message = super.parseSnapshot(snapshot);
                if (message != null) {
                    message.setId(snapshot.getKey());
                }
                return message;
            }


            @Override
            protected void populateViewHolder(MessageViewHolder viewHolder, Message message, int position) {
                viewHolder.message.setText(message.getMessage());
                viewHolder.username.setText(message.getUsername());
                if(message.getUsername().equalsIgnoreCase(username)) {
                    viewHolder.messageHolder.setBackgroundColor(Color.parseColor("#FFFFFF"));
                }
            }
        };

        mFirebaseAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted( positionStart, itemCount);
                int messageCount = mFirebaseAdapter.getItemCount();
                int lastVisiblePosition = mLinearLayoutManager.findLastCompletelyVisibleItemPosition();

                if (lastVisiblePosition == -1 || (positionStart >= (messageCount -1) && lastVisiblePosition == (positionStart - 1))) {
                    mMessageRecyclerView.scrollToPosition(positionStart);
                }
            }

        });

        mMessageRecyclerView.setLayoutManager(mLinearLayoutManager);
        mMessageRecyclerView.setAdapter(mFirebaseAdapter);




        mMessageEditText = (EditText) findViewById(R.id.messageEditText);
        mSendButton = (ImageButton) findViewById(R.id.sendButton);
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Message message = new Message(username, mMessageEditText.getText().toString());
                mFirebaseDatabaseReference.child(MESSAGES_CHILD).push().setValue(message);
                mMessageEditText.setText("");
            }
        });





    }


    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

}
