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
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
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
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import Models.Model_Doctor;
import Models.Model_GlobalValues;
import Models.Model_HospitalLocation;
import Models.Model_RatedDoctor;
import ValidationRules.CommentValidation;
import ValidationRules.RequiredFieldValidation;
import pack.knowyourdoctor.LocationListenerClass;
import pack.knowyourdoctor.MainControllers.Controller_Home;
import pack.knowyourdoctor.MainControllers.Controller_WebTasks;
import pack.knowyourdoctor.R;

public class Adapter_DoctorList extends BaseExpandableListAdapter implements LocationListener {

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
                //Call relevant async task
                Controller_WebTasks webTaskController = new Controller_WebTasks();
                webTaskController.executeRatingListLoadTask(ratedDoc, context, ratedDocComments, listAdapter, newComment, commentDoctorTextView, ratingsDialog, url.toString(), selectedDoctor);
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
                                    String baseURL = context.getResources().getString(R.string.webserviceLink);
                                    StringBuilder url = new StringBuilder(baseURL);
                                    url.append("PhoneAppControllers/DoctorRatingController/insertNewRating/");
                                    JSONObject docJSONObj = new JSONObject();
                                    try {
                                        docJSONObj.put("docID", selectedDoctor.getRegNo());
                                        docJSONObj.put("docName", selectedDoctor.getFullName());
                                        docJSONObj.put("docAddress", selectedDoctor.getAddress());
                                        docJSONObj.put("docRegDate", selectedDoctor.getRegDate());
                                        docJSONObj.put("docQualifications", selectedDoctor.getQualifications());
                                        docJSONObj.put("comment", comment);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    //Call relevant async task
                                    Controller_WebTasks webTaskController = new Controller_WebTasks();
                                    webTaskController.executePostRequestTaks(context,
                                            context.getResources().getString(R.string.thanks_for_rating), docJSONObj, url.toString());


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

                JSONObject currentLocationJSON = new JSONObject();
                try {
                    //Load Current Location Coordinates
                    // Getting LocationManager object
                    LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

                    if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                        buildAlertMessageNoGps();
                    }

                    // Creating an empty criteria object
                    Criteria criteria = new Criteria();

                    // Getting the name of the provider that meets the criteria
                    String provider = locationManager.getBestProvider(criteria, false);

                    if (provider != null && !provider.equals("")) {
                        // Get the location from the given provider
                        Location location = locationManager.getLastKnownLocation(provider);
                        LocationListenerClass a = new LocationListenerClass(context);
                        locationManager.requestLocationUpdates(provider, 20000, 1, a);

                        if (location != null)
                            onLocationChanged(location);
                        else
                            Toast.makeText(context, "Location can't be retrieved", Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(context, "No Provider Found", Toast.LENGTH_SHORT).show();
                    }
                    //Hard coded values
                    currentLocationJSON.put("latitude", Model_GlobalValues.latitude);
                    currentLocationJSON.put("longtitude", Model_GlobalValues.longtitude);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                final ArrayList<Model_HospitalLocation> hospitals = new ArrayList<Model_HospitalLocation>();

                StringBuilder url = new StringBuilder(context.getResources().getString(R.string.webserviceLink));
                url.append("PhoneAppControllers/HospitalListController/getAllHospitals");

                //Call relevant async task
                final Controller_WebTasks webTaskController = new Controller_WebTasks();
                webTaskController.executeHospitalListLoadTask(hospitals, spinner, context, currentLocationJSON, url.toString());

                Button submitLocationbtn = (Button) locationDialog.findViewById(R.id.submitLocationBtn);
                submitLocationbtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int selectedIndex = spinner.getSelectedItemPosition();
                        Model_HospitalLocation selectedHospital = hospitals.get(selectedIndex);
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
                        webTaskController.executePostRequestTaks(context, "Thanks for your support", docLocationJSON, url.toString());
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

    @Override
    public void onLocationChanged(Location location) {
        Model_GlobalValues.latitude = location.getLatitude();
        Model_GlobalValues.longtitude = location.getLongitude();

        Toast.makeText(context, "Loading Coordinates", Toast.LENGTH_LONG);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    //GPS validator method
    public void buildAlertMessageNoGps() {

    }
}



