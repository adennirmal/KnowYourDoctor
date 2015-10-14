package pack.knowyourdoctor.Tab_Controllers;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.FacebookDialog;
import com.facebook.widget.LoginButton;
import com.facebook.widget.ProfilePictureView;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import Models.Model_FBUser;
import pack.knowyourdoctor.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class Controller_Fragment_Facebook_Login extends Fragment {

    private LoginButton loginBtn;
    private Button shareBtn, tellFriendBtn;
    ProfilePictureView profile_pic;
    public static String user_name;
    ArrayList<String> user_info;
    Context con;
    SharedPreferences mPrefs;
    private static int RESULT_LOAD_IMG = 1;
    private static int RESULT_CAM_IMG = 0;
    public static final String PREFS_NAME = "PrefsFile";
    String imgDecodableString;
    View rootView;
    ImageView imgView;
    Boolean imageChanged = false;
    Boolean checkImage = false;
    private TextView userName;
    private UiLifecycleHelper uiHelper;

    public Controller_Fragment_Facebook_Login() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // To maintain FB Login session
        uiHelper = new UiLifecycleHelper(getActivity(), statusCallback);
        uiHelper.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        con = getActivity();

        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.view_fragment_facebook_login, container, false);

        mPrefs = this.getActivity().getPreferences(con.MODE_PRIVATE);

        profile_pic = (ProfilePictureView) rootView.findViewById(R.id.profile_pic);
        imgView = (ImageView) rootView.findViewById(R.id.inside_imageview);
        userName = (TextView) rootView.findViewById(R.id.user_name);
        loginBtn = (LoginButton) rootView.findViewById(R.id.fb_login_button);
        loginBtn.setFragment(this);

        imgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(con);
                LayoutInflater inflater = LayoutInflater.from(con);
                View dialogView = inflater.inflate(R.layout.choose_image_dialog, null);
                alertDialogBuilder.setView(dialogView);

                final AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
                Button option_one_btn = (Button) dialogView.findViewById(R.id.option_one);
                option_one_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        profile_pic.setVisibility(View.INVISIBLE);
                        // Create intent to Open Image applications
                        Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        // Start the Intent
                        startActivityForResult(galleryIntent, RESULT_LOAD_IMG);
                        alertDialog.dismiss();
                    }
                });

                Button option_two_btn = (Button) dialogView.findViewById(R.id.option_two);
                option_two_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        profile_pic.setVisibility(View.INVISIBLE);
                        Intent galleryIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(galleryIntent, RESULT_CAM_IMG);
                        alertDialog.dismiss();
                    }
                });
            }
        });

        profile_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(con);
                LayoutInflater inflater = LayoutInflater.from(con);
                View dialogView = inflater.inflate(R.layout.choose_image_dialog, null);
                alertDialogBuilder.setView(dialogView);

                final AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
                Button option_one_btn = (Button) dialogView.findViewById(R.id.option_one);
                option_one_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        profile_pic.setVisibility(View.INVISIBLE);
                        // Create intent to Open Image applications
                        Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        // Start the Intent
                        startActivityForResult(galleryIntent, RESULT_LOAD_IMG);
                        alertDialog.dismiss();
                    }
                });
            }
        });

        shareBtn = (Button) rootView.findViewById(R.id.share_on_fb);
        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (FacebookDialog.canPresentShareDialog(getActivity(), FacebookDialog.ShareDialogFeature.SHARE_DIALOG)) {
                    FacebookDialog shareDialog = new FacebookDialog.ShareDialogBuilder(getActivity())
                            .setName(getResources().getString(R.string.fb_dialog_name))
                            .setCaption(getResources().getString(R.string.fb_dialog_caption))
                            .setLink(getResources().getString(R.string.fb_dialog_link))
                            .setDescription(getResources().getString(R.string.fb_dialog_description))
                            .setPicture(getResources().getString(R.string.fb_dialog_picture))
                            .build();
                    uiHelper.trackPendingDialogCall(shareDialog.present());
                } else {
                    Toast.makeText(con, getResources().getString(R.string.fb_share_dialog_error), Toast.LENGTH_LONG).show();
                }
            }
        });

        tellFriendBtn = (Button) rootView.findViewById(R.id.tell_friends);
        tellFriendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (FacebookDialog.canPresentMessageDialog(getActivity(), FacebookDialog.MessageDialogFeature.MESSAGE_DIALOG)) {
                    FacebookDialog messageDialog = new FacebookDialog.MessageDialogBuilder(getActivity())
                            .setName(getResources().getString(R.string.fb_dialog_name))
                            .setCaption(getResources().getString(R.string.fb_dialog_caption))
                            .setLink(getResources().getString(R.string.fb_dialog_link))
                            .setDescription(getResources().getString(R.string.fb_dialog_description))
                            .setPicture(getResources().getString(R.string.fb_dialog_picture))
                            .build();
                    uiHelper.trackPendingDialogCall(messageDialog.present());
                } else {
                    Toast.makeText(con, getResources().getString(R.string.fb_message_dialog_error), Toast.LENGTH_LONG).show();
                }
            }
        });

        return rootView;
    }

    private Session.StatusCallback statusCallback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            onSessionStateChange(session, state, exception);
            if (state.isOpened()) {
                buttonsEnabled(true);
                Log.d(getResources().getString(R.string.fb_log), getResources().getString(R.string.fb_session_open));
            } else if (state.isClosed()) {
                buttonsEnabled(false);
                Log.d(getResources().getString(R.string.fb_log), getResources().getString(R.string.fb_session_close));
            }
        }
    };

    public void buttonsEnabled(boolean isEnabled) {
        shareBtn.setEnabled(isEnabled);
        tellFriendBtn.setEnabled(isEnabled);
    }

    private void onSessionStateChange(Session session, SessionState state, Exception exception) {
        final SharedPreferences.Editor prefsEditor = mPrefs.edit();
        if (state.isOpened()) {

            Log.d(getResources().getString(R.string.fb_log), getResources().getString(R.string.fb_log_login));
            // make request to the API to get Graph user
            Request.newMeRequest(session, new Request.GraphUserCallback() {
                // callback after Graph API response with user
                @Override
                public void onCompleted(GraphUser user, Response response) {
                    if (user != null) {
                        userName.setText("Hello, " + user.getName());
                        SharedPreferences prefs = con.getSharedPreferences(PREFS_NAME, con.MODE_PRIVATE);
                        String restoredPath = prefs.getString("path", null);
                        if (restoredPath != null) {
                            String path = prefs.getString("path", "");//"" is the default value
                            checkImage = prefs.getBoolean("imageChanged", false);
                            profile_pic.setVisibility(View.INVISIBLE);
                            imgView.setImageURI(Uri.parse(path));
                        }
                        if (imgView.getDrawable() == null) {
                            profile_pic.setVisibility(View.VISIBLE);
                            profile_pic.setDrawingCacheEnabled(true);
                            profile_pic.setProfileId(user.getId());
                        }

                        Model_FBUser fb_user = new Model_FBUser(user.getName(), user.getId());
                        Gson gson = new Gson();
                        String obj = gson.toJson(fb_user);
                        prefsEditor.putString("FbUser", obj);
                        prefsEditor.commit();
                    }
                }
            }).executeAsync();
        } else if (state.isClosed()) {
            prefsEditor.putString("FbUser", null);
            prefsEditor.commit();
            userName.setText(getResources().getString(R.string.fb_not_logged));
            profile_pic.setVisibility(View.INVISIBLE);
            Log.d(getResources().getString(R.string.fb_log), getResources().getString(R.string.fb_log_logout));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        uiHelper.onResume();
        buttonsEnabled(Session.getActiveSession().isOpened());
    }

    @Override
    public void onPause() {
        super.onPause();
        uiHelper.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        uiHelper.onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        uiHelper.onActivityResult(requestCode, resultCode, data);

        uiHelper.onActivityResult(requestCode, resultCode, data,
                new FacebookDialog.Callback() {

                    @Override
                    public void onError(FacebookDialog.PendingCall pendingCall,
                                        Exception error, Bundle data) {
                        Toast.makeText(con, getResources().getString(R.string.fb_activity_result_dialog_error), Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onComplete(
                            FacebookDialog.PendingCall pendingCall, Bundle data) {
                        Toast.makeText(con, "Done!", Toast.LENGTH_LONG).show();
                    }
                });

        try {
            // When an Image is picked
            if (requestCode == RESULT_LOAD_IMG && resultCode == Activity.RESULT_OK && null != data) {
                // Get the Image from data
                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                // Get the cursor
                Cursor cursor = con.getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                // Move to first row
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                imgDecodableString = cursor.getString(columnIndex);
                cursor.close();

                // Set the Image in ImageView after decoding the String
                imgView.setImageBitmap(BitmapFactory.decodeFile(imgDecodableString));
                imageChanged = true;
                //Save to Directory
                imgView.buildDrawingCache();
                Bitmap bm = imgView.getDrawingCache();

                OutputStream fOut = null;
                Uri outputFileUri;
                try {
                    File root = new File(Environment.getExternalStorageDirectory() + File.separator + "KYD Images" + File.separator);
                    root.mkdirs();
                    String filename = imgDecodableString.substring(imgDecodableString.lastIndexOf("/") + 1);
                    File sdImageMainDirectory = new File(root, filename);
                    outputFileUri = Uri.fromFile(sdImageMainDirectory);
                    fOut = new FileOutputStream(sdImageMainDirectory);

                    SharedPreferences.Editor editor = con.getSharedPreferences(PREFS_NAME, con.MODE_PRIVATE).edit();
                    editor.putString("path", outputFileUri.toString());
                    editor.putBoolean("imageChanged", imageChanged);
                    editor.commit();
                } catch (Exception e) {
                    Toast.makeText(con, getResources().getString(R.string.fb_save_image_error), Toast.LENGTH_SHORT).show();
                }

                try {
                    bm.compress(Bitmap.CompressFormat.PNG, 100, fOut);
                    fOut.flush();
                    fOut.close();
                } catch (Exception e) {
                }
            } else if (requestCode == RESULT_CAM_IMG && resultCode == Activity.RESULT_OK && null != data) {
                Bitmap bp = (Bitmap) data.getExtras().get("data");
                imgView.setImageBitmap(bp);
                String path = MediaStore.Images.Media.insertImage(con.getContentResolver(), bp, "Title", null);
                String filename = Uri.parse(path).getLastPathSegment() + ".jpg";
                imageChanged = true;

                //Save to Directory
                imgView.buildDrawingCache();
                Bitmap bm = imgView.getDrawingCache();

                OutputStream fOut = null;
                Uri outputFileUri;
                try {
                    File root = new File(Environment.getExternalStorageDirectory() + File.separator + "KYD Images" + File.separator);
                    root.mkdirs();
                    File sdImageMainDirectory = new File(root, filename);
                    outputFileUri = Uri.fromFile(sdImageMainDirectory);
                    fOut = new FileOutputStream(sdImageMainDirectory);

                    SharedPreferences.Editor editor = con.getSharedPreferences(PREFS_NAME, con.MODE_PRIVATE).edit();
                    editor.putString("path", outputFileUri.toString());
                    editor.putBoolean("imageChanged", imageChanged);
                    editor.commit();

                } catch (Exception e) {
                    Toast.makeText(con, getResources().getString(R.string.fb_save_image_error), Toast.LENGTH_SHORT).show();
                }

                try {
                    bm.compress(Bitmap.CompressFormat.PNG, 100, fOut);
                    fOut.flush();
                    fOut.close();
                } catch (Exception e) {
                }
            } else {
                profile_pic.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            Toast.makeText(con, getResources().getString(R.string.fb_something_went_wrong), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedState) {
        super.onSaveInstanceState(savedState);
        uiHelper.onSaveInstanceState(savedState);
    }
}
