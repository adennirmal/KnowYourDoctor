package pack.knowyourdoctor.MainControllers;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import Models.Model_Doctor;
import ValidationRules.CommentValidation;
import ValidationRules.RequiredFieldValidation;
import pack.knowyourdoctor.R;

/**
 * Created by Home on 5/17/2015.
 */
public class Controller_Doctor_Rating extends DialogFragment implements View.OnClickListener {
    View view;
    Model_Doctor selectedDoc;
    Context context;

    public Controller_Doctor_Rating() {
    }

    public void setArguments(Model_Doctor selectedDoc, Context context) {
        this.selectedDoc = selectedDoc;
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.view_doctor_rating, container, false);
        Button ratebtn = (Button) view.findViewById(R.id.rateBtn);
        ratebtn.setOnClickListener(this);
        getDialog().setTitle("Rate Dr." + selectedDoc.getFullName());

        //cancel button
        Button cancelbtn = (Button) view.findViewById(R.id.cancelBtn);
        cancelbtn.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rateBtn:
                TextView commentText = (TextView) view.findViewById(R.id.comment);
                String comment = commentText.getText().toString();
                //executeRatingTask(selectedDoc);
                String[] Result = CommentValidation.checkComment(comment, R.array.wordsList, R.array.wordsToIgnore, context);
                commentText.setText(Result[1]);
                commentText.getText().toString().trim();
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
                        docJSONObj.put("docID", selectedDoc.getRegNo());
                        docJSONObj.put("docName", selectedDoc.getFullName());
                        docJSONObj.put("docAddress", selectedDoc.getAddress());
                        docJSONObj.put("docRegDate", selectedDoc.getRegDate());
                        docJSONObj.put("docQualifications", selectedDoc.getQualifications());
                        docJSONObj.put("comment", comment);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    //Call relevant async task
                    Controller_WebTasks webTaskController = new Controller_WebTasks();
                    webTaskController.executePostRequestTaks(context,
                            context.getResources().getString(R.string.thanks_for_rating), docJSONObj, url.toString());
                    getDialog().dismiss();
                } else if (Integer.parseInt(Result[0]) == -1) {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                    alertDialogBuilder.setTitle("Warning!");
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
                break;
            case R.id.cancelBtn:
                getDialog().dismiss();
                break;
        }
    }
}
