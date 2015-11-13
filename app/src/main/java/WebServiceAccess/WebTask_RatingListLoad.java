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

import Models.CommentModel;
import Models.DoctorModel;
import Models.RatedDoctorModel;
import ValidationRules.CommentValidation;
import ValidationRules.RequiredFieldValidation;
import pack.knowyourdoctor.Constants.Numbers;
import pack.knowyourdoctor.Constants.Strings;
import pack.knowyourdoctor.ListControllers.Adapter_Comments;
import pack.knowyourdoctor.MainControllers.Controller_WebTasks;
import pack.knowyourdoctor.R;

//Retrieve all comments of given doctor
public class WebTask_RatingListLoad
        extends AsyncTask<String, Void, String>
        implements WebTask_Interface {
    private String jsonResult;
    private RatedDoctorModel ratedDoc;
    private Context context;
    private ListView ratedDocComments;
    private Adapter_Comments listAdapter;
    private EditText newComment;
    private TextView commentDoctorTextView;
    private Dialog ratingsDialog;
    private DoctorModel selectedDoctor;
    private String url;

    //Getters and setters
    public void setRatedDoc(RatedDoctorModel ratedDoc) {
        this.ratedDoc = ratedDoc;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void setRatedDocComments(ListView ratedDocComments) {
        this.ratedDocComments = ratedDocComments;
    }

    public void setListAdapter(Adapter_Comments listAdapter) {
        this.listAdapter = listAdapter;
    }

    public void setNewComment(EditText newComment) {
        this.newComment = newComment;
    }

    public void setCommentDoctorTextView(TextView commentDoctorTextView) {
        this.commentDoctorTextView = commentDoctorTextView;
    }

    public void setRatingsDialog(Dialog ratingsDialog) {
        this.ratingsDialog = ratingsDialog;
    }

    public void setSelectedDoctor(DoctorModel selectedDoctor) {
        this.selectedDoctor = selectedDoctor;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    //Run in background thread - Execution of web task
    @Override
    protected String doInBackground(String... params) {
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(params[Numbers.ZERO]);
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

    //Convert Character Stream to StringBuilder object
    private StringBuilder inputStreamToString(InputStream is) {
        String rLine;
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
            JSONArray docDetailNode = jsonResponse.optJSONArray(Strings.JSON_DOC_RATINGS_ARRAY);

            JSONObject jsonDocDetails = docDetailNode.getJSONObject(Numbers.ZERO);

            int doctorID = Integer.parseInt(jsonDocDetails.optString(Strings.JSON_DOCID));
            ratedDoc.setRegNo(doctorID);

            String doctorName = jsonDocDetails.optString(Strings.JSON_DOCTOR_NAME);
            ratedDoc.setFullName(doctorName);

            String doctorAddress = jsonDocDetails.optString(Strings.JSON_ADDRESS);
            ratedDoc.setAddress(doctorAddress);

            String doctorRegDate = jsonDocDetails.optString(Strings.JSON_REG_DATE);
            ratedDoc.setRegDate(doctorRegDate);

            String docQualification = jsonDocDetails.optString(Strings.JSON_QUALIFICATIONS);
            ratedDoc.setQualifications(docQualification);

            /*String topCommentText = jsonDocDetails.optString("topcomment");
            Model_Comment topComment = new Model_Comment();
            topComment.se*/

            JSONArray commentsDetailNode = jsonResponse.optJSONArray(Strings.JSON_COMMENTS_ARRAY);

            ArrayList<CommentModel> commentsOfCurrentDoc = new ArrayList<CommentModel>();
            for (int j = commentsDetailNode.length() - Numbers.ONE; j >= Numbers.ZERO; j--) {
                JSONObject comment = commentsDetailNode.getJSONObject(j);
                CommentModel currentComment = new CommentModel();
                currentComment.setCommentID(Integer.parseInt(
                        comment.optString(Strings.JSON_COMMENT_ID)));
                currentComment.setComment(comment.optString(Strings.JSON_COMMENT));
                currentComment.setNoOfLikes(Integer.parseInt(
                        comment.optString(Strings.JSON_COMMENT_LIKES)));

                commentsOfCurrentDoc.add(currentComment);
            }
            ratedDoc.setComments(commentsOfCurrentDoc);

            ratingsDialog = new Dialog(context);

            ratingsDialog.setContentView(R.layout.view_ratings);
            ratingsDialog.setTitle(Strings.REVIEWS_DOC + selectedDoctor.getFullName());

            ratedDocComments = (ListView) ratingsDialog.findViewById(R.id.RatedDocList);
            listAdapter = new Adapter_Comments(context, ratedDoc.getComments());
            ratedDocComments.setAdapter(listAdapter);

            commentDoctorTextView = (TextView) ratingsDialog.findViewById(
                    R.id.comment_doctor_textView);
            commentDoctorTextView.setText(Strings.DR + selectedDoctor.getFullName());

            TextView noOfComments = (TextView) ratingsDialog.findViewById(R.id.no_of_comments);
            noOfComments.setText(ratedDocComments.getCount() + Strings.COMMENTS);

            ratingsDialog.show();

            Button commentSubmitBtn = (Button) ratingsDialog.findViewById(R.id.comment_submit_btn);
            commentSubmitBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    newComment = (EditText) ratingsDialog.findViewById(R.id.newComment);
                    String comment = newComment.getText().toString().trim();

                    String Result[] = CommentValidation.checkComment(comment,
                            R.array.wordsList, R.array.wordsToIgnore, context);
                    newComment.setText(Result[1]);
                    newComment.getText().toString().trim();

                    boolean isValid = true;
                    if (RequiredFieldValidation.isEmpty(comment)) {
                        newComment.setError(
                                context.getResources().getString(R.string.add_comment_to_submit));
                        isValid = false;
                    }
                    ////Rating bar
                    ////RatingBar numberOfStars = (RatingBar)ratingsDialog.findViewById(R.id.doctorRatingBar);
                    ////float rating = numberOfStars.getRating();
                    if (isValid == true &&
                            Integer.parseInt(Result[Numbers.ZERO].toString()) == Numbers.ZERO) {
                        String baseURL = context.getResources().getString(R.string.webserviceLink);
                        StringBuilder urlToInsertNewRating = new StringBuilder(baseURL);
                        urlToInsertNewRating.append(Strings.INSERT_DOC_RATING);
                        JSONObject docJSONObj = new JSONObject();
                        try {
                            docJSONObj.put(Strings.JSON_SEND_DOCID, selectedDoctor.getRegNo());
                            docJSONObj.put(Strings.JSON_SEND_DOCTOR_NAME,
                                    selectedDoctor.getFullName());
                            docJSONObj.put(Strings.JSON_SEND_ADDRESS, selectedDoctor.getAddress());
                            docJSONObj.put(Strings.JSON_SEND_REG_DATE, selectedDoctor.getRegDate());
                            docJSONObj.put(Strings.JSON_SEND_QUALIFICATIONS,
                                    selectedDoctor.getQualifications());
                            docJSONObj.put(Strings.JSON_SEND_COMMENT, comment);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        //Call relevant async task
                        Controller_WebTasks webTaskController = new Controller_WebTasks();
                        webTaskController.executePostRequestTaks(context,
                                context.getResources().getString(R.string.thanks_for_rating),
                                docJSONObj, urlToInsertNewRating.toString());

                        ratingsDialog.dismiss();

                        //reload the dialog after inserting new comment
                        ratedDoc = new RatedDoctorModel();
                        StringBuilder url = new StringBuilder(context.getResources().getString(R.string.webserviceLink));
                        url.append(Strings.GET_ALL_COMMENTS + selectedDoctor.getRegNo());
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
                    } else if (Integer.parseInt(Result[Numbers.ZERO].toString()) == -1) {
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
            Toast.makeText(context,
                    Strings.NO_COMMENTS_OF_DOC + selectedDoctor.getFullName(),
                    Toast.LENGTH_LONG).show();
        }
    }

    //Method to start execution of current web task
    @Override
    public void executeWebTask() {
        this.execute(url);
    }
}