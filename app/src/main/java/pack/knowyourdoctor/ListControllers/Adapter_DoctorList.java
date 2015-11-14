package pack.knowyourdoctor.ListControllers;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v4.app.FragmentActivity;
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

import com.google.android.gms.maps.GoogleMap;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import Models.DoctorModel;
import Models.GPSModel;
import Models.GlobalValueModel;
import Models.HospitalLocationModel;
import Models.RatedDoctorModel;
import ValidationRules.CommentValidation;
import ValidationRules.RequiredFieldValidation;
import pack.knowyourdoctor.Constants.Numbers;
import pack.knowyourdoctor.Constants.Strings;
import pack.knowyourdoctor.MainControllers.Controller_Home;
import pack.knowyourdoctor.MainControllers.Controller_WebTasks;
import pack.knowyourdoctor.R;

//Display doctor list
public class Adapter_DoctorList extends BaseExpandableListAdapter {
    private Context context;
    private ArrayList<DoctorModel> searchedDoctors;
    DoctorModel selectedDoctor;
    RatedDoctorModel ratedDoc;
    ListView ratedDocComments;
    Adapter_Comments listAdapter;
    EditText newComment;
    TextView commentDoctorTextView;
    Dialog ratingsDialog;

    public Adapter_DoctorList(Context context, ArrayList<DoctorModel> doctors) {
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
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView,
                             ViewGroup parent) {
        String docName = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.view_searched_doc, null);
        }

        TextView lblListHeader = (TextView) convertView.findViewById(R.id.searchedDocName);
        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(Strings.DR + docName);

        return convertView;
    }

    @Override
    public View getChildView(final int groupPosition, int childPosition, boolean isLastChild,
                             View convertView, ViewGroup parent) {
        DoctorModel currentDoc = (DoctorModel) getChild(groupPosition, childPosition);

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

        TextView textQualification = (TextView) convertView.findViewById(
                R.id.searchedDocQualification);
        textQualification.setText(currentDoc.getQualifications());

        //ViewRating button
        Button viewRatingBtn = (Button) convertView.findViewById(R.id.viewRatingBTN);
        viewRatingBtn.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                selectedDoctor = searchedDoctors.get(groupPosition);
                ratedDoc = new RatedDoctorModel();
                StringBuilder url = new StringBuilder(
                        context.getResources().getString(R.string.webserviceLink));
                url.append(Strings.GET_ALL_COMMENTS + selectedDoctor.getRegNo());
                //Call relevant async task
                Controller_WebTasks webTaskController = new Controller_WebTasks();
                webTaskController.executeRatingListLoadTask(ratedDoc, context, ratedDocComments,
                        listAdapter, newComment, commentDoctorTextView, ratingsDialog,
                        url.toString(), selectedDoctor);
            }
        });

        Button rateBtn = (Button) convertView.findViewById(R.id.rateBTN);
        rateBtn.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                selectedDoctor = searchedDoctors.get(groupPosition);

                final AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                alertDialog.setTitle(context.getResources().getString(R.string.rate_dialog_title));
                alertDialog.setMessage(context.getResources().getString(R.string.rate_dialog_message));
                alertDialog.setPositiveButton(Strings.YES, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final Dialog ratingDialog = new Dialog(context);

                        ratingDialog.setContentView(R.layout.view_doctor_rating);
                        ratingDialog.setTitle("Your Comment");

                        TextView docNameTV = (TextView) ratingDialog.findViewById(R.id.doctorName);
                        docNameTV.setText(Strings.DR + selectedDoctor.getFullName());
                        //Rate confirm button
                        Button ratebtn = (Button) ratingDialog.findViewById(R.id.rateBtn);
                        ratebtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //Comment text view
                                TextView commentText = (TextView) ratingDialog.findViewById(
                                        R.id.comment);
                                String comment = commentText.getText().toString();

                                String[] Result = CommentValidation.checkComment(comment,
                                        R.array.wordsList, R.array.wordsToIgnore, context);
                                commentText.setText(Result[1]);

                                boolean isValid = true;
                                if (RequiredFieldValidation.isEmpty(comment)) {
                                    commentText.setError(context.getResources().getString(R.string.add_comment_to_submit));
                                    isValid = false;
                                }
                                if (isValid == true && Integer.parseInt(Result[0].toString()) == Numbers.ZERO) {
                                    String baseURL = context.getResources().getString(R.string.webserviceLink);
                                    StringBuilder url = new StringBuilder(baseURL);
                                    url.append(Strings.INSERT_DOC_RATING);
                                    JSONObject docJSONObj = new JSONObject();
                                    try {
                                        docJSONObj.put(Strings.JSON_SEND_DOCID,
                                                selectedDoctor.getRegNo());
                                        docJSONObj.put(Strings.JSON_SEND_DOCTOR_NAME,
                                                selectedDoctor.getFullName());
                                        docJSONObj.put(Strings.JSON_SEND_ADDRESS,
                                                selectedDoctor.getAddress());
                                        docJSONObj.put(Strings.JSON_SEND_REG_DATE,
                                                selectedDoctor.getRegDate());
                                        docJSONObj.put(Strings.JSON_SEND_QUALIFICATIONS,
                                                selectedDoctor.getQualifications());
                                        docJSONObj.put(Strings.JSON_SEND_COMMENT, comment);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    //Call relevant async task
                                    Controller_WebTasks webTaskController = new Controller_WebTasks();
                                    webTaskController.executePostRequestTaks(context,
                                            context.getResources().getString(R.string.thanks_for_rating), docJSONObj, url.toString());


                                    ratingDialog.dismiss();
                                } else if (Integer.parseInt(Result[Numbers.ZERO]) == Numbers.MINUS_ONE) {
                                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                                    alertDialogBuilder.setTitle(context.getResources().getString(R.string.warning));
                                    alertDialogBuilder
                                            .setIcon(R.drawable.warning_icon)
                                            .setMessage(R.string.warning_body)
                                            .setCancelable(false)
                                            .setNegativeButton(Strings.OK, new DialogInterface.OnClickListener() {
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

        final Button locationBtn = (Button) convertView.findViewById(R.id.locationBTN);
        locationBtn.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                selectedDoctor = searchedDoctors.get(groupPosition);

                final Dialog locationDialog = new Dialog(context);
                locationDialog.setTitle(Strings.SUBMIT_MEDICAL_CENTER);

                locationDialog.setContentView(R.layout.view_submit_hospital);

                final Spinner spinner = (Spinner) locationDialog.findViewById(R.id.hospitals);
                TextView docNameTV = (TextView) locationDialog.findViewById(R.id.doctorName);
                /*MapFragment sample (SupportMapFragment)
                MapsInitializer.initialize(context);

                Activity a = (Activity) context;
                //googleMap = ((MapFragment) a.getFragmentManager().findFragmentById(R.id.chooseMapView)).getMap();
                if(googleMap !=  null) {
                    googleMap.setMyLocationEnabled(true);
                }else{
                    System.out.println("Unable to retreive Map");
                }
                if(!RequiredFieldValidation.isEmpty(addHospitalName.getText().toString())){

                }else{
                    addHospitalName.setError(context.getResources().getString(R.string.hospital_name_required));
                }*/

                docNameTV.setText(Strings.DR + selectedDoctor.getFullName());

                JSONObject currentLocationJSON = new JSONObject();
                try {
                    //Load Current Location Coordinates
                    GPSModel modelGps = GPSModel.getInstance();
                    modelGps.getCurrentLocation(context);

                    //Pass Latitude & Longtitude to json object
                    currentLocationJSON.put(Strings.JSON_LATITUDE, GlobalValueModel.latitude);
                    currentLocationJSON.put(Strings.JSON_LONGTITUDE, GlobalValueModel.longtitude);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                final ArrayList<HospitalLocationModel> hospitals = new ArrayList<HospitalLocationModel>();

                StringBuilder url = new StringBuilder(context.getResources().getString(R.string.webserviceLink));
                url.append(Strings.GET_ALL_HOSPITALS);

                //Call relevant async task
                final Controller_WebTasks webTaskController = new Controller_WebTasks();
                webTaskController.executeHospitalListLoadTask(hospitals, spinner, context, currentLocationJSON, url.toString());

                Button submitLocationbtn = (Button) locationDialog.findViewById(R.id.submitLocationBtn);
                submitLocationbtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int selectedIndex = spinner.getSelectedItemPosition();
                        HospitalLocationModel selectedHospital = hospitals.get(selectedIndex);
                        StringBuilder url = new StringBuilder(context.getResources().getString(R.string.webserviceLink));
                        url.append(Strings.INSERT_DOC_LOCATION);

                        JSONObject docLocationJSON = new JSONObject();
                        try {
                            docLocationJSON.put(Strings.JSON_SEND_DOCID, selectedDoctor.getRegNo());
                            docLocationJSON.put(Strings.JSON_SEND_DOCTOR_NAME, selectedDoctor.getFullName());
                            docLocationJSON.put(Strings.JSON_SEND_ADDRESS, selectedDoctor.getAddress());
                            docLocationJSON.put(Strings.JSON_SEND_REG_DATE, selectedDoctor.getRegDate());
                            docLocationJSON.put(Strings.JSON_SEND_QUALIFICATIONS, selectedDoctor.getQualifications());
                            docLocationJSON.put(Strings.JSON_SEND_LOCATION_ID, selectedHospital.getId());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        webTaskController.executePostRequestTaks(context, Strings.THANKING_TEXT, docLocationJSON, url.toString());
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

    public void sendBasicNotification(DoctorModel selectedDoc) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setAutoCancel(true);
        builder.setContentTitle(Strings.RATE_DR + selectedDoc.getFullName());
        builder.setContentText(Strings.TAP_TO_RATE);
        builder.setSmallIcon(R.mipmap.appiconimg);

        Intent i = new Intent(this.context, Controller_Home.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this.context);
        stackBuilder.addParentStack(Controller_Home.class);
        i.putExtra(Strings.SELECTED_DOC_TEXT, selectedDoc);
        stackBuilder.addNextIntent(i);
        PendingIntent pi_main = stackBuilder.getPendingIntent(Numbers.ZERO, PendingIntent.FLAG_CANCEL_CURRENT);

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