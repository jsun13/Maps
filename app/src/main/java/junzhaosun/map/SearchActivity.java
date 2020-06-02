package junzhaosun.map;

import android.app.Activity;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import junzhaosun.map.R;

public class SearchActivity extends AppCompatActivity {
    private SearchView searchView;
    private SearchManager searchManager;
    public static ArrayList<String> queries;
    private SharedPreferences sharedPreferences;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu( menu );

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);

        searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(new ComponentName(this, MapsActivity.class)));
        searchView.setIconifiedByDefault(false);
        searchView.setIconified(false);

        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        ListView listView=(ListView) findViewById(R.id.listView);
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

        ArrayAdapter<String> saveAdapter=new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, queries);
        listView.setAdapter(saveAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent returnIntent = new Intent();
                returnIntent.putExtra("query", parent.getItemAtPosition(position).toString());
                setResult(Activity.RESULT_OK,returnIntent);
                finish();
            }
        });


        if(searchView!=null){
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    finish();
                    return false;
                }
                @Override
                public boolean onQueryTextChange(String newText) {
                    return false;
                }
            });
        }

    }



}
