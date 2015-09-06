package pack.knowyourdoctor;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;

/**
 * Created by Home on 5/17/2015.
 */
public class View_Doctor_Rating extends DialogFragment implements View.OnClickListener {
    View view;
    Model_Doctor selectedDoc;
    Context context;

    public View_Doctor_Rating() {
    }

    public void setArguments(Model_Doctor selectedDoc, Context context) {
        this.selectedDoc = selectedDoc;
        this.context = context;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.view_doctor_rating, container, false);
        Button ratebtn = (Button) view.findViewById(R.id.rateBtn);
        ratebtn.setOnClickListener(this);
        getDialog().setTitle("Rate Dr." + selectedDoc.getFullName());

        //cancel button
        Button cancelbtn = (Button) view.findViewById(R.id.cancelBtn);
        cancelbtn.setOnClickListener(this);

        return view;
    }

    /*Doctor_RateAndComment rate_comment = new Doctor_RateAndComment();
    TextView commentText = (TextView) view.findViewById(R.id.comment);
    String comment = commentText.getText().toString();
    //Rating bar
    RatingBar numberOfStars = (RatingBar) view.findViewById(R.id.doctorRatingBar);
    float rating = numberOfStars.getRating();*/

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rateBtn:
                executeRatingTask(selectedDoc);
                //rate_comment.executeRatingAndCommentTask(selectedDoc, rating,comment,context);
                break;
            case R.id.cancelBtn:
                getDialog().dismiss();
                break;
        }
    }

    private void executeRatingTask(Model_Doctor selectedDoctor) {
        TextView commentText = (TextView) view.findViewById(R.id.comment);
        String comment = commentText.getText().toString();
        //Rating bar
        RatingBar numberOfStars = (RatingBar) view.findViewById(R.id.doctorRatingBar);
        float rating = numberOfStars.getRating();

        StringBuilder url = new StringBuilder("http://sepandroid.esy.es/Rating.php?");
        url.append("doctorid=" + selectedDoctor.getRegNo());
        url.append("&doctorname=" + selectedDoctor.getFullName());
        url.append("&rating=" + rating);
        url.append("&commentofdoc=" + comment);

        getDialog().dismiss();

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
            Toast.makeText(context, "Thanks for rating!!", Toast.LENGTH_SHORT).show();
        }
    }
}
