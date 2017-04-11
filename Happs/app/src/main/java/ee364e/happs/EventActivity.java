package ee364e.happs;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
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
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import id.zelory.compressor.Compressor;


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

    TextView name;
    TextView placeName;
    TextView address;
    TextView details;
    ImageView picture;
    TextView endTime;
    TextView startTime;
    TextView startDate;
    TextView privateOrPublic;
    Button invites;
    Button direction;
    ArrayList<String> pictures;
    int pictureNumber = 0;

    Event event;
    TextView eventName;
    TextView userName;
    ImageView eventImage;
    Button attend;
    LinearLayout eventActivity;
    String id;
    String username;
    String pictureURL;
    Context context;
    ArrayList<String> data;
    int attendID = -1;
    private GestureDetector detector;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_event);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        eventName = (TextView) findViewById(R.id.eventName);
        userName = (TextView) findViewById(R.id.userName);
        eventImage = (ImageView) findViewById(R.id.eventImage);
        eventActivity = (LinearLayout) findViewById(R.id.event_activity);
        context = this;
        placeName = (TextView) findViewById(R.id.placenameOverView);
        address = (TextView) findViewById(R.id.addressOverView);
        attend = (Button) findViewById(R.id.attend);
        details = (TextView) findViewById(R.id.detailsOverView);
        endTime = (TextView) findViewById(R.id.endTimeOverView);
        startTime = (TextView) findViewById(R.id.startTimeOverView);
        startDate = (TextView) findViewById(R.id.dateOverView);
        privateOrPublic = (TextView) findViewById(R.id.privateOverView);
        invites = (Button) findViewById(R.id.invites);
        direction = (Button) findViewById(R.id.directions);
        event = EventBus.getDefault().removeStickyEvent(Event.class);
        eventName.setText(event.getName());
        actionBar.setTitle(event.getName());
        userName.setText("Created by " + event.getUsername());
        placeName.setText(event.getPlaceName());
        address.setText(event.getAddress());
        details.setText(event.getdetails());
        startDate.setText(event.getDate());
        data = new ArrayList<>();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        username = preferences.getString("username", "anonymous");
        attend.setOnClickListener( new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                sendAttendRequest();
            }
        });

        if(event.isPublicEvent()) {
            privateOrPublic.setText("Public event");
        } else {
            privateOrPublic.setText("Private event");
        }
        startTime.setText(event.getStartTime());
        endTime.setText(event.getEndTime());
        id = event.getId();

        Glide.with(this).load(event.getCover()).placeholder(R.drawable.happs).into(eventImage);

        if(!event.isInvites()) {
            invites.setText("No Invites");
            invites.setClickable(false);
        } else {
            invites.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, InvitationsActivity.class);
                    intent.putExtra("event_name", event.getName());
                    intent.putExtra("event_id", event.getId());
                    startActivity(intent);
                }
            });
        }

        direction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri gmmIntentUri = Uri.parse("google.navigation:q=" + event.getLatitude() + "," + event.getLongitude());
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
            }


        });

        detector = new GestureDetector(context, new OnSwipeListener() {

            @Override
            public boolean onSwipe(Direction direction) {

                if (pictures == null) {
                    return true;
                }
                // Replace these to change images on image View
                if (direction == Direction.left ) {
                    if(pictureNumber == 0) {
                        pictureNumber = pictures.size() - 1;
                    } else {
                        pictureNumber--;
                    }
                    Glide.with(context).load(pictures.get(pictureNumber)).placeholder(R.drawable.happs).into(eventImage);
                    return true;
                } else if (direction == Direction.right) {
                    if(pictureNumber == pictures.size() - 1) {
                        pictureNumber = 0;
                    } else {
                        pictureNumber++;
                    }
                    Glide.with(context).load(pictures.get(pictureNumber)).placeholder(R.drawable.happs).into(eventImage);
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
        refresh();
        getAttendees();
    }


    public void openChat(View v) {
        Intent intent = new Intent(this, FirebaseChat.class);
        int index = id.lastIndexOf("/", id.length() - 2 );
        String idNumber =id.substring(index +1);
        idNumber = idNumber.replace("/" , "");
        intent.putExtra("id", idNumber);
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
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        ///////////  Should get the attendees data into data!
        PopupAttendeesAdapter adapter = new PopupAttendeesAdapter(context, data, new PopupAttendeesAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String name) {
                if(name.equals(username)) {
                    Intent intent = new Intent(context, MainUserProfileActivity.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(context, FriendProfileViewActivity.class);
                    intent.putExtra("username", name);
                    startActivity(intent);
                }
            }
        });
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

        if(id == R.id.camera) {
            cameraOpen();
        } else if (id == R.id.refresh) {
            refresh();
            getAttendees();
        }


        //noinspection SimplifiableIfStatement
        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        boolean handled = super.dispatchTouchEvent(event);
        detector.onTouchEvent(event);
        return handled;

    }


    Uri file;
    private static final int CAMERA_REQUEST = 1;
    String mCurrentPhotoPath;
    Uri resultUri;
    File finalFile;



    void cameraOpen() {

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            file = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", getOutputMediaFile());
        } else {
            file = Uri.fromFile(getOutputMediaFile());
        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT, file);
        startActivityForResult(intent, CAMERA_REQUEST);
    }

    private File getOutputMediaFile(){
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "Happs");
        if (!mediaStorageDir.exists()){
            if (!mediaStorageDir.mkdirs()){
                return null;
            }
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

        File image =  new File(mediaStorageDir.getPath() + File.separator +
                "IMG_"+ timeStamp + ".jpg");
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
            MediaScannerConnection.scanFile(this,
                    new String[]{file.getPath()}, null,
                    new MediaScannerConnection.OnScanCompletedListener() {
                        public void onScanCompleted (String path, Uri uri ) {
                        }
                    });
            CropImage.activity(file)
                    .setFixAspectRatio(true)
                    .setAspectRatio(1,1)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .start(this);
        } else if (resultCode == 0) {
            finish();
        }  else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                resultUri = result.getUri();
                finalFile = new File(resultUri.getPath());
                finalFile = Compressor.getDefault(this).compressToFile(finalFile);
                resultUri = Uri.fromFile(finalFile);
                StorageReference mStorageRef  = FirebaseStorage.getInstance().getReference();
                StorageReference picRef = mStorageRef.child( username +"/" + file.getLastPathSegment());

                picRef.putFile(resultUri)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                // Get a URL to the uploaded content
                                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                                pictureURL = downloadUrl.toString();
                                picturePost(id);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                Toast.makeText(context,R.string.picture_upload_error, Toast.LENGTH_LONG);
                            }
                        });

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    void picturePost(String id) {
        JsonObject json = new JsonObject();
        json.addProperty("username","https://uthapps-backend.herokuapp.com/api/users/" + username + "/");
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
                        refresh();
                    }
                });

    }

    void refresh(){
        int index = id.lastIndexOf("/", id.length() - 2 );
        String idNumber =id.substring(index +1);
        idNumber = idNumber.replace("/" , "");
        String searchURL = "https://uthapps-backend.herokuapp.com/api/photos/?event_id=" + idNumber;
        Ion.with(context)
                .load(searchURL)
                .asJsonArray()
                .setCallback(new FutureCallback< JsonArray>() {

                    @Override
                    public void onCompleted(Exception e, JsonArray results) {
                        if(results.size() > 0) {
                            pictureNumber = 0;
                            pictures = new ArrayList<String>();
                            for(int i = 0; i < results.size() ; i++) {
                                JsonElement result = results.get(i);
                                JsonObject object = result.getAsJsonObject();
                                pictures.add(object.get("datafile").getAsString());
                            }
                        }

                    }
                });

    }

    void getAttendees() {
        int index = id.lastIndexOf("/", id.length() - 2);
        String idNumber = id.substring(index + 1);
        idNumber = idNumber.replace("/", "");
        String searchURL = "https://uthapps-backend.herokuapp.com/api/attendees/?event_id=" + idNumber;
        Ion.with(context)
                .load(searchURL)
                .asJsonArray()
                .setCallback(new FutureCallback<JsonArray>() {
                    @Override
                    public void onCompleted(Exception e, JsonArray results) {
                        data.clear();
                        if(results == null) {
                            Toast.makeText(context, "Error on loading attendees. Please try again.", Toast.LENGTH_SHORT).show();
                        } else if (results.size() > 0) {
                            for(int i = 0; i < results.size(); i++) {
                                JsonObject result = results.get(i).getAsJsonObject();
                                JsonArray attendees = result.get("attendees").getAsJsonArray();
                                String attendee = attendees.get(0).getAsString().replace("\"", "");
                                data.add(attendee);
                                if(attendee.equals(username)){
                                    attendID = result.get("attendees_id").getAsInt();
                                }
                            }
                        }
                        changeAttendButton();

                    }
                });
    }


    void changeAttendButton() {
        if(attendID != -1) {
            attend.setText("DON'T ATTEND");
            attend.setOnClickListener( new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    deleteAttendRequest();
                }
            });
        } else {
            attend.setText("ATTEND");
            attend.setOnClickListener( new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    sendAttendRequest();
                }
            });
        }

    }

    void sendAttendRequest(){
        int index = id.lastIndexOf("/", id.length() - 2);
        String idNumber = id.substring(index + 1);
        idNumber = idNumber.replace("/", "");
        JsonObject json = new JsonObject();
        ArrayList<String> array = new ArrayList();
        array.add("https://uthapps-backend.herokuapp.com/api/users/" + username + "/");
        String attendee = array.toString();
        json.addProperty("attendees", attendee);
        json.addProperty("event_id", idNumber);
        String URL = "https://uthapps-backend.herokuapp.com/api/attendees/";
        Ion.with(context)
                .load("POST", URL)
                .setMultipartParameter("event_id", idNumber)
                .setMultipartParameter("attendees", username)
                .asJsonObject()
                .setCallback(new FutureCallback< JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        getAttendees();
                    }
                });

    }

    void deleteAttendRequest(){
        String URL = "https://uthapps-backend.herokuapp.com/api/attendees/" + attendID + "/destroy/";
        Ion.with(context)
                .load("DELETE", URL)
                .asJsonObject()
                .setCallback(new FutureCallback< JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        attendID = -1;
                        getAttendees();
                    }
                });
    }

}
