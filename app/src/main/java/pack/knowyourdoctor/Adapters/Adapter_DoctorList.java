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
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import Models.Model_Doctor;
import Models.Model_HospitalLocation;
import Models.Model_RatedDoctor;
import ValidationRules.CommentValidation;
import ValidationRules.RequiredFieldValidation;
import WebServiceAccess.WebTask_ExecutePostRequests;
import WebServiceAccess.WebTask_HospitalListLoad;
import WebServiceAccess.WebTask_RatingListLoad;
import pack.knowyourdoctor.MainControllers.Controller_Call_DocRating;
import pack.knowyourdoctor.MainControllers.Controller_Home;
import pack.knowyourdoctor.R;

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

                                boolean isValid = true;
                                if (RequiredFieldValidation.isEmpty(comment)) {
                                    commentText.setError(context.getResources().getString(R.string.add_comment_to_submit));
                                    isValid = false;
                                }
                                if (isValid == true && Integer.parseInt(Result[0].toString()) == 0) {
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

                final Dialog locationDialog = new Dialog(context);
                locationDialog.setTitle("Submit Medical Center");

                locationDialog.setContentView(R.layout.view_submit_hospital);

                final Spinner spinner = (Spinner) locationDialog.findViewById(R.id.hospitals);
                TextView docNameTV = (TextView) locationDialog.findViewById(R.id.doctorName);
                docNameTV.setText("Dr." + selectedDoctor.getFullName());

                WebTask_HospitalListLoad webTaskHospitalListLoad = new WebTask_HospitalListLoad();

                webTaskHospitalListLoad.setHospitalNamesSpinner(spinner);
                webTaskHospitalListLoad.setContext(context);
                JSONObject currentLocationJSON = new JSONObject();
                try {
                    //Hard coded values
                    currentLocationJSON.put("latitude", 6.9509300000);
                    currentLocationJSON.put("longtitude", 79.8668766000);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                webTaskHospitalListLoad.setjObject(currentLocationJSON);
                final ArrayList<Model_HospitalLocation> hospitals = new ArrayList<Model_HospitalLocation>();
                webTaskHospitalListLoad.setHospitals(hospitals);

                StringBuilder url = new StringBuilder(context.getResources().getString(R.string.webserviceLink));
                url.append("PhoneAppControllers/HospitalListController/getAllHospitals");


                webTaskHospitalListLoad.execute(url.toString());

                Button submitLocationbtn = (Button) locationDialog.findViewById(R.id.submitLocationBtn);
                submitLocationbtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int selectedIndex = spinner.getSelectedItemPosition();
                        Model_HospitalLocation selectedHospital = hospitals.get(selectedIndex);

                        WebTask_ExecutePostRequests submitLocationTask = new WebTask_ExecutePostRequests();
                        StringBuilder url = new StringBuilder(context.getResources().getString(R.string.webserviceLink));
                        url.append("PhoneAppControllers/DoctorLocationController/insertLocation");

                        JSONObject docLocationJSON = new JSONObject();
                        try {
                            docLocationJSON.put("docID", selectedDoctor.getRegNo());
                            docLocationJSON.put("docName", selectedDoctor.getFullName());
                            docLocationJSON.put("docAddress", selectedDoctor.getAddress());
                            docLocationJSON.put("docRegDate", selectedDoctor.getRegDate());
                            docLocationJSON.put("docQualifications", selectedDoctor.getQualifications());
                            docLocationJSON.put("locationID", selectedHospital.getId());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        submitLocationTask.setContext(context);
                        submitLocationTask.setMessage("Thanks for your support");
                        submitLocationTask.setjObject(docLocationJSON);

                        submitLocationTask.execute(url.toString());

                        locationDialog.dismiss();
                    }
                });

                //cancel button
                Button cancelbtn = (Button) locationDialog.findViewById(R.id.cancelBtn);
                cancelbtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        locationDialog.dismiss();
                    }
                });

                locationDialog.show();
            }
        });
        return convertView;
    }

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
}



