package pack.knowyourdoctor;

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

import java.io.IOException;
import java.util.ArrayList;

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
        LikesTextView.setText("Likes : " + thisComment.getNoOfLikes());
        final Button button = (Button) convertView.findViewById(R.id.likeorUnlikeBTN);

        //Check Like or unlike state current comment
        if (commentedIds.contains(thisComment.getCommentID())) {
            button.setText("UnLike");
        } else {
            button.setText("Like");
        }

        final View finalConvertView = convertView;
        button.setOnClickListener(new ImageButton.OnClickListener() {
            public void onClick(View v) {
                int currentLikes = thisComment.getNoOfLikes();
                //Check button text is like or not and set no of likes accordingly
                if (button.getText().equals("Like")) {
                    thisComment.setNoOfLikes(++currentLikes);
                    access.insertCommentID(thisComment.getCommentID());
                    button.setText("UnLike");
                    Toast.makeText(finalConvertView.getContext(), "Thanks for like!", Toast.LENGTH_SHORT).show();
                } else {
                    thisComment.setNoOfLikes(--currentLikes);
                    access.deleteCommentID(thisComment.getCommentID());
                    button.setText("Like");
                }
                LikesTextView.setText("Likes : " + currentLikes);

                StringBuilder url = new StringBuilder("http://sepandroid.esy.es/Like.php?commentID=");
                //Value
                url.append(thisComment.getCommentID());
                url.append("&nOfLikes=" + currentLikes);
                new LikeCommentsTask().execute(url.toString());
            }
        });
        return convertView;
    }

    public class LikeCommentsTask extends AsyncTask<String, Void, String> {
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

        }
    }
}
