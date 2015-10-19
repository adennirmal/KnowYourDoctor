package pack.knowyourdoctor.MainControllers;

import android.app.Dialog;
import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.ArrayList;

import Models.Model_Doctor;
import Models.Model_HospitalLocation;
import Models.Model_RatedDoctor;
import WebServiceAccess.WebTask_ExecutePostRequests;
import WebServiceAccess.WebTask_GetHTMLContent;
import WebServiceAccess.WebTask_HospitalListLoad;
import WebServiceAccess.WebTask_RatingListLoad;
import pack.knowyourdoctor.Adapters.Adapter_Comments;
import pack.knowyourdoctor.Adapters.Adapter_DoctorList;

public class Controller_WebTasks {
    private WebTask_ExecutePostRequests postRequestTask;
    private WebTask_GetHTMLContent getHTMLTask;
    private WebTask_HospitalListLoad hospitalListLoad;
    private WebTask_RatingListLoad ratingListLoadTask;

    public Controller_WebTasks() {
        this.postRequestTask = new WebTask_ExecutePostRequests();
        this.getHTMLTask = new WebTask_GetHTMLContent();
        this.hospitalListLoad = new WebTask_HospitalListLoad();
        this.ratingListLoadTask = new WebTask_RatingListLoad();
    }

    public void executePostRequestTaks(Context context, String message, JSONObject jObject, String url) {
        this.postRequestTask.setContext(context);
        this.postRequestTask.setMessage(message);
        this.postRequestTask.setjObject(jObject);
        this.postRequestTask.setUrl(url);
        this.postRequestTask.executeWebTask();
    }

    public void executeGetHTMLTask(ArrayList<Model_Doctor> searchedDoctors,
                                   ProgressBar pBar, TextView txt, Adapter_DoctorList listAdapter,
                                   EditText regNoTE, Context context, String searchedRegNo,
                                   FragmentActivity currentActivity, LinearLayout linearLayoutView,
                                   ArrayList<String> urls) {
        this.getHTMLTask.setSearchedDoctors(searchedDoctors);
        this.getHTMLTask.setpBar(pBar);
        this.getHTMLTask.setTxt(txt);
        this.getHTMLTask.setListAdapter(listAdapter);
        this.getHTMLTask.setRegNoTE(regNoTE);
        this.getHTMLTask.setContext(context);
        this.getHTMLTask.setSearchedRegNo(searchedRegNo);
        this.getHTMLTask.setCurrentActivity(currentActivity);
        this.getHTMLTask.setLinearLayoutView(linearLayoutView);
        this.getHTMLTask.setUrls(urls);
        this.getHTMLTask.executeWebTask();
    }

    public void executeHospitalListLoadTask(ArrayList<Model_HospitalLocation> hospitals,
                                            Spinner hospitalNamesSpinner,
                                            Context context, JSONObject jObject, String url) {
        this.hospitalListLoad.setHospitals(hospitals);
        this.hospitalListLoad.setjObject(jObject);
        this.hospitalListLoad.setUrl(url);
        this.hospitalListLoad.setContext(context);
        this.hospitalListLoad.setHospitalNamesSpinner(hospitalNamesSpinner);
        this.hospitalListLoad.executeWebTask();
    }

    public void executeRatingListLoadTask(Model_RatedDoctor ratedDoc,
                                          Context context, ListView ratedDocComments,
                                          Adapter_Comments listAdapter, EditText newComment,
                                          TextView commentDoctorTextView, Dialog ratingsDialog,
                                          String url, Model_Doctor selectedDoctor) {
        this.ratingListLoadTask.setRatedDoc(ratedDoc);
        this.ratingListLoadTask.setContext(context);
        this.ratingListLoadTask.setListAdapter(listAdapter);
        this.ratingListLoadTask.setNewComment(newComment);
        this.ratingListLoadTask.setCommentDoctorTextView(commentDoctorTextView);
        this.ratingListLoadTask.setRatingsDialog(ratingsDialog);
        this.ratingListLoadTask.setUrl(url);
        this.ratingListLoadTask.setSelectedDoctor(selectedDoctor);
        this.ratingListLoadTask.executeWebTask();
    }


}
