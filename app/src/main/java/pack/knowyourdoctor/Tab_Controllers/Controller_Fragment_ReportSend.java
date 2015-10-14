package pack.knowyourdoctor.Tab_Controllers;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import WebServiceAccess.WebTask_ExecutePostRequests;
import pack.knowyourdoctor.R;
import ValidationRules.RegNoValidation;

public class Controller_Fragment_ReportSend extends Fragment {
    Context context;
    private String fakeRegNo;
    EditText dreg;
    String regNo;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.view_fragment_reportsend, container, false);
        context = rootView.getContext();
        dreg = (EditText) rootView.findViewById(R.id.d_reg);

        //String regNo = View_Fragment_DoctorDetails.getRegNo();
        //String regNo;
        if (regNo == null) {
            regNo = "";
        } else {
            try {
                regNo = container.getTag().toString();
            } catch (NullPointerException e) {
                regNo = "";
            }
        }
        dreg.setText(regNo);

        final Button send = (Button) rootView.findViewById(R.id.btnsubmit);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText name = (EditText) rootView.findViewById(R.id.fullname);
                //final EditText nic = (EditText)rootView.findViewById(R.id.nic_no);
                final EditText contact = (EditText) rootView.findViewById(R.id.contact);

                final EditText dname = (EditText) rootView.findViewById(R.id.d_name);
                final EditText comment = (EditText) rootView.findViewById(R.id.comment);
                //final EditText dcontact = (EditText)rootView.findViewById(R.id.d_contact);

                //name.setHint(Html.fromHtml("<font size=\"16\">" + "Your Name" + "</font>"));

                //Validation
                boolean isValid = true;
                //if (RequiredFieldValidation.isEmpty(name.getText().toString())) {
                //  name.setError("Please enter your full name");
                //isValid = false;
                //}

                /*if(!NICValidation.isValidNIC(nic.getText().toString())){
                    nic.setError("Please enter your NIC number");
                    isValid = false;
                }*/

                //if (!ContactNoValidation.isValidContactNo(contact.getText().toString())) {
                //    contact.setError("Please enter your contact number as XXX-XXXXXXX");
                //    isValid = false;
                // }

                if (!RegNoValidation.isValidRegNoWithRequiredValidation(dreg.getText().toString())) {
                    dreg.setError("Please enter fake doctor's registration number");
                    isValid = false;
                }

                //if (RequiredFieldValidation.isEmpty(dname.getText().toString())) {
                //    dname.setError("Please enter fake doctor's name");
                //    isValid = false;
                //}

                /*if(!NICValidation.isValidNIC(dnicno.getText().toString())){
                    dnicno.setError("Please enter fake doctor's NIC number");
                    isValid = false;
                }

                if(RequiredFieldValidation.isEmpty(dcontact.getText().toString())){
                    dcontact.setError("Please enter fake doctor's address");
                    isValid = false;
                }*/

                if (isValid) {
                    final String reportUserName = name.getText().toString();
                    //String reportUserNIC = nic.getText().toString();
                    final String reportContact = contact.getText().toString();

                    final String reportDoctorRegNo = dreg.getText().toString();
                    final String reportDoctorName = dname.getText().toString();
                    final String reportComment = comment.getText().toString();
                    //String reportDoctorAddress = dcontact.getText().toString();
                    //Send email
                    /*
                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            GmailSender sender = new GmailSender("knowyourdoctorslmc@gmail.com", "xc45A&qw7!b1");
                            String newline = System.getProperty("line.separator");
                            StringBuilder emailBody = new StringBuilder("");
                            emailBody.append("Reported User Details" + newline);
                            emailBody.append("------------------------------" + newline);
                            emailBody.append("Fullname:" + reportUserName + newline);
                            //emailBody.append("NIC No:" + reportUserNIC + newline);
                            emailBody.append("Contact No:" + reportContact + newline + newline);

                            emailBody.append("Fake Doctor Details" + newline);
                            emailBody.append("------------------------------" + newline);
                            emailBody.append("Registration No:" + reportDoctorRegNo + newline);
                            emailBody.append("Fullname:" + reportDoctorName + newline);
                            emailBody.append("Comment:" + reportComment + newline);
                            //emailBody.append("Address: " + reportDoctorAddress);
                            try {
                                sender.sendMail("Fake doctor Details", emailBody.toString(),
                                        "knowyourdoctorslmc@gmail.com", "yasipiraba@gmail.com");
                            } catch (Exception e) {
                                Log.e("SendMail", e.getMessage(), e);
                                System.out.println(e);
                            }
                        }
                    });
                    thread.start();
                    */
                    /*
                    String newline = System.getProperty("line.separator");
                    StringBuilder emailBody = new StringBuilder("");
                    emailBody.append("Reported User Details" + newline);
                    emailBody.append("------------------------------" + newline);
                    emailBody.append("Fullname:" + reportUserName + newline);
                    emailBody.append("Contact No:" + reportContact + newline + newline);

                    emailBody.append("Fake Doctor Details" + newline);
                    emailBody.append("------------------------------" + newline);
                    emailBody.append("Registration No:" + reportDoctorRegNo + newline);
                    emailBody.append("Fullname:" + reportDoctorName + newline);
                    emailBody.append("Comment:" + reportComment + newline);

                    Intent i = new Intent(Intent.ACTION_SEND);
                    i.setType("message/rfc822");
                    i.putExtra(Intent.EXTRA_EMAIL, new String[]{"mcsfernando10@gmail.com"});
                    i.putExtra(Intent.EXTRA_SUBJECT, "Fake doctor Details");
                    i.putExtra(Intent.EXTRA_TEXT, emailBody.toString());
                    try {
                        startActivity(Intent.createChooser(i, "Send mail..."));
                    } catch (android.content.ActivityNotFoundException ex) {
                        Toast.makeText(context, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                    }*/

                    //Send fake doctor details to webservice
                    String baseURL = context.getResources().getString(R.string.webserviceLink);
                    StringBuilder url = new StringBuilder(baseURL);
                    url.append("PhoneAppControllers/FakeDoctorReportController/insertFakeDoctorReport/");
                    JSONObject fakeDocJSONObj = new JSONObject();
                    try {
                        fakeDocJSONObj.put("fakeDocID", reportDoctorRegNo);
                        fakeDocJSONObj.put("fakeDocName", reportDoctorName);
                        fakeDocJSONObj.put("reportedPerson", reportUserName);
                        fakeDocJSONObj.put("contactNo", reportContact);
                        fakeDocJSONObj.put("comment", reportComment);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    WebTask_ExecutePostRequests ratingTask = new WebTask_ExecutePostRequests();
                    ratingTask.setContext(context);
                    ratingTask.setMessage("Thanks for your report!!");
                    ratingTask.setjObject(fakeDocJSONObj);
                    // passes values for the urls string array
                    ratingTask.execute(url.toString());
                }

            }
        });
        return rootView;
    }

    @Override
    public void onResume() {
        if (regNo == null) {
            regNo = "";
        }
        dreg.setText(regNo);
        super.onResume();
    }


}