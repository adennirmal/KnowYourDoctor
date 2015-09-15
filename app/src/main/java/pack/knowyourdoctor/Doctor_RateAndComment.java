package pack.knowyourdoctor;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;

/**
 * Created by Aden on 9/6/2015.
 */
public class Doctor_RateAndComment {

    Context context;
    String message;

    public void executeRatingAndCommentTask(Model_Doctor selectedDoctor, float rating, String comment, Context con, String toastMessage) {

        context = con;
        message = toastMessage;

        StringBuilder url = new StringBuilder(con.getResources().getString(R.string.server_link) + "/Rating.php?");
        url.append("doctorid=" + selectedDoctor.getRegNo());
        url.append("&doctorname="+selectedDoctor.getFullName());
        url.append("&doctoraddress="+selectedDoctor.getAddress());
        url.append("&docregdate="+selectedDoctor.getRegDate());
        url.append("&docqualification="+selectedDoctor.getQualifications());
        url.append("&rating="+rating);
        url.append("&commentofdoc="+comment);

        //getDialog().dismiss();

        RatingTask ratingTask = new RatingTask();
        // passes values for the urls string array
        ratingTask.execute(url.toString().replace(" ", "%20"));
    }

    public class RatingTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            HttpClient client = new DefaultHttpClient();
            HttpPost post = new HttpPost(params[0]);
            try {
                client.execute(post);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        }
    }
}
