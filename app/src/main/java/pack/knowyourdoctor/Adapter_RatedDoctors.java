package pack.knowyourdoctor;

import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Home on 5/2/2015.
 */
public class Adapter_RatedDoctors extends BaseExpandableListAdapter {
    private ArrayList<Model_RatedDoctor> ratedDoctors;
    private Context context;

    public Adapter_RatedDoctors(Context context, ArrayList<Model_RatedDoctor> commentedDoctors) {
        this.context = context;
        ratedDoctors = commentedDoctors;
    }

    @Override
    public int getGroupCount() {
        return this.ratedDoctors.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return ratedDoctors.get(groupPosition).getComments().size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.ratedDoctors.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return ratedDoctors.get(groupPosition).getComments().get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        Model_RatedDoctor currentRatedDoctor = ratedDoctors.get(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.view_rated_doc, null);
        }

        TextView tv = (TextView) convertView.findViewById(R.id.ratedDocName);
        tv.setText("Dr. " + currentRatedDoctor.getFullName());
        return convertView;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final Model_Comment thisComment = (Model_Comment) getChild(groupPosition, childPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) context
                    .getSystemService(context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.view_comment, null);
        }
        TextView commentTextView = (TextView) convertView.findViewById(R.id.comment);
        final TextView LikesTextView = (TextView) convertView.findViewById(R.id.likes);

        commentTextView.setText(thisComment.getComment().toString());
        LikesTextView.setText("Likes : " + thisComment.getNoOfLikes());
        ImageButton button = (ImageButton) convertView.findViewById(R.id.likeorUnlikeBTN);

        final View finalConvertView = convertView;
        button.setOnClickListener(new ImageButton.OnClickListener() {
            public void onClick(View v) {
                StringBuilder url = new StringBuilder(context.getResources().getString(R.string.server_link) + "/Like.php?commentID=");
                //Value
                url.append(thisComment.getCommentID());
                new LikeCommentsTask().execute(url.toString());
                int currentLikes = thisComment.getNoOfLikes();
                thisComment.setNoOfLikes(++currentLikes);
                LikesTextView.setText("Likes : " + currentLikes);

                Toast.makeText(finalConvertView.getContext(), context.getResources().getString(R.string.thanks_for_like), Toast.LENGTH_SHORT).show();
            }
        });
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
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
