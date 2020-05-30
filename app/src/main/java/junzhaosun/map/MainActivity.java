package junzhaosun.map;

import android.Manifest;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.os.TestLooperManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.TextView;

import junzhaosun.map.R;

public class MainActivity extends AppCompatActivity {


    private TextView resident;
    private TextView commuter;
    private TextView faculty;
    private SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        resident= findViewById(R.id.resident);
        commuter = findViewById(R.id.commuter);
        faculty= findViewById(R.id.faculty);

        sharedPreferences = getSharedPreferences("data",Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        if(sharedPreferences.contains("resident") || sharedPreferences.contains("commuter") ||sharedPreferences.contains("faculty"))
            sharedPreferences.edit().clear().apply();
        resident.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                editor.putBoolean("resident", true);
                editor.commit();
                startActivity(new Intent(getApplicationContext(), MapsActivity.class));

            }
        });

        commuter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                editor.putBoolean("commuter", true);
                editor.commit();
                startActivity(new Intent(getApplicationContext(), MapsActivity.class));

            }
        });

        //facultY为什么比其他俩加载要慢？？？

        faculty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                editor.putBoolean("faculty", true);
                editor.commit();
                startActivity(new Intent(getApplicationContext(), MapsActivity.class));
            }
        });
    }

//    /**
//     * if user navigates back, we reset their preference
//     */
//    @Override
//    protected void onRestart(){
//        super.onRestart();
//        if(sharedPreferences!=null){
//            sharedPreferences.edit().clear().apply();
//        }
//    }

    /**
     * need to implement notice to save preference
     */

}
