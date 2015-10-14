package WebServiceAccess;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import Models.Model_Comment;
import Models.Model_Doctor;
import Models.Model_RatedDoctor;
import ValidationRules.CommentValidation;
import ValidationRules.RequiredFieldValidation;
import pack.knowyourdoctor.Adapters.Adapter_Comments;
import pack.knowyourdoctor.MainControllers.Controller_Call_DocRating;
import pack.knowyourdoctor.R;

public class WebTask_RatingListLoad extends AsyncTask<String, Void, String> {
    private String jsonResult;
    private Model_RatedDoctor ratedDoc;
    private Context context;
    private ListView ratedDocComments;
    private Adapter_Comments listAdapter;
    private EditText newComment;
    private TextView commentDoctorTextView;
    private Dialog ratingsDialog;
    private Model_Doctor selectedDoctor;

    public String getJsonResult() {
        return jsonResult;
    }

    public void setJsonResult(String jsonResult) {
        this.jsonResult = jsonResult;
    }

    public Model_RatedDoctor getRatedDoc() {
        return ratedDoc;
    }

    public void setRatedDoc(Model_RatedDoctor ratedDoc) {
        this.ratedDoc = ratedDoc;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public ListView getRatedDocComments() {
        return ratedDocComments;
    }

    public void setRatedDocComments(ListView ratedDocComments) {
        this.ratedDocComments = ratedDocComments;
    }

    public Adapter_Comments getListAdapter() {
        return listAdapter;
    }

    public void setListAdapter(Adapter_Comments listAdapter) {
        this.listAdapter = listAdapter;
    }

    public EditText getNewComment() {
        return newComment;
    }

    public void setNewComment(EditText newComment) {
        this.newComment = newComment;
    }

    public TextView getCommentDoctorTextView() {
        return commentDoctorTextView;
    }

    public void setCommentDoctorTextView(TextView commentDoctorTextView) {
        this.commentDoctorTextView = commentDoctorTextView;
    }

    public Dialog getRatingsDialog() {
        return ratingsDialog;
    }

    public void setRatingsDialog(Dialog ratingsDialog) {
        this.ratingsDialog = ratingsDialog;
    }

    public Model_Doctor getSelectedDoctor() {
        return selectedDoctor;
    }

    public void setSelectedDoctor(Model_Doctor selectedDoctor) {
        this.selectedDoctor = selectedDoctor;
    }

    public int getCommentsCount() {
        return commentsCount;
    }

    public void setCommentsCount(int commentsCount) {
        this.commentsCount = commentsCount;
    }

    private int commentsCount;

    @Override
    protected String doInBackground(String... params) {
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(params[0]);
        try {
            HttpResponse response = httpclient.execute(httppost);
            jsonResult = inputStreamToString(response.getEntity().getContent()).toString();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return jsonResult;
    }

    private StringBuilder inputStreamToString(InputStream is) {
        String rLine = "";
        StringBuilder questionString = new StringBuilder();
        BufferedReader rd = new BufferedReader(new InputStreamReader(is));

        try {
            while ((rLine = rd.readLine()) != null) {
                questionString.append(rLine);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return questionString;
    }

    //commented doctor list preparation
    @Override
    protected void onPostExecute(String result) {
        try {
            //get rated doctor details
            JSONObject jsonResponse = new JSONObject(jsonResult);
            JSONArray docDetailNode = jsonResponse.optJSONArray("docRatings");

            JSONObject jsonDocDetails = docDetailNode.getJSONObject(0);

            int doctorID = Integer.parseInt(jsonDocDetails.optString("docid"));
            ratedDoc.setRegNo(doctorID);

            String doctorName = jsonDocDetails.optString("docname");
            ratedDoc.setFullName(doctorName);

            String doctorAddress = jsonDocDetails.optString("address");
            ratedDoc.setAddress(doctorAddress);

            String doctorRegDate = jsonDocDetails.optString("regdate");
            ratedDoc.setRegDate(doctorRegDate);

            String docQualification = jsonDocDetails.optString("qualifications");
            ratedDoc.setQualifications(docQualification);

            JSONArray commentsDetailNode = jsonResponse.optJSONArray("comments");

            ArrayList<Model_Comment> commentsOfCurrentDoc = new ArrayList<Model_Comment>();
            for (int j = commentsDetailNode.length() - 1; j >= 0; j--) {
                JSONObject comment = commentsDetailNode.getJSONObject(j);
                Model_Comment currentComment = new Model_Comment();
                currentComment.setCommentID(Integer.parseInt(comment.optString("commentid")));
                currentComment.setComment(comment.optString("comment"));
                currentComment.setNoOfLikes(Integer.parseInt(comment.optString("commentlikes")));

                commentsOfCurrentDoc.add(currentComment);
            }
            ratedDoc.setComments(commentsOfCurrentDoc);

            ratingsDialog = new Dialog(context);

            ratingsDialog.setContentView(R.layout.view_ratings);
            ratingsDialog.setTitle("Reviews: Dr. " + selectedDoctor.getFullName());

            ratedDocComments = (ListView) ratingsDialog.findViewById(R.id.RatedDocList);
            listAdapter = new Adapter_Comments(context, ratedDoc.getComments());
            ratedDocComments.setAdapter(listAdapter);

            commentDoctorTextView = (TextView) ratingsDialog.findViewById(R.id.comment_doctor_textView);
            commentDoctorTextView.setText("Dr. " + selectedDoctor.getFullName());

            TextView noOfComments = (TextView) ratingsDialog.findViewById(R.id.no_of_comments);
            noOfComments.setText(ratedDocComments.getCount() + " comments");

            ratingsDialog.show();

            Button commentSubmitBtn = (Button) ratingsDialog.findViewById(R.id.comment_submit_btn);
            commentSubmitBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    newComment = (EditText) ratingsDialog.findViewById(R.id.newComment);
                    String comment = newComment.getText().toString().trim();

                    String Result[] = CommentValidation.checkComment(comment, R.array.wordsList, R.array.wordsToIgnore, context);
                    newComment.setText(Result[1]);
                    newComment.getText().toString().trim();

                    boolean isValid = true;
                    if (RequiredFieldValidation.isEmpty(comment)) {
                        newComment.setError(context.getResources().getString(R.string.add_comment_to_submit));
                        isValid = false;
                    }
                    ////Rating bar
                    ////RatingBar numberOfStars = (RatingBar)ratingsDialog.findViewById(R.id.doctorRatingBar);
                    ////float rating = numberOfStars.getRating();
                    if (isValid == true && Integer.parseInt(Result[0].toString()) == 0) {
                        Controller_Call_DocRating rate_comment = new Controller_Call_DocRating();
                        rate_comment.executeRatingAndCommentTask(selectedDoctor, comment, context, context.getResources().getString(R.string.thanks_for_the_comment));
                        ratingsDialog.dismiss();

                        //reload the dialog after inserting new comment
                        ratedDoc = new Model_RatedDoctor();
                        StringBuilder url = new StringBuilder(context.getResources().getString(R.string.webserviceLink));
                        url.append("PhoneAppControllers/DoctorRatingController/getAllCommentsOfDoc/" + selectedDoctor.getRegNo());
                        WebTask_RatingListLoad ratingListLoad = new WebTask_RatingListLoad();
                        ratingListLoad.setContext(context);
                        ratingListLoad.setRatedDoc(ratedDoc);
                        ratingListLoad.setSelectedDoctor(selectedDoctor);
                        ratingListLoad.setRatingsDialog(ratingsDialog);
                        ratingListLoad.setRatedDocComments(ratedDocComments);
                        ratingListLoad.setCommentDoctorTextView(commentDoctorTextView);
                        ratingListLoad.setListAdapter(listAdapter);
                        ratingListLoad.setNewComment(newComment);
                        ratingListLoad.execute(url.toString());
                    } else if (Integer.parseInt(Result[0].toString()) == -1) {
                        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                        LayoutInflater inflater = LayoutInflater.from(context);
                        final View dialogView = inflater.inflate(R.layout.warning_alert_dialog, null);
                        alertDialogBuilder.setView(dialogView);
                        final AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.show();
                        Button ok_btn = (Button) dialogView.findViewById(R.id.ok_btn);
                        ok_btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                alertDialog.dismiss();
                            }
                        });
                    }
                }
            });
        } catch (JSONException e) {
            Toast.makeText(context, "No Comments about Dr. " + selectedDoctor.getFullName(), Toast.LENGTH_LONG).show();
        }
    }
}