package com.itrans.kurs.fragment;

import android.app.Fragment;
import android.hardware.GeomagneticField;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.itrans.kurs.OnAzimuthChangedListener;
import com.itrans.kurs.OnLocationChangedListener;
import com.itrans.kurs.R;
import com.itrans.kurs.model.CurrentAzimuth;
import com.itrans.kurs.model.CurrentLocation;


public class NavigatorFragment extends Fragment implements OnLocationChangedListener, OnAzimuthChangedListener {

    private static final String ARG_TARGET_LON = "target_lon";
    private static final String ARG_TARGET_LAT = "target_lat";

    private Double target_lon;
    private Double target_lat;

    double heading = 0;
    GeomagneticField geoField;




    private CurrentLocation currentLocation;
    private CurrentAzimuth currentAzimuth;



    public NavigatorFragment() {
        // Required empty public constructor
    }

    private TextView azimut_text;
    private TextView location_text;
    private TextView distance_text;
    private ImageView nav_arrow;
    private ImageView compass;

    private double rotatedDegree;
    private double targetAngle;
    private double dX;
    private double dY;

    private Double mMyLongitude = 0d;
    private Double mMyLatitude = 0d;


    public static NavigatorFragment newInstance(Double lat, Double lon) {
        NavigatorFragment fragment = new NavigatorFragment();
        Bundle args = new Bundle();
        args.putDouble(ARG_TARGET_LAT, lat);
        args.putDouble(ARG_TARGET_LON, lon);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments()!=null){
            target_lat = getArguments().getDouble(ARG_TARGET_LAT);
            target_lon = getArguments().getDouble(ARG_TARGET_LON);
        }
        setUpListeners();
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.navigator_menu,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_info:
                showInfo();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        currentLocation.start();
        currentAzimuth.start();
    }


    @Override
    public void onStop() {
        super.onStop();
        currentLocation.stop();
        currentAzimuth.stop();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_navigator, container, false);
        azimut_text = v.findViewById(R.id.azimut);
        location_text = v.findViewById(R.id.location);
        distance_text = v.findViewById(R.id.distance);
        nav_arrow = v.findViewById(R.id.nav_arrow);
        compass = v.findViewById(R.id.compass);
        return v;
    }

    @Override
    public void onAzimuthChanged(float azimuthFrom, float azimuthTo) {

        double degree = azimuthTo;


        double tanPhi = Math.abs(dX/dY);


        double phiAngle = Math.atan(tanPhi);
        phiAngle = Math.toDegrees(phiAngle);

        if(dY < 0){
            if(dX > 0){
                phiAngle = 180-phiAngle;
            }else{
                phiAngle = 180+phiAngle;
            }
        }
        else{
            if(dX<0){
                phiAngle = 360-phiAngle;
            }
        }

        targetAngle = -degree+(float)phiAngle;

        RotateAnimation navArrowAnimation = new RotateAnimation(
                (float) rotatedDegree,
                (float) targetAngle,
                Animation.RELATIVE_TO_SELF,0.5f,
                Animation.RELATIVE_TO_SELF,0.5f);

        RotateAnimation compassAnimation = new RotateAnimation(
                -azimuthFrom,
                -azimuthTo,
                Animation.RELATIVE_TO_SELF,0.5f,
                Animation.RELATIVE_TO_SELF,0.5f);

        navArrowAnimation.setFillAfter(true);
        compassAnimation.setFillAfter(true);
        nav_arrow.startAnimation(navArrowAnimation);
        compass.startAnimation(compassAnimation);
        rotatedDegree = targetAngle;

        azimut_text.setText("Отклонение от севера - "+Double.toString(degree)+" градусов\n"
                +"Азимут цели: "+phiAngle+" градусов\n"+
                "Угол цели: "+targetAngle+"градусов");
    }


    protected void setUpListeners(){
        currentLocation = new CurrentLocation(this);
        currentLocation.buildGoogleApiClient(getActivity());
        currentLocation.start();
        currentAzimuth = new CurrentAzimuth(this,getActivity());
        currentAzimuth.start();
    }




    @Override
    public void onLocationChanged(Location location) {
        mMyLatitude = location.getLatitude();
        mMyLongitude = location.getLongitude();
        double dist = calculateDistance(location);
        dist = Math.round(dist*10.0)/10.0;

        location_text.setText("My coordinates: "+mMyLatitude+" "+mMyLongitude+"\n"
        +"Target coordinates: "+target_lat+" "+target_lon);
        distance_text.setText("Distance: "+dist+" meters");
    }



    private Double calculateDistance(Location location){

        dY = target_lat - mMyLatitude;
        dX = target_lon - mMyLongitude;

        Location loc = new Location("Test");
        loc.setLongitude(target_lon);
        loc.setLatitude(target_lat);



        double distance = (double) location.distanceTo(loc);

        return distance;
    }

    private void showInfo(){
        if(location_text.getVisibility() == View.GONE) {
            location_text.setVisibility(View.VISIBLE);
            azimut_text.setVisibility(View.VISIBLE);
        }
        else{
            location_text.setVisibility(View.GONE);
            azimut_text.setVisibility(View.GONE);
        }
    }
}
