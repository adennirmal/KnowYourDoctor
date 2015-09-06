package pack.knowyourdoctor;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;

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

import pack.knowyourdoctor.Validators.RequiredFieldValidation;


/**
 * Created by Nirmal on 3/30/2015.
 */
public class Adapter_DoctorList extends BaseExpandableListAdapter {

    private Context context;
    private ArrayList<Model_Doctor> searchedDoctors;
    Model_Doctor selectedDoctor;
    Model_RatedDoctor ratedDoc;
    ListView ratedDocComments;
    Adapter_Comments listAdapter;
    EditText newComment;
    TextView commentDoctorTextView;
    Dialog ratingsDialog;


    public Adapter_DoctorList(Context context, ArrayList<Model_Doctor> doctors){
        this.context = context;
        searchedDoctors = doctors;
    }

    @Override
    public int getGroupCount() {
        return this.searchedDoctors.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        //one child
        return 1;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.searchedDoctors.get(groupPosition).getFullName();
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return this.searchedDoctors.get(groupPosition);
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
        String docName = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.view_searched_doc, null);
        }

        TextView lblListHeader = (TextView) convertView.findViewById(R.id.searchedDocName);
        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText("Dr. " + docName);

        return convertView;
    }

    @Override
    public View getChildView(final int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        Model_Doctor currentDoc = (Model_Doctor)getChild(groupPosition,childPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.view_searched_docdetails, null);
        }

        TextView textRegNo = (TextView) convertView.findViewById(R.id.searchedDocRegNo);
        textRegNo.setText(String.valueOf(currentDoc.getRegNo()));

        TextView textRegDate = (TextView) convertView.findViewById(R.id.searchedDocRegDate);
        textRegDate.setText(currentDoc.getRegDate());

        TextView textAddress = (TextView) convertView.findViewById(R.id.searchedDocAddress);
        textAddress.setText(currentDoc.getAddress());

        TextView textQualification = (TextView) convertView.findViewById(R.id.searchedDocQualification);
        textQualification.setText(currentDoc.getQualifications());

        //ViewRating button
        Button viewRatingBtn = (Button) convertView.findViewById(R.id.viewRatingBTN);
        viewRatingBtn.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                selectedDoctor = searchedDoctors.get(groupPosition);
                ratedDoc = new Model_RatedDoctor();
                new RatingListLoadTask().execute("http://sepandroid.esy.es/RatedDoctorsWithComments.php?doctorid="+selectedDoctor.getRegNo());
            }
        });

        Button rateBtn = (Button) convertView.findViewById(R.id.rateBTN);
        rateBtn.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                selectedDoctor = searchedDoctors.get(groupPosition);

                final AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                alertDialog.setTitle("Rate Doctor");
                alertDialog.setMessage("Do you want to rate now?");
                alertDialog.setPositiveButton("YES",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final Dialog ratingDialog = new Dialog(context);

                        ratingDialog.setContentView(R.layout.view_doctor_rating);
                        ratingDialog.setTitle("Rate Dr." + selectedDoctor.getFullName());
                        //Rate confirm button
                        Button ratebtn = (Button) ratingDialog.findViewById(R.id.rateBtn);
                        ratebtn.setOnClickListener(new View.OnClickListener(){
                            @Override
                            public void onClick(View v) {
                                //Comment text view
                                TextView commentText=(TextView)ratingDialog.findViewById(R.id.comment);
                                String comment = commentText.getText().toString();
                                //Rating bar
                                RatingBar numberOfStars = (RatingBar)ratingDialog.findViewById(R.id.doctorRatingBar);
                                float rating = numberOfStars.getRating();

                                Doctor_RateAndComment rate_comment = new Doctor_RateAndComment();
                                rate_comment.executeRatingAndCommentTask(selectedDoctor,rating,comment,context,"Thanks for Rating!");
                                ratingDialog.dismiss();

                                /*StringBuilder url = new StringBuilder("http://sepandroid.esy.es/Rating.php?");
                                url.append("doctorid=" + selectedDoctor.getRegNo());
                                url.append("&doctorname="+selectedDoctor.getFullName());
                                url.append("&doctoraddress="+selectedDoctor.getAddress());
                                url.append("&docregdate="+selectedDoctor.getRegDate());
                                url.append("&docqualification="+selectedDoctor.getQualifications());
                                url.append("&rating="+rating);
                                url.append("&commentofdoc="+comment);

                                ratingDialog.dismiss();

                                RatingTask ratingTask = new RatingTask();
                                // passes values for the urls string array
                                ratingTask.execute(url.toString().replace(" ","%20"));*/
                            }
                        });

                        //cancel button
                        Button cancelbtn = (Button) ratingDialog.findViewById(R.id.cancelBtn);
                        cancelbtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ratingDialog.dismiss();
                            }
                        });
                        ratingDialog.show();
                    }
                });
                alertDialog.setNegativeButton("LATER", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sendBasicNotification(selectedDoctor);
                    }
                });
                alertDialog.setNeutralButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                alertDialog.show();
            }
        });

        Button locationBtn = (Button) convertView.findViewById(R.id.locationBTN);
        locationBtn.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                selectedDoctor = searchedDoctors.get(groupPosition);

                final AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                alertDialog.setTitle("Submit Doctor's Location");
                alertDialog.setMessage("Is the Doctor available in your current location ?");
                alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {



                        StringBuilder url = new StringBuilder("http://sepandroid.esy.es/Location.php?");
                        url.append("doctorid=" + selectedDoctor.getRegNo());
                        url.append("&doctorname="+selectedDoctor.getFullName());
                        url.append("&doctoraddress="+selectedDoctor.getAddress());
                        url.append("&docregdate="+selectedDoctor.getRegDate());
                        url.append("&docqualification="+selectedDoctor.getQualifications());
                        url.append("&latitude="+Global_Values.latitude);
                        url.append("&longtitude="+Global_Values.longtitude);


                        JsonReadTask locationTask = new JsonReadTask();
                        // passes values for the urls string array
                        locationTask.execute(url.toString().replace(" ","%20"));
                        //Toast.makeText(context, "Thanks for support", Toast.LENGTH_SHORT).show();
                    }
                });
                alertDialog.setNeutralButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                alertDialog.show();
            }
        });
        return convertView;
    }

    public class RatingListLoadTask extends AsyncTask<String, Void, String> {
        String jsonResult;
        @Override
        protected String doInBackground(String... params) {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(params[0]);
            try {
                HttpResponse response = httpclient.execute(httppost);
                jsonResult = inputStreamToString(response.getEntity().getContent()).toString();
            }
            catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
        private StringBuilder inputStreamToString(InputStream is) {
            String rLine = "";
            StringBuilder questionString = new StringBuilder();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));

            try {
                while ((rLine = rd.readLine()) != null) {
                    questionString.append(rLine);
                }
            }
            catch (IOException e) {
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
                JSONArray jsonMainNode = jsonResponse.optJSONArray("docRatings");

                JSONObject jsonChildNode = jsonMainNode.getJSONObject(0);

                int doctorID = Integer.parseInt(jsonChildNode.optString("doctorID"));
                ratedDoc.setRegNo(doctorID);

                String doctorName = jsonChildNode.optString("doctorName");
                ratedDoc.setFullName(doctorName);

                String doctorAddress = jsonChildNode.optString("docAddress");
                ratedDoc.setAddress(doctorAddress);

                String doctorRegDate = jsonChildNode.optString("docRegDate");
                ratedDoc.setRegDate(doctorRegDate);

                String docQualification = jsonChildNode.optString("docQualification");
                ratedDoc.setQualifications(docQualification);

                Double docAverageRate = Double.parseDouble(jsonChildNode.optString("docAverageRate"));
                ratedDoc.setAverageRating(docAverageRate);

                //Get comments
                JSONArray commentsArray = jsonChildNode.optJSONArray("comments");

                ArrayList<Model_Comment> commentsOfCurrentDoc = new ArrayList<Model_Comment>();
                for (int j = 0; j < commentsArray.length(); j++) {
                    JSONObject comment = commentsArray.getJSONObject(j);
                    Model_Comment currentComment = new Model_Comment();
                    currentComment.setCommentID(Integer.parseInt(comment.optString("commentID")));
                    currentComment.setComment(comment.optString("comment"));
                    currentComment.setNoOfLikes(Integer.parseInt(comment.optString("noOfLikes")));
                    currentComment.setRate(Double.parseDouble(comment.optString("rate")));

                    commentsOfCurrentDoc.add(currentComment);
                }
                ratedDoc.setComments(commentsOfCurrentDoc);

                ratingsDialog = new Dialog(context);

                ratingsDialog.setContentView(R.layout.view_ratings);
                ratingsDialog.setTitle("Reviews: Dr. " + selectedDoctor.getFullName());

                ratedDocComments = (ListView)ratingsDialog.findViewById(R.id.RatedDocList);
                listAdapter = new Adapter_Comments(context, ratedDoc.getComments());
                ratedDocComments.setAdapter(listAdapter);

                commentDoctorTextView = (TextView)ratingsDialog.findViewById(R.id.comment_doctor_textView);
                commentDoctorTextView.setText("Dr. " + selectedDoctor.getFullName());

                ratingsDialog.show();

                Button commentSubmitBtn = (Button)ratingsDialog.findViewById(R.id.comment_submit_btn);
                commentSubmitBtn.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        newComment =(EditText)ratingsDialog.findViewById(R.id.newComment);
                        String comment = newComment.getText().toString();

                        boolean isValid = true;
                        if (RequiredFieldValidation.isEmpty(comment)){
                            newComment.setError("Please add a comment to submit");
                            isValid = false;
                        }
                        //Rating bar
                        //RatingBar numberOfStars = (RatingBar)ratingsDialog.findViewById(R.id.doctorRatingBar);
                        //float rating = numberOfStars.getRating();
                        if(isValid) {
                            Doctor_RateAndComment rate_comment = new Doctor_RateAndComment();
                            rate_comment.executeRatingAndCommentTask(selectedDoctor, 0.0f, comment, context, "Thanks for the comment!");
                            ratingsDialog.dismiss();
                            new RatingListLoadTask().execute("http://sepandroid.esy.es/RatedDoctorsWithComments.php?doctorid=" + selectedDoctor.getRegNo());
                        }
                    }
                });

            } catch (JSONException e) {
                Toast.makeText(context, "No Ratings for Dr. "+selectedDoctor.getFullName(), Toast.LENGTH_LONG).show();
            }
        }
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

    // Async Task to access the web
    private class JsonReadTask extends AsyncTask<String, Void, String> {
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
            Toast.makeText(context, "Thanks for support", Toast.LENGTH_SHORT).show();
        }
    }// end async task

    public void sendBasicNotification(Model_Doctor selectedDoc){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setAutoCancel(true);
        builder.setContentTitle("Rate Dr. " + selectedDoc.getFullName());
        builder.setContentText("Tap to rate");
        builder.setSmallIcon(R.mipmap.appiconimg);

        Intent i = new Intent(this.context,View_Home.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this.context);
        stackBuilder.addParentStack(View_Home.class);
        i.putExtra("SelectedDoc", (java.io.Serializable) selectedDoc);
        stackBuilder.addNextIntent(i);
        PendingIntent pi_main = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_CANCEL_CURRENT);

        builder.setContentIntent(pi_main);

        Notification notification = builder.build();
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(8,notification);
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}


