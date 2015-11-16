package pack.knowyourdoctor.MainControllers;

import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.ActionBar;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ogaclejapan.arclayout.ArcLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import Models.DoctorModel;
import Services.InternetCheck;
import WebServiceAccess.WebTask_ExecutePostRequests;
import pack.knowyourdoctor.AnimationControllers.Animator;
import pack.knowyourdoctor.Constants.Numbers;
import pack.knowyourdoctor.Constants.Strings;
import pack.knowyourdoctor.DialogControllers.DoctorRatingDialog;
import pack.knowyourdoctor.R;
import pack.knowyourdoctor.TabControllers.Tab_Controller;

public class Controller_Home extends FragmentActivity implements View.OnClickListener,
        ViewPager.OnPageChangeListener, ActionBar.TabListener {
    private Context context;
    View arcLayouCtrl;
    View menuLayout;
    ArcLayout arcLayout;
    ViewPager viewDisplay;
    Button docDetailsBTN;
    Button reportFakeDocBTN;
    Button locationDetailBTN;
    Button faceBookShareBTN;
    private Tab_Controller mAdapter;
    private ActionBar actionBar;
    Dialog fragmentsDialog;
    Button lastSelectedButton;

    //Tab images
    final int[] icons = {
            R.drawable.search_selector,
            R.drawable.location_selector,
            R.drawable.report_selector,
            R.drawable.fb_selector
    };

    //onCreate method - calls in the initializing the page
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getApplicationContext();
        setContentView(R.layout.view_home);

        //prevent app stop
        /*urlBundle = new Bundle();
        String url = getResources().getString(R.string.slmc_url);
        urlBundle.putString("url", url);*/

        //Set up arc layout
        arcLayouCtrl = findViewById(R.id.arcLayoutCtrlBtn);
        menuLayout = findViewById(R.id.menuLayout);
        arcLayout = (ArcLayout) findViewById(R.id.arcLayout);
        //Define arc layout buttons
        docDetailsBTN = (Button) findViewById(R.id.docDetailsBtn);
        reportFakeDocBTN = (Button) findViewById(R.id.reportFakeDocBtn);
        locationDetailBTN = (Button) findViewById(R.id.locationDetailsBtn);
        faceBookShareBTN = (Button) findViewById(R.id.facebookShareBtn);
        lastSelectedButton = docDetailsBTN;
        docDetailsBTN.setBackgroundResource(R.drawable.light_blue_oval_selector);
        //Set click event to arc layout buttons
        menuLayout.setOnClickListener(this);
        for (int i = 0, size = arcLayout.getChildCount(); i < size; i++) {
            arcLayout.getChildAt(i).setOnClickListener(this);
        }
        arcLayouCtrl.setOnClickListener(this);

        //Set up tabs
        viewDisplay = (ViewPager) findViewById(R.id.fragmentViewer);
        actionBar = getActionBar();
        mAdapter = new Tab_Controller(getSupportFragmentManager());
        viewDisplay.setAdapter(mAdapter);
        actionBar.setHomeButtonEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        viewDisplay.setAdapter(mAdapter);
        //Set up images to respective tabs
        for (int i = 0; i < icons.length; i++) {
            actionBar.addTab(actionBar.newTab().setIcon(icons[i]).setTabListener(this));
        }
        //Detect slide event
        viewDisplay.setOnPageChangeListener(this);

        //Handle rate later option of the app
        if (this.getIntent().hasExtra(Strings.SELECTED_DOC_TEXT)) {
            DoctorModel selectedDoctor = (DoctorModel) this.getIntent().getSerializableExtra(Strings.SELECTED_DOC_TEXT);
            DoctorRatingDialog ratingDialog = new DoctorRatingDialog();
            ratingDialog.setArguments(selectedDoctor, context);
            ratingDialog.show(getSupportFragmentManager(), null);
        }

        /*  Load Coordinates
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE); // Getting LocationManager object

        Criteria criteria = new Criteria();// Creating an empty criteria object

        provider = locationManager.getBestProvider(criteria, false);// Getting the name of the provider that meets the criteria

        if (provider != null && !provider.equals("")) {

            // Get the location from the given provider
            location = locationManager.getLastKnownLocation(provider);

            locationManager.requestLocationUpdates(provider, 20000, 1, this);

            if (location != null)
                onLocationChanged(location);

        } else {
            Toast.makeText(context, getResources().getString(R.string.no_provider), Toast.LENGTH_SHORT).show();
        }*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_view_home, menu);
        return true;
    }

    //Menu items selection
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            //About SLMC dialog box
            case R.id.view_fragment_slmc:
                fragmentsDialog = new Dialog(Controller_Home.this);
                fragmentsDialog.setTitle(Strings.ABOUT_SLMC_TITLE);
                fragmentsDialog.setContentView(R.layout.view_fragment_slmc);

                WebView aboutSLMCWebView = (WebView) fragmentsDialog.findViewById(R.id.slmcDetails);
                aboutSLMCWebView.setBackgroundColor(Color.TRANSPARENT);

                //Display about SLMC Text
                StringBuilder textAboutSLMC = new StringBuilder(Strings.HTML_STYLE);
                textAboutSLMC.append(getString(R.string.about_SLMC));
                textAboutSLMC.append(Strings.HTML_REST);

                aboutSLMCWebView.loadData(textAboutSLMC.toString(), Strings.DOC_TYPE, Strings.TEXT_TYPE);

                //Set URL link to SLMC Website
                TextView urlView = (TextView) fragmentsDialog.findViewById(R.id.slmcURL);
                urlView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(Strings.SLMC_SITE_URL));
                        startActivity(browserIntent);

                    }
                });

                fragmentsDialog.show();
                break;
            //About Us dialog box
            case R.id.view_fragment_aboutUs:
                fragmentsDialog = new Dialog(Controller_Home.this);
                fragmentsDialog.setContentView(R.layout.view_fragment_aboutus);
                fragmentsDialog.setTitle(Strings.ABOUT_US_TITLE);
                final RatingBar ratingBar = (RatingBar) fragmentsDialog.findViewById(R.id.ratingBar);

                ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                    public void onRatingChanged(RatingBar ratingBar, final float rating, boolean fromUser) {
                        TextView ratingLevelTV = (TextView) fragmentsDialog.findViewById(R.id.rateLevelTV);
                        String rate = getRatingTextFrom(rating);
                        ratingLevelTV.setText(rate);
                    }
                });

                Button button = (Button) fragmentsDialog.findViewById(R.id.rateBtn);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        //Check internet is enabled or not
                        if (InternetCheck.isNetworkAvailable(context)) {
                            String baseURL = context.getResources().getString(R.string.webserviceLink);
                            StringBuilder url = new StringBuilder(baseURL);
                            float rating = ratingBar.getRating();
                            String rateLevelText = getRatingTextFrom(rating);
                            url.append(Strings.INSERT_NEW_RATING_URL);
                            JSONObject ratingJSONObj = new JSONObject();
                            try {
                                ratingJSONObj.put(Strings.JSON_RATING_VALUE, rating);
                                ratingJSONObj.put(Strings.JSON_RATING_TEXT, rateLevelText);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            //Execute web task
                            Controller_WebTasks controller_webTasks = new Controller_WebTasks();
                            controller_webTasks.executePostRequestTaks(context, Strings.THANKING_TEXT, ratingJSONObj, url.toString());
                        } else {
                            Toast.makeText(context, "Sorry! please turn on your internet connection", Toast.LENGTH_LONG).show();
                        }
                    }
                });


                fragmentsDialog.show();
                break;
        }
        return true;
    }

    //Get rating string according to the given rating
    private String getRatingTextFrom(float rate) {
        int rateLevel = (int) rate;
        String rateLevelString;
        switch (rateLevel) {
            case 0:
                rateLevelString = Strings.RATING_VERY_BAD;
                break;
            case 1:
                rateLevelString = Strings.RATING_BAD;
                break;
            case 2:
                rateLevelString = Strings.RATING_AVERAGE_LEVEL;
                break;
            case 3:
                rateLevelString = Strings.RATING_GOOD;
                break;
            case 4:
                rateLevelString = Strings.RATING_VERY_GOOD;
                break;
            default:
                rateLevelString = Strings.RATING_EXCELLENT;
        }
        return rateLevelString;
    }

    //Onclick handle
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.arcLayoutCtrlBtn) {
            arcLayoutCtrlBtnClick(v);
            return;
        }

        if (v instanceof Button) {
            switch (v.getId()) {
                case R.id.docDetailsBtn:
                    viewDisplay.setCurrentItem(Numbers.ZERO);
                    docDetailsBTN.setBackgroundResource(R.drawable.light_blue_oval_selector);
                    viewDisplay.setBackgroundColor(Color.parseColor(Strings.WHITE_COLOR));
                    break;
                case R.id.locationDetailsBtn:
                    viewDisplay.setCurrentItem(Numbers.ONE);
                    viewDisplay.setBackgroundColor(Color.parseColor(Strings.WHITE_COLOR));
                    break;
                case R.id.reportFakeDocBtn:
                    viewDisplay.setCurrentItem(Numbers.TWO);
                    viewDisplay.setBackgroundColor(Color.parseColor(Strings.WHITE_COLOR));
                    break;
                case R.id.facebookShareBtn:
                    viewDisplay.setCurrentItem(Numbers.THREE);
                    viewDisplay.setBackgroundColor(Color.parseColor(Strings.WHITE_COLOR));
                    break;
            }
        }

        //Hide menu if user click anywhere in the screen
        if (v.getId() == menuLayout.getId()) {
            hideMenu();
        }
    }

    //Handle click event of arc layout controller buttons
    private void arcLayoutCtrlBtnClick(View v) {
        if (v.isSelected()) {
            hideMenu();
        } else {
            showMenu();
        }
        v.setSelected(!v.isSelected());
    }

    //Way to show arc layout
    private void showMenu() {
        menuLayout.setVisibility(View.VISIBLE);

        List<android.animation.Animator> animList = new ArrayList<>();

        for (int i = 0, len = arcLayout.getChildCount(); i < len; i++) {
            animList.add(createShowItemAnimator(arcLayout.getChildAt(i)));
        }

        AnimatorSet animSet = new AnimatorSet();
        animSet.setDuration(Numbers.ANIM_DURATION);
        animSet.setInterpolator(new OvershootInterpolator());
        animSet.playTogether(animList);
        animSet.start();
    }

    //Way to hide arc layout
    private void hideMenu() {

        List<android.animation.Animator> animList = new ArrayList<>();

        for (int i = arcLayout.getChildCount() - 1; i >= 0; i--) {
            animList.add(createHideItemAnimator(arcLayout.getChildAt(i)));
        }

        AnimatorSet animSet = new AnimatorSet();
        animSet.setDuration(Numbers.ANIM_DURATION);
        animSet.setInterpolator(new AnticipateInterpolator());
        animSet.playTogether(animList);
        animSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(android.animation.Animator animation) {
                super.onAnimationEnd(animation);
                menuLayout.setVisibility(View.INVISIBLE);
            }
        });
        animSet.start();
    }

    //Way to show animation of given arc layout button
    private android.animation.Animator createShowItemAnimator(View item) {
        float dx = arcLayouCtrl.getX() - item.getX();
        float dy = arcLayouCtrl.getY() - item.getY();

        item.setRotation(Numbers.STARTING_POSITION);
        item.setTranslationX(dx);
        item.setTranslationY(dy);

        android.animation.Animator anim = ObjectAnimator.ofPropertyValuesHolder(
                item,
                Animator.rotation(Numbers.STARTING_POSITION, Numbers.ENDING_POSITION),
                Animator.translationX(dx, Numbers.STARTING_POSITION),
                Animator.translationY(dy, Numbers.STARTING_POSITION)
        );

        return anim;
    }

    //Way to hide animation of given arc layout button
    private android.animation.Animator createHideItemAnimator(final View item) {
        float dx = arcLayouCtrl.getX() - item.getX();
        float dy = arcLayouCtrl.getY() - item.getY();

        android.animation.Animator anim = ObjectAnimator.ofPropertyValuesHolder(
                item,
                Animator.rotation(Numbers.ENDING_POSITION, Numbers.STARTING_POSITION),
                Animator.translationX(Numbers.STARTING_POSITION, dx),
                Animator.translationY(Numbers.STARTING_POSITION, dy)
        );

        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(android.animation.Animator animation) {
                super.onAnimationEnd(animation);
                item.setTranslationX(Numbers.STARTING_POSITION);
                item.setTranslationY(Numbers.STARTING_POSITION);
            }
        });

        return anim;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    //Handle tab click events
    @Override
    public void onPageSelected(int position) {
        getActionBar().setSelectedNavigationItem(position);
        switch (position) {
            case 0:
                viewDisplay.setCurrentItem(Numbers.ZERO);
                SelectedButtonDisplay(docDetailsBTN);
                viewDisplay.setBackgroundColor(Color.parseColor(Strings.WHITE_COLOR));
                break;
            case 1:
                viewDisplay.setCurrentItem(Numbers.ONE);
                SelectedButtonDisplay(locationDetailBTN);
                viewDisplay.setBackgroundColor(Color.parseColor(Strings.WHITE_COLOR));
                break;
            case 2:
                viewDisplay.setCurrentItem(Numbers.TWO);
                SelectedButtonDisplay(reportFakeDocBTN);
                viewDisplay.setBackgroundColor(Color.parseColor(Strings.WHITE_COLOR));
                break;
            case 3:
                viewDisplay.setCurrentItem(Numbers.THREE);
                SelectedButtonDisplay(faceBookShareBTN);
                viewDisplay.setBackgroundColor(Color.parseColor(Strings.WHITE_COLOR));
                break;
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    //Handle selected button of arc layout
    private void SelectedButtonDisplay(Button b) {
        lastSelectedButton.setBackgroundResource(R.drawable.path_white_oval);
        b.setBackgroundResource(R.drawable.light_blue_oval_selector);
        lastSelectedButton = b;
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, android.app.FragmentTransaction ft) {
        viewDisplay.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, android.app.FragmentTransaction ft) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, android.app.FragmentTransaction ft) {

    }
}
