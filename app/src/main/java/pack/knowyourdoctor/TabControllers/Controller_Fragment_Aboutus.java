package pack.knowyourdoctor.TabControllers;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;

import pack.knowyourdoctor.R;

public class Controller_Fragment_Aboutus extends Fragment {
    View rootView;

    SharedPreferences mPrefs;
    Context con;
    TextView user_name;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.view_fragment_aboutus, container, false);
        addListenerOnRatingBar();

        /*user_name = (TextView) rootView.findViewById(R.id.user_name);

        mPrefs = this.getActivity().getPreferences(con.MODE_PRIVATE);

        Gson gson = new Gson();
        String json = mPrefs.getString("FbUser", "");
        Model_FBUser fb_user = gson.fromJson(json, Model_FBUser.class);
        if (fb_user != null) {
            user_name.setText(fb_user.getFb_user_name());
        }else {
            user_name.setText("Not Logged");
        }*/

        return rootView;
    }

    public void addListenerOnRatingBar() {
        RatingBar ratingBar = (RatingBar) rootView.findViewById(R.id.ratingBar);

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            public void onRatingChanged(RatingBar ratingBar, final float rating, boolean fromUser) {
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                String url = getResources().getString(R.string.server_link) + "/AppRating.php?rating=" + String.valueOf(rating);
                                WebService task = new WebService();
                                // passes values for the urls string array
                                task.execute(url);
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked
                                break;
                        }
                    }
                };
                AlertDialog.Builder builder = new AlertDialog.Builder(rootView.getContext());
                builder.setMessage("Do you like to rate Know Your Doctor?")
                        .setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();
            }
        });
    }

    public class WebService extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            HttpClient client = new DefaultHttpClient();
            HttpPost post = new HttpPost(params[0]);
            try {
                HttpResponse response = client.execute(post);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(rootView.getContext(), getResources().getString(R.string.thanks_for_rating), Toast.LENGTH_SHORT).show();
        }
    }

}
