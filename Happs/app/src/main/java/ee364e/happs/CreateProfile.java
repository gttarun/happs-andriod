package ee364e.happs;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.identity.intents.AddressConstants;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import id.zelory.compressor.Compressor;

public class CreateProfile extends AppCompatActivity {
    private final String URL = "http://teamhapps.herokuapp.com/api/users/";
    private static final int CAMERA_REQUEST = 1;
    Uri resultUri;
    String mCurrentPhotoPath;
    private Context context;
    private Profile newUser;
    private EditText name;
    private EditText username;
    private EditText description;
    private ImageView profilepic;
    private Button submitButton;
    ProgressDialog dialog;
    String pictureURL;
    Uri file;

    int id = -1;
    File finalFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile);
        context = this;
        submitButton =(Button) findViewById(R.id.button7);
        name = (EditText) findViewById(R.id.editText4);
        username = (EditText) findViewById(R.id.editText5);
        description = (EditText) findViewById(R.id.editText6);
        profilepic = (ImageView) findViewById(R.id.imageView4);
        newUser = new Profile();
        Intent intent = getIntent();
        newUser.setUserID(intent.getStringExtra("user_id"));
        newUser.setAuthToken(intent.getStringExtra("authToken"));
        int resourceId = R.drawable.com_facebook_profile_picture_blank_square;
        Resources resources = context.getResources();
        resultUri = new Uri.Builder()
                .scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
                .authority(resources.getResourcePackageName(resourceId))
                .appendPath(resources.getResourceTypeName(resourceId))
                .appendPath(resources.getResourceEntryName(resourceId))
                .build();
    }

       public void onClickSubmit(View view) /*throws Exception*/{
           dialog = ProgressDialog.show(this, "",
                   "Please wait for few seconds...", true);
           newUser.setName(name.getText().toString());
           newUser.setUserName(username.getText().toString());
           isUniqueUserName(newUser.getUserName());
    }

    public void isUniqueUserName(String userName) {
        String searchURL = "https://uthapps-backend.herokuapp.com/api/users/?username=" + userName;
        Ion.with(context)
                .load(searchURL)
                .asJsonArray()
                .setCallback(new FutureCallback<JsonArray>() {
                    @Override
                    public void onCompleted(Exception e, JsonArray result) {
                        if(result == null) {
                            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putString("name", newUser.getName());
                            editor.putString("username", newUser.getUserName());
                            editor.putString("user_id", newUser.getUserID());
                            editor.putString("authentication_token", newUser.getAuthToken());
                            editor.apply();
                            uploadPhotoAndPost();
                        } else if(result.size() == 0) {
                            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putString("name", newUser.getName());
                            editor.putString("username", newUser.getUserName());
                            editor.putString("user_id", newUser.getUserID());
                            editor.putString("authentication_token", newUser.getAuthToken());
                            editor.apply();
                            uploadPhotoAndPost();
                        } else {
                            dialog.dismiss();
                            Toast.makeText(context, "username taken, please try a different username", Toast.LENGTH_LONG).show();
                        }

                    }
                });
    }

    void uploadPhotoAndPost() {
        StorageReference mStorageRef  = FirebaseStorage.getInstance().getReference();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String name = preferences.getString("username", "anonymous");
        StorageReference picRef = mStorageRef.child( name +"/" + resultUri.getLastPathSegment());

        picRef.putFile(resultUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Get a URL to the uploaded content
                        Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        pictureURL = downloadUrl.toString();
                        createProfile();
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

    void createProfile() {
        String URL = "https://uthapps-backend.herokuapp.com/api/users/";
        JsonObject json = new JsonObject();
        json.addProperty("name", newUser.getName());
        json.addProperty("username", newUser.getUserName());
        json.addProperty("user_id", newUser.getUserID());
        json.addProperty("authentication_token", newUser.getAuthToken());
        json.addProperty("picture", pictureURL);

        Ion.with(context)
                .load("POST", URL)
                .setJsonObjectBody(json)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        if(result != null) {
                            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putString("name", newUser.getName());
                            editor.putString("username", newUser.getUserName());
                            editor.putString("user_id", newUser.getUserID());
                            editor.putString("authentication_token", newUser.getAuthToken());
                            editor.putString("picture", pictureURL);
                            editor.apply();
                            FirebaseMessaging.getInstance().subscribeToTopic(newUser.getUserName());
                            dialog.dismiss();
                            Intent intent = new Intent (context, EventLayoutActivity.class);
                            startActivity(intent);
                            finish();
                        }
                        dialog.dismiss();
                    }
                });
    }





    public void onClickTakePic(View view){
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if(Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                file = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", getOutputMediaFile());
            } else {
                file = Uri.fromFile(getOutputMediaFile());
            }
            intent.putExtra(MediaStore.EXTRA_OUTPUT, file);
            startActivityForResult(intent, CAMERA_REQUEST);
    }

    private  File getOutputMediaFile(){
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
                profilepic.setImageURI(resultUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

}


