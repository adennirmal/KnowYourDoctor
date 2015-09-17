package pack.knowyourdoctor;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import pack.knowyourdoctor.Validators.EmailValidation;
import pack.knowyourdoctor.Validators.RegNoValidation;

/**
 * Created by Yasi on 9/3/2015.
 */
public class SendMail_Public extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_report_public);

        final EditText emailaddress = (EditText)findViewById(R.id.emailaddress);
        final EditText cc1 = (EditText)findViewById(R.id.cc1);
        final EditText cc2 = (EditText)findViewById(R.id.cc2);
        final EditText cc3 = (EditText)findViewById(R.id.cc3);
        final EditText bcc = (EditText)findViewById(R.id.cc);
        final EditText message_id = (EditText)findViewById(R.id.message_id);
        final EditText message_name = (EditText)findViewById(R.id.message_name);
        final EditText subject = (EditText)findViewById(R.id.subject);
        final EditText message_des = (EditText)findViewById(R.id.message_des);
        final Button sendmail = (Button)findViewById(R.id.btnsendemail);
        sendmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // validation.........................................
                boolean isValidText = true;
                //boolean isValidE = true;
                boolean isValid = true;
                boolean isValidCC = true;

                if ( emailaddress.getText().toString().trim().equals("")) {
                    emailaddress.setError("Please Enter Email");
                    isValidText = false;
                }

                if (isValidText && !EmailValidation.isValidEmail(emailaddress.getText().toString())) {
                    emailaddress.setError("Invalid Email");
                    isValid = false;
                }

                if (!cc1.getText().toString().trim().equals("")) {
                    if (!EmailValidation.isValidEmail(cc1.getText().toString())) {
                        cc1.setError("Invalid Email");
                        isValidCC = false;
                    }
                }

                if (!cc3.getText().toString().trim().equals("")) {
                    if (!EmailValidation.isValidEmail(cc3.getText().toString())) {
                        cc3.setError("Invalid Email");
                        isValidCC = false;
                    }
                }
                if (!cc2.getText().toString().trim().equals("")) {
                    if (!EmailValidation.isValidEmail(cc2.getText().toString())) {
                        cc2.setError("Invalid Email");
                        isValidCC = false;
                    }
                }
                if (!bcc.getText().toString().trim().equals("")) {
                    if (!EmailValidation.isValidEmail(bcc.getText().toString())) {
                        bcc.setError("Invalid Email");
                        isValidCC = false;
                    }
                }
                if(subject.getText().toString().trim().equals(""))
                {
                    subject.setError("Please Enter Subject");
                    isValidText =false;
                }


                if(message_name.getText().toString().trim().equals(""))
                {
                    subject.setError("Please Enter Doctor Name");
                    isValidText =false;
                }
                if (!RegNoValidation.isValidRegNoWithRequiredValidation(message_id.getText().toString())) {
                    message_id.setError("Please enter Doctor Id");
                    isValid = false;
                }
                if(message_des.getText().toString().trim().equals(""))
                {
                    message_des.setError("Please Enter Description");
                    isValidText =false;
                }

                if (isValidText &&isValid && isValidCC) {
                    Thread thread = new Thread(new Runnable() {

                        @Override
                        public void run() {

                            String n1 = emailaddress.getText().toString();
                            String n2_1 = cc1.getText().toString();
                            String n2_2 = cc2.getText().toString();
                            String n2_3 = cc3.getText().toString();
                            String n3 = bcc.getText().toString();
                            String n4 = subject.getText().toString();
                            String n6 = message_id.getText().toString();
                            String n7 = message_name.getText().toString();
                            String n8 = message_des.getText().toString();

                            SenderGmail sender = new SenderGmail("knowyourdoctorslmc@gmail.com", "xc45A&qw7!b1");
                            String newline = System.getProperty("line.separator");
                            //String cc_results = n2_1+n2_2+n2_3;

                            //String cc_results= n2_1,n2_2,n2_3
                            //ArrayList<String> array = new ArrayList<String>();
                            //array.add(n1);
                            //array.add(n2_1);
                            //array.add(n2_2);
                            //array.add(n2_3);
                            //array.add(n3);


                            //String allemailaddress = n1 + cc_results +n3;
                            String message_result = "Doctor Details" + newline +
                                    "---------------------------------"+newline+
                                    "Doctor Reg.No" + n6 + newline +
                                    "Doctor Name" + n7 + newline +
                                    "Description" + n8;
                            // String option = null;
                            // if(option== n1 && option==n2_1 )
                            //{

                            //}

                            //String[] resultsemail = new String[]{n1,n2_1,n2_2,n2_3,n3};

                            //String x =resultsemail.toString();


                            try {
                                sender.sendMail(n4,
                                        message_result,
                                        "yasipiraba@gmail.com", n1,n3, n2_1, n2_2, n2_3
                                );


                            } catch (Exception e) {
                                Log.e("SendMail", e.getMessage(), e);
                                System.out.println(e);
                            }

                        }
                    });
                    thread.start();
                    Toast.makeText(getApplicationContext(), "Submitted Successfully", Toast.LENGTH_LONG).show();
                }
            }

        });









    }
}

