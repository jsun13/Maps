package junzhaosun.map;

import androidx.annotation.NonNull;


import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import junzhaosun.map.MainActivity;
import junzhaosun.map.Constants;
import android.Manifest;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;

import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.textfield.TextInputEditText;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import junzhaosun.map.R;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private LocationManager locationManager;
    private LocationListener locationListener;
    private GoogleMap mMap;
    private Constants constant=new Constants();
    private List<LatLng> listLocation;
    private Marker curmark;
    private SharedPreferences sharedPreferences;
    private Location myLocation;
    private Marker searchMark;
    private boolean hasSearch=false;

    //buttons
    SwitchIconView Brelocate;
    SwitchIconView Bshow;
    SwitchIconView savedLocs;
//    private String query;
    /***
     * Whether a user responses yes or no to location request
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode==1){

            if (grantResults.length>0 && grantResults[0]== PackageManager.PERMISSION_GRANTED) {

                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                    Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    centerMapOnLocation(lastKnownLocation, "Your location");
                }
            }
        }
    }

    private View.OnClickListener mButtonOnClickListener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.save:
                    v.setEnabled(true);
                    Brelocate.setEnabled(false);
                    Bshow.setEnabled(false);
                    startActivityForResult(new Intent(getApplicationContext(), SearchActivity.class), 1);
                    break;
                case R.id.relocate:
                    v.setEnabled(true);
                    Bshow.setEnabled(false);
                    centerMapOnLocation(myLocation, "my Location");
                    hasSearch=false;
                    break;
                case R.id.show:
                    if(curmark!=null && searchMark!=null){
                        v.setEnabled(true);
                        GetDirectionData getDirectionData=new GetDirectionData();
                        String url=getUrl();
                        Object[] obj=new Object[3];
                        obj[0]=mMap;
                        obj[1]=searchMark.getPosition();
                        obj[2]=url;
                        getDirectionData.execute(obj);
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(searchMark.getPosition(), 14));
                        Brelocate.setEnabled(false);
                        savedLocs.setEnabled(false);
                    }else{
                        Toast.makeText(getApplicationContext(),"invalid operation",Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        savedLocs= findViewById(R.id.save);
        savedLocs.setOnClickListener(mButtonOnClickListener);
        Brelocate=(SwitchIconView) findViewById(R.id.relocate);
        Brelocate.setOnClickListener(mButtonOnClickListener);
        Bshow=(SwitchIconView) findViewById(R.id.show);
        Bshow.setOnClickListener(mButtonOnClickListener);

        Places.initialize(getApplicationContext(),"AIzaSyAVust1m2U_6bQ5vGSj4kE3yR8aW5KH8eo");

        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        if(autocompleteFragment!=null){
            autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME));

            autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
                @Override
                public void onPlaceSelected(Place place) {
                    if(Brelocate.isEnabled()) Brelocate.setEnabled(false);
                    moveToQuery(place.getName());
                }

                @Override
                public void onError(Status status) {
                    Log.i("onerror", "An error occurred: " + status);
                }
            });
        }


        /**
         * store unique action search result
         */
//        Intent intent=getIntent();
//
//        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
//            hasSearch=true;
//            query=intent.getStringExtra(SearchManager.QUERY);
//            moveToQuery(query);
//            if(SearchActivity.queries!=null && query!=null && !SearchActivity.queries.contains(query)){
//                SearchActivity.queries.add(query);
//                SharedPreferences savedlocs=getSharedPreferences("results",Context.MODE_PRIVATE);
//                try{
//                    savedlocs.edit().putString("queries",ObjectSerializer.serialize(SearchActivity.queries)).apply();
//                }catch (IOException e){
//                    e.printStackTrace();
//                }
//                moveToQuery(query);
//            }
//        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        addMarker();
        setUpMap();
        mMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
            @Override
            public void onCameraMove() {
                if(Brelocate.isEnabled())Brelocate.setEnabled(false);
                if(Bshow.isEnabled()) Bshow.setEnabled(false);
            }
        });
    }


    /**
     * deal with data sent from searchActivity
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if(resultCode == Activity.RESULT_OK){
                if(data.hasExtra("query")){
                    hasSearch=true;
                    String searchLoc=data.getStringExtra("query");
                    moveToQuery(searchLoc);
                    savedLocs.setEnabled(false);
                }
            }
        }
    }


    /**
     * 以下都是封装的方法
     */


    private String getUrl(){
        StringBuilder googleDirectionsUrl = new StringBuilder("https://maps.googleapis.com/maps/api/distancematrix/json?");
        googleDirectionsUrl.append("origins="+curmark.getPosition().latitude+","+curmark.getPosition().longitude);
        googleDirectionsUrl.append("&destinations="+searchMark.getPosition().latitude+","+searchMark.getPosition().longitude);
        googleDirectionsUrl.append("&key=AIzaSyAVust1m2U_6bQ5vGSj4kE3yR8aW5KH8eo");
        return googleDirectionsUrl.toString();
    }


    private void setUpMap(){
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if(!hasSearch){
                    centerMapOnLocation(location, "Your location");
                }
                myLocation=location;
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
        };
        /**
         * Check whether location is allowed and updates location
         */
        if(Build.VERSION.SDK_INT < 23){
            Log.i("You need to update", "update your version");
        }else{
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }else{
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 30, locationListener);
            }
        }
    }

    private void addMarker(){
        sharedPreferences= getSharedPreferences("data", Context .MODE_PRIVATE);
        if(sharedPreferences!=null && mMap!=null){
            if(sharedPreferences.contains("resident")){
                listLocation=constant.getResident();
            }else if(sharedPreferences.contains("commuter")){
                listLocation=constant.getCommuter();
            }else{
                listLocation=constant.getFaculty();
            }
            for(LatLng loc: listLocation){
                mMap.addMarker(new MarkerOptions().position(loc).title(getAddress(loc))
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
            }
        }
    }


    private void centerMapOnLocation(Location location, String title) {

        if (location != null) {

            LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
            if(curmark!=null) curmark.remove();
            if(mMap!=null){
                curmark=mMap.addMarker(new MarkerOptions().position(userLocation).title(title));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 14));
            }
        }
    }


    private String getAddress(LatLng location){

        String add="";

        Geocoder geocoder=new Geocoder(MapsActivity.this, Locale.getDefault());
        try{
            List<Address> addresses=geocoder.getFromLocation(location.latitude, location.longitude, 1);
            if(addresses != null && addresses.size()>0) {
                add = addresses.get(0).getThoroughfare() + addresses.get(0).getSubThoroughfare();
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        return add;
    }


    private void moveToQuery( String query){

        Geocoder geocoder=new Geocoder(MapsActivity.this,Locale.getDefault());
        try{
            List<Address> addresses=geocoder.getFromLocationName(query, 1);

            if(addresses != null && addresses.size()>0) {
                Address address=addresses.get(0);
                LatLng newLoc=new LatLng(address.getLatitude(),address.getLongitude());
                if(searchMark!=null) searchMark.remove();
                if(mMap!=null){
                    searchMark=mMap.addMarker(new MarkerOptions().position(newLoc).title("new location"));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(newLoc, 14));
                }
            }else{
                Toast.makeText(getApplicationContext(), "cannot get from address name", Toast.LENGTH_SHORT).show();
            }
        }catch (IOException e){
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
}
