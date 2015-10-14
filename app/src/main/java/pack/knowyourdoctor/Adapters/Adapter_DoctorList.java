package pack.knowyourdoctor.Adapters;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.util.ArrayList;

import Models.Model_Doctor;
import Models.Model_GlobalValues;
import Models.Model_RatedDoctor;
import ValidationRules.CommentValidation;
import WebServiceAccess.WebTask_RatingListLoad;
import pack.knowyourdoctor.MainControllers.Controller_Call_DocRating;
import pack.knowyourdoctor.MainControllers.Controller_Home;
import pack.knowyourdoctor.R;


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
    int commentsCount;

    public Adapter_DoctorList(Context context, ArrayList<Model_Doctor> doctors) {
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
        Model_Doctor currentDoc = (Model_Doctor) getChild(groupPosition, childPosition);

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
            }
        });

        Button rateBtn = (Button) convertView.findViewById(R.id.rateBTN);
        rateBtn.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                selectedDoctor = searchedDoctors.get(groupPosition);

                final AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                alertDialog.setTitle(context.getResources().getString(R.string.rate_dialog_title));
                alertDialog.setMessage(context.getResources().getString(R.string.rate_dialog_message));
                alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final Dialog ratingDialog = new Dialog(context);

                        ratingDialog.setContentView(R.layout.view_doctor_rating);
                        ratingDialog.setTitle("Your Comment");

                        TextView docNameTV = (TextView) ratingDialog.findViewById(R.id.doctorName);
                        docNameTV.setText("Dr." + selectedDoctor.getFullName());
                        //Rate confirm button
                        Button ratebtn = (Button) ratingDialog.findViewById(R.id.rateBtn);
                        ratebtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //Comment text view
                                TextView commentText = (TextView) ratingDialog.findViewById(R.id.comment);
                                String comment = commentText.getText().toString();

                                String[] Result = CommentValidation.checkComment(comment, R.array.wordsList, R.array.wordsToIgnore, context);
                                commentText.setText(Result[1]);

                                if (Integer.parseInt(Result[0]) == 0) {
                                    Controller_Call_DocRating rate_comment = new Controller_Call_DocRating();
                                    rate_comment.executeRatingAndCommentTask(selectedDoctor, comment, context, context.getResources().getString(R.string.thanks_for_rating));
                                    ratingDialog.dismiss();
                                } else if (Integer.parseInt(Result[0]) == -1) {
                                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                                    alertDialogBuilder.setTitle(context.getResources().getString(R.string.warning));
                                    alertDialogBuilder
                                            .setIcon(R.drawable.warning_icon)
                                            .setMessage(R.string.warning_body)
                                            .setCancelable(false)
                                            .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    dialog.cancel();
                                                }
                                            });
                                    AlertDialog alertDialog = alertDialogBuilder.create();
                                    alertDialog.show();
                                }
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
                alertDialog.setNegativeButton(context.getResources().getString(R.string.LATER), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sendBasicNotification(selectedDoctor);
                    }
                });
                alertDialog.setNeutralButton(context.getResources().getString(R.string.CANCEL), new DialogInterface.OnClickListener() {
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
                alertDialog.setTitle(context.getResources().getString(R.string.location_dialog_title));
                alertDialog.setMessage(context.getResources().getString(R.string.location_dialog_message));
                alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        StringBuilder url = new StringBuilder(context.getResources().getString(R.string.server_link) + "/Location.php?");
                        url.append("doctorid=" + selectedDoctor.getRegNo());
                        url.append("&doctorname=" + selectedDoctor.getFullName());
                        url.append("&doctoraddress=" + selectedDoctor.getAddress());
                        url.append("&docregdate=" + selectedDoctor.getRegDate());
                        url.append("&docqualification=" + selectedDoctor.getQualifications());
                        url.append("&latitude=" + Model_GlobalValues.latitude);
                        url.append("&longtitude=" + Model_GlobalValues.longtitude);

                        JsonReadTask locationTask = new JsonReadTask();
                        // passes values for the urls string array
                        locationTask.execute(url.toString().replace(" ", "%20"));
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
            Toast.makeText(context, context.getResources().getString(R.string.thanks_for_support), Toast.LENGTH_SHORT).show();
        }
    }// end async task

    public void sendBasicNotification(Model_Doctor selectedDoc) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setAutoCancel(true);
        builder.setContentTitle("Rate Dr. " + selectedDoc.getFullName());
        builder.setContentText("Tap to rate");
        builder.setSmallIcon(R.mipmap.appiconimg);

        Intent i = new Intent(this.context, Controller_Home.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this.context);
        stackBuilder.addParentStack(Controller_Home.class);
        i.putExtra("SelectedDoc", (java.io.Serializable) selectedDoc);
        stackBuilder.addNextIntent(i);
        PendingIntent pi_main = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_CANCEL_CURRENT);

        builder.setContentIntent(pi_main);

        Notification notification = builder.build();
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(8, notification);
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

        /*public class RatingTask extends AsyncTask<String, Void, String> {
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
    }*/
}



