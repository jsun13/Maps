package junzhaosun.map;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;


public class SearchActivity extends AppCompatActivity {
    private SearchView searchView;
    private SearchManager searchManager;
    public static ArrayList<String> queries;
    private SharedPreferences sharedPreferences;

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        super.onCreateOptionsMenu( menu );
//
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.search_menu, menu);
//
//        searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
//        searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
//        searchView.setSearchableInfo(searchManager.getSearchableInfo(new ComponentName(this, MapsActivity.class)));
//        searchView.setIconifiedByDefault(false);
//        searchView.setIconified(false);
//
//        return true;
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        final ListView listView=(ListView) findViewById(R.id.listview);
        queries =new ArrayList<>();

        sharedPreferences=getSharedPreferences("results",Context.MODE_PRIVATE);
        if(sharedPreferences.contains("queries") && sharedPreferences.getString("queries", "").length()>0){
            try {
                queries = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences
                                .getString("queries",ObjectSerializer.serialize(new ArrayList<String>())));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        ListItemAdapter saveAdapter=new ListItemAdapter(getBaseContext(), queries);
        listView.setAdapter(saveAdapter);

        saveAdapter.setOnClickListener(new ListItemAdapter.mListener() {
            @Override
            public void onShowLocation(BaseAdapter adapter, View view, int position) {
                Intent returnIntent = new Intent();
                returnIntent.putExtra("query", queries.get(position));
                setResult(Activity.RESULT_OK,returnIntent);
                finish();
            }
            @Override
            public void onDeleteLocation(ListItemAdapter adapter, View view, int position) {
                adapter.remove(position);
                try{
                    sharedPreferences.edit().putString("queries",ObjectSerializer.serialize(queries)).apply();
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        });

//        if(searchView!=null){
//            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//                @Override
//                public boolean onQueryTextSubmit(String query) {
//                    finish();
//                    return false;
//                }
//                @Override
//                public boolean onQueryTextChange(String newText) {
//                    return false;
//                }
//            });
//        }

    }



}
