package pack.knowyourdoctor.DialogControllers;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import Models.DoctorModel;
import ValidationRules.CommentValidation;
import ValidationRules.RequiredFieldValidation;
import pack.knowyourdoctor.Constants.Numbers;
import pack.knowyourdoctor.Constants.Strings;
import pack.knowyourdoctor.MainControllers.Controller_WebTasks;
import pack.knowyourdoctor.R;

//Doctor Rating Dialog Controller
public class DoctorRatingDialog
        extends DialogFragment
        implements View.OnClickListener {
    View view;
    DoctorModel selectedDoc;
    Context context;

    public void setArguments(DoctorModel selectedDoc, Context context) {
        this.selectedDoc = selectedDoc;
        this.context = context;
    }

    //onCreate method - calls in the initializing the dialog
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.view_doctor_rating, container, false);
        //Set title
        getDialog().setTitle(Strings.RATE_DR + selectedDoc.getFullName());
        //Set doctor name textview text
        TextView docName = (TextView) view.findViewById(R.id.doctorName);
        docName.setText(Strings.DR + selectedDoc.getFullName());
        //Rate button
        Button ratebtn = (Button) view.findViewById(R.id.rateBtn);
        ratebtn.setOnClickListener(this);
        //cancel button
        Button cancelbtn = (Button) view.findViewById(R.id.cancelBtn);
        cancelbtn.setOnClickListener(this);
        return view;
    }

    //Onclick handlers
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rateBtn:
                TextView commentText = (TextView) view.findViewById(R.id.comment);
                String comment = commentText.getText().toString();
                //Bad word validations
                String[] Result = CommentValidation.checkComment(comment, R.array.wordsList,
                        R.array.wordsToIgnore, context);
                commentText.setText(Result[1]);
                commentText.getText().toString().trim();
                //Validate required field
                boolean isValid = true;
                if (RequiredFieldValidation.isEmpty(comment)) {
                    commentText.setError(context.getResources().getString(R.string.add_comment_to_submit));
                    isValid = false;
                }
                if (isValid == true && Integer.parseInt(Result[Numbers.ZERO].toString()) == Numbers.ZERO) {
                    /* If validation pass then insert comment to doctor rating
                        table which is in the web service */
                    String baseURL = context.getResources().getString(R.string.webserviceLink);
                    StringBuilder url = new StringBuilder(baseURL);
                    url.append(Strings.INSERT_DOC_RATING);
                    JSONObject docJSONObj = new JSONObject();
                    try {
                        docJSONObj.put(Strings.JSON_SEND_DOCID,
                                selectedDoc.getRegNo());
                        docJSONObj.put(Strings.JSON_SEND_DOCTOR_NAME,
                                selectedDoc.getFullName());
                        docJSONObj.put(Strings.JSON_SEND_ADDRESS,
                                selectedDoc.getAddress());
                        docJSONObj.put(Strings.JSON_SEND_REG_DATE,
                                selectedDoc.getRegDate());
                        docJSONObj.put(Strings.JSON_SEND_QUALIFICATIONS,
                                selectedDoc.getQualifications());
                        docJSONObj.put(Strings.JSON_SEND_COMMENT, comment);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    //Call relevant async task
                    Controller_WebTasks webTaskController = new Controller_WebTasks();
                    webTaskController.executePostRequestTaks(context,
                            context.getResources().getString(R.string.thanks_for_rating), docJSONObj, url.toString());
                    getDialog().dismiss();
                } else if (Integer.parseInt(Result[Numbers.ZERO]) == Numbers.MINUS_ONE) {
                    //Display bad word warning
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                    alertDialogBuilder.setTitle(Strings.WARNING);
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
                break;
            case R.id.cancelBtn:
                getDialog().dismiss();
                break;
        }
    }
}
