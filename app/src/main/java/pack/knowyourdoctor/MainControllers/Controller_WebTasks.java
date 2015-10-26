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

import com.google.android.gms.maps.GoogleMap;

import org.json.JSONObject;

import java.util.ArrayList;

import Models.Model_Doctor;
import Models.Model_HospitalLocation;
import Models.Model_RatedDoctor;
import WebServiceAccess.WebTask_DoctorListLoad;
import WebServiceAccess.WebTask_ExecutePostRequests;
import WebServiceAccess.WebTask_GetHTMLContent;
import WebServiceAccess.WebTask_HospitalListLoad;
import WebServiceAccess.WebTask_RatingListLoad;
import WebServiceAccess.WebTask_SearchHospital;
import pack.knowyourdoctor.Adapters.Adapter_Comments;
import pack.knowyourdoctor.Adapters.Adapter_DoctorList;

public class Controller_WebTasks {
    private WebTask_ExecutePostRequests postRequestTask;
    private WebTask_GetHTMLContent getHTMLTask;
    private WebTask_HospitalListLoad hospitalListLoad;
    private WebTask_RatingListLoad ratingListLoadTask;
    private WebTask_SearchHospital searchHospital;
    private WebTask_DoctorListLoad doctorListLoad;

    public Controller_WebTasks() {
        this.postRequestTask = new WebTask_ExecutePostRequests();
        this.getHTMLTask = new WebTask_GetHTMLContent();
        this.hospitalListLoad = new WebTask_HospitalListLoad();
        this.ratingListLoadTask = new WebTask_RatingListLoad();
        this.searchHospital = new WebTask_SearchHospital();
        this.doctorListLoad = new WebTask_DoctorListLoad();
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

    public void executeSearchHospitalTask(Context context, String hospitalName, GoogleMap googleMap) {
        this.searchHospital.setContext(context);
        this.searchHospital.setHospitalName(hospitalName);
        this.searchHospital.setmMap(googleMap);
        this.searchHospital.executeWebTask();
    }

    public void executeDoctorListLoadTask(Context context, ArrayList<Model_Doctor> model_doctors,
                                          Spinner spinner, JSONObject jsonObject, String url) {
        this.doctorListLoad.setContext(context);
        this.doctorListLoad.setDoctorList(model_doctors);
        this.doctorListLoad.setDoctorNamesSpinner(spinner);
        this.doctorListLoad.setjObject(jsonObject);
        this.doctorListLoad.setUrl(url);
        this.doctorListLoad.executeWebTask();
    }

}
