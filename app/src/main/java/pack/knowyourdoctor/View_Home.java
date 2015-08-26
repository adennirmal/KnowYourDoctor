package pack.knowyourdoctor;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.app.ActionBar;
import android.view.View;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.Toast;

import com.ogaclejapan.arclayout.ArcLayout;

import java.util.ArrayList;
import java.util.List;

import pack.knowyourdoctor.AnimationPack.AnimatorUtils;
import pack.knowyourdoctor.adapter.TabPagerAdapter;


public class View_Home extends FragmentActivity implements View.OnClickListener,ViewPager.OnPageChangeListener,ActionBar.TabListener, LocationListener{
    private Context context;
    View mFab;
    View mMenuLayout;
    ArcLayout mArcLayout;
    ViewPager viewDisplay;
    Button docDetailsBTN;
    Button reportFakeDocBTN;
    Button locationDetailBTN;
    Button likeDocDetailsBTN;
    Button faceBookShareBTN;
    Button aboutSLMCBTN;
    Button aboutDevelopersBTN;
    private TabPagerAdapter mAdapter;
    private ActionBar actionBar;
    LocationManager locationManager ;
    Location location;
    String provider;

    Button lastSelectedButton;

    public static Bundle urlBundle;

    // Tab titles
    //private String[] tabs = { "Search", "Locate Doctor","Report","FB","SLMC","About Us"};
    final int[] icons = {R.drawable.search_selector,R.drawable.location_selector,R.drawable.report_selector,R.drawable.fb_selector};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_home);

        //prevent app stop
        urlBundle = new Bundle();
        String url = "http://www.srilankamedicalcouncil.org/registry.php?registry=&initials=&last_name=&other_name=&reg_no=&nic=&part_of_address=&search=Search";
        urlBundle.putString("url", url);

        mFab = findViewById(R.id.fab);
        mMenuLayout = findViewById(R.id.menu_layout);
        mArcLayout = (ArcLayout) findViewById(R.id.arc_layout);
        viewDisplay = (ViewPager) findViewById(R.id.pager);

        actionBar = getActionBar();
        mAdapter = new TabPagerAdapter(getSupportFragmentManager());

        viewDisplay.setAdapter(mAdapter);

        actionBar.setHomeButtonEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        viewDisplay.setAdapter(mAdapter);

        /*for (int i =0; i<tabs.length; i++){
            actionBar.addTab(actionBar.newTab().setText(tabs[i]).setTabListener(this));
        }*/


        for (int i =0; i<icons.length; i++){
            actionBar.addTab(actionBar.newTab().setIcon(icons[i]).setTabListener(this));
        }
        //actionBar.setBackgroundDrawable(new ColorDrawable(Color.WHITE));

        context = getApplicationContext();

        //Define buttons
        docDetailsBTN = (Button) findViewById(R.id.docDetailsBtn);
        reportFakeDocBTN = (Button) findViewById(R.id.reportFakeDocBtn);
        locationDetailBTN = (Button) findViewById(R.id.locationDetailsBtn);
        faceBookShareBTN = (Button) findViewById(R.id.facebookShareBtn);
        aboutSLMCBTN = (Button) findViewById(R.id.aboutSLMCBtn);
        aboutDevelopersBTN = (Button) findViewById(R.id.aboutDevelopersBtn);

        lastSelectedButton = docDetailsBTN;
        docDetailsBTN.setBackgroundResource(R.drawable.light_blue_oval_selector);

        //Detect slide
        viewDisplay.setOnPageChangeListener(this);

        mMenuLayout.setOnClickListener(this);

        for (int i = 0, size = mArcLayout.getChildCount(); i < size; i++) {
            mArcLayout.getChildAt(i).setOnClickListener(this);
        }

        mFab.setOnClickListener(this);

        if (this.getIntent().hasExtra("SelectedDoc")) {
            Model_Doctor selectedDoctor = (Model_Doctor) this.getIntent().getSerializableExtra("SelectedDoc");
            View_Doctor_Rating ratingDialog = new View_Doctor_Rating();
            ratingDialog.setArguments(selectedDoctor,context);
            ratingDialog.show(getSupportFragmentManager(), null);
        }


        //Load Coordinates

        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE); // Getting LocationManager object

        Criteria criteria = new Criteria();// Creating an empty criteria object

        provider = locationManager.getBestProvider(criteria, false);// Getting the name of the provider that meets the criteria

        if(provider!=null && !provider.equals("")){

            // Get the location from the given provider
            location = locationManager.getLastKnownLocation(provider);

            locationManager.requestLocationUpdates(provider, 20000, 1, this);

                if(location!=null)
                    onLocationChanged(location);
                else

                    Toast.makeText(context, "Location can't be retrieved", Toast.LENGTH_SHORT).show();

        }else{
            Toast.makeText(context, "No Provider Found", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.fab) {
            onFabClick(v);
            return;
        }

        if (v instanceof Button) {
            switch (((Button) v).getId()){
                case R.id.docDetailsBtn:
                    viewDisplay.setCurrentItem(0);
                    docDetailsBTN.setBackgroundResource(R.drawable.light_blue_oval_selector);
                    viewDisplay.setBackgroundColor(Color.parseColor("#ffffff"));
                    break;
                case R.id.locationDetailsBtn:
                    viewDisplay.setCurrentItem(1);
                    viewDisplay.setBackgroundColor(Color.parseColor("#ffffff"));
                    break;
                case R.id.reportFakeDocBtn:
                    viewDisplay.setCurrentItem(2);
                    viewDisplay.setBackgroundColor(Color.parseColor("#ffffff"));
                    break;
                case R.id.facebookShareBtn:
                    viewDisplay.setCurrentItem(3);
                    viewDisplay.setBackgroundColor(Color.parseColor("#f1f1f1"));
                    break;
                case R.id.aboutSLMCBtn:
                    viewDisplay.setCurrentItem(4);
                    viewDisplay.setBackgroundColor(Color.parseColor("#ffffff"));
                    break;
                case R.id.aboutDevelopersBtn:
                    viewDisplay.setCurrentItem(5);
                    viewDisplay.setBackgroundColor(Color.parseColor("#ffffff"));
                    break;
            }
        }

        //Hide menu if user click anywhere in the screen
        if(v.getId() == mMenuLayout.getId()){
            hideMenu();
        }
    }

    private void onFabClick(View v) {
        if (v.isSelected()) {
            hideMenu();
        } else {
            showMenu();
        }
        v.setSelected(!v.isSelected());
    }

    @SuppressWarnings("NewApi")
    private void showMenu() {
        mMenuLayout.setVisibility(View.VISIBLE);

        List<Animator> animList = new ArrayList<>();

        for (int i = 0, len = mArcLayout.getChildCount(); i < len; i++) {
            animList.add(createShowItemAnimator(mArcLayout.getChildAt(i)));
        }

        AnimatorSet animSet = new AnimatorSet();
        animSet.setDuration(400);
        animSet.setInterpolator(new OvershootInterpolator());
        animSet.playTogether(animList);
        animSet.start();
    }

    @SuppressWarnings("NewApi")
    private void hideMenu() {

        List<Animator> animList = new ArrayList<>();

        for (int i = mArcLayout.getChildCount() - 1; i >= 0; i--) {
            animList.add(createHideItemAnimator(mArcLayout.getChildAt(i)));
        }

        AnimatorSet animSet = new AnimatorSet();
        animSet.setDuration(400);
        animSet.setInterpolator(new AnticipateInterpolator());
        animSet.playTogether(animList);
        animSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mMenuLayout.setVisibility(View.INVISIBLE);
            }
        });
        animSet.start();
    }

    private Animator createShowItemAnimator(View item) {

        float dx = mFab.getX() - item.getX();
        float dy = mFab.getY() - item.getY();

        item.setRotation(0f);
        item.setTranslationX(dx);
        item.setTranslationY(dy);

        Animator anim = ObjectAnimator.ofPropertyValuesHolder(
                item,
                AnimatorUtils.rotation(0f, 720f),
                AnimatorUtils.translationX(dx, 0f),
                AnimatorUtils.translationY(dy, 0f)
        );

        return anim;
    }

    private Animator createHideItemAnimator(final View item) {
        float dx = mFab.getX() - item.getX();
        float dy = mFab.getY() - item.getY();

        Animator anim = ObjectAnimator.ofPropertyValuesHolder(
                item,
                AnimatorUtils.rotation(720f, 0f),
                AnimatorUtils.translationX(0f, dx),
                AnimatorUtils.translationY(0f, dy)
        );

        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                item.setTranslationX(0f);
                item.setTranslationY(0f);
            }
        });

        return anim;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        switch (position){
            case 0:
                viewDisplay.setCurrentItem(0);
                SelectedButtonDisplay(docDetailsBTN);
                viewDisplay.setBackgroundColor(Color.parseColor("#ffffff"));
                break;
            case 1:
                viewDisplay.setCurrentItem(1);
                SelectedButtonDisplay(locationDetailBTN);
                viewDisplay.setBackgroundColor(Color.parseColor("#ffffff"));
                break;
            case 2:
                viewDisplay.setCurrentItem(2);
                SelectedButtonDisplay(reportFakeDocBTN);
                viewDisplay.setBackgroundColor(Color.parseColor("#ffffff"));
                break;
            case 3:
                viewDisplay.setCurrentItem(3);
                SelectedButtonDisplay(faceBookShareBTN);
                viewDisplay.setBackgroundColor(Color.parseColor("#f1f1f1"));
                break;
            case 4:
                viewDisplay.setCurrentItem(4);
                SelectedButtonDisplay(aboutSLMCBTN);
                viewDisplay.setBackgroundColor(Color.parseColor("#ffffff"));
                break;
            case 5:
                viewDisplay.setCurrentItem(5);
                SelectedButtonDisplay(aboutDevelopersBTN);
                viewDisplay.setBackgroundColor(Color.parseColor("#ffffff"));
                break;
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    private void SelectedButtonDisplay(Button b){
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

    @Override
    public void onLocationChanged(Location location) {

        Global_Values.latitude = location.getLatitude();
        Global_Values.longtitude = location.getLongitude();

        Toast.makeText(context,"Loading Coordinates",Toast.LENGTH_LONG);

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
}