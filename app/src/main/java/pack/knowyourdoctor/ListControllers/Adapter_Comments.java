package pack.knowyourdoctor.ListControllers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import LocalDatabase.DBAccess;
import Models.CommentModel;
import WebServiceAccess.WebTask_ExecutePostRequests;
import pack.knowyourdoctor.Constants.Numbers;
import pack.knowyourdoctor.Constants.Strings;
import pack.knowyourdoctor.R;

//Display comments list
public class Adapter_Comments extends BaseAdapter {
    private ArrayList<CommentModel> comments;
    private Context context;
    DBAccess access;

    public Adapter_Comments(Context context, ArrayList<CommentModel> ratedDocComments) {
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
        access = new DBAccess(context, Strings.EMPTY_STRING, null, 0);
        ArrayList<Integer> commentedIds = access.getAllCommentedIds();
        final CommentModel thisComment = comments.get(position);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) context
                    .getSystemService(context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.view_comment, null);
        }
        TextView commentTextView = (TextView) convertView.findViewById(R.id.comment);
        final TextView LikesTextView = (TextView) convertView.findViewById(R.id.likes);

        commentTextView.setText(thisComment.getComment().toString());
        ////LikesTextView.setText("Likes : " + thisComment.getNoOfLikes());
        if (thisComment.getNoOfLikes() == Numbers.ZERO) {
            LikesTextView.setText(context.getResources().getString(R.string.first_to_like));
        } else if (thisComment.getNoOfLikes() == Numbers.ONE) {
            LikesTextView.setText(thisComment.getNoOfLikes() + Strings.LIKE_WITH_SPACE);
        } else {
            LikesTextView.setText(thisComment.getNoOfLikes() + Strings.NO_OF_PEOPLE_TEXT);
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
                if (button.getText().equals(Strings.LIKE)) {
                    isIncrement = true;
                    thisComment.setNoOfLikes(++currentLikes);
                    access.insertCommentID(thisComment.getCommentID());
                    button.setText(Strings.UNLIKE);
                } else {
                    isIncrement = false;
                    thisComment.setNoOfLikes(--currentLikes);
                    access.deleteCommentID(thisComment.getCommentID());
                    button.setText(Strings.LIKE);
                }
                if (currentLikes == 0) {
                    LikesTextView.setText(context.getResources().getString(R.string.first_to_like));
                } else if (currentLikes == 1) {
                    LikesTextView.setText(currentLikes + Strings.LIKE_WITH_SPACE);
                } else {
                    LikesTextView.setText(currentLikes + Strings.NO_OF_PEOPLE_TEXT);
                }

                StringBuilder url = new StringBuilder(
                        context.getResources().getString(R.string.webserviceLink) +
                                Strings.UPDATE_LIKES);
                JSONObject commentDetailsJSONObj = new JSONObject();
                try {
                    commentDetailsJSONObj.put(Strings.COMMENTID, thisComment.getCommentID());
                    commentDetailsJSONObj.put(Strings.IS_INCREMENT, isIncrement);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                WebTask_ExecutePostRequests ratingTask = new WebTask_ExecutePostRequests();
                ratingTask.setContext(context);
                ratingTask.setMessage(Strings.THANKING_TEXT);
                ratingTask.setjObject(commentDetailsJSONObj);
                // passes values for the urls string array
                ratingTask.execute(url.toString());
            }
        });
        return convertView;
    }
}
