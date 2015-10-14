package pack.knowyourdoctor.Adapters;

import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import LocalDatabase.DBAccess;
import Models.Model_Comment;
import WebServiceAccess.WebTask_ExecutePostRequests;
import pack.knowyourdoctor.R;

/**
 * Created by Home on 7/30/2015.
 */
public class Adapter_Comments extends BaseAdapter {
    private ArrayList<Model_Comment> comments;
    private Context context;
    DBAccess access;

    public Adapter_Comments(Context context, ArrayList<Model_Comment> ratedDocComments) {
        comments = ratedDocComments;
        this.context = context;
    }

    @Override
    public int getCount() {
        return comments.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        access = new DBAccess(context, "", null, 0);
        ArrayList<Integer> commentedIds = access.getAllCommentedIds();
        final Model_Comment thisComment = (Model_Comment) comments.get(position);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) context
                    .getSystemService(context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.view_comment, null);
        }
        TextView commentTextView = (TextView) convertView.findViewById(R.id.comment);
        final TextView LikesTextView = (TextView) convertView.findViewById(R.id.likes);

        commentTextView.setText(thisComment.getComment().toString());
        ////LikesTextView.setText("Likes : " + thisComment.getNoOfLikes());
        if (thisComment.getNoOfLikes() == 0) {
            LikesTextView.setText(context.getResources().getString(R.string.first_to_like));
        } else if (thisComment.getNoOfLikes() == 1) {
            LikesTextView.setText(thisComment.getNoOfLikes() + " Like");
        } else {
            LikesTextView.setText(thisComment.getNoOfLikes() + " people Like this");
        }
        final Button button = (Button) convertView.findViewById(R.id.likeorUnlikeBTN);

        //Check Like or unlike state current comment
        if (commentedIds.contains(thisComment.getCommentID())) {
            button.setText("UnLike");
        } else {
            button.setText("Like");
        }

        button.setOnClickListener(new ImageButton.OnClickListener() {
            public void onClick(View v) {
                int currentLikes = thisComment.getNoOfLikes();
                boolean isIncrement;
                //Check button text is like or not and set no of likes accordingly
                if (button.getText().equals("Like")) {
                    isIncrement = true;
                    thisComment.setNoOfLikes(++currentLikes);
                    access.insertCommentID(thisComment.getCommentID());
                    button.setText("UnLike");
                } else {
                    isIncrement = false;
                    thisComment.setNoOfLikes(--currentLikes);
                    access.deleteCommentID(thisComment.getCommentID());
                    button.setText("Like");
                }
                if (currentLikes == 0) {
                    LikesTextView.setText(context.getResources().getString(R.string.first_to_like));
                } else if (currentLikes == 1) {
                    LikesTextView.setText(currentLikes + " Like");
                } else {
                    LikesTextView.setText(currentLikes + " people Like this");
                }

                StringBuilder url = new StringBuilder(context.getResources().getString(R.string.webserviceLink) + "PhoneAppControllers/DoctorRatingController/updateLikes/");
                JSONObject commentDetailsJSONObj = new JSONObject();
                try {
                    commentDetailsJSONObj.put("commentID", thisComment.getCommentID());
                    commentDetailsJSONObj.put("isIncrement", isIncrement);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                WebTask_ExecutePostRequests ratingTask = new WebTask_ExecutePostRequests();
                ratingTask.setContext(context);
                ratingTask.setMessage("Thanks for your support!!");
                ratingTask.setjObject(commentDetailsJSONObj);
                // passes values for the urls string array
                ratingTask.execute(url.toString());
            }
        });
        return convertView;
    }
}
