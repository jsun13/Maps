package junzhaosun.map.classes;

import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import junzhaosun.map.MapsActivity;

public class GetDirectionData extends AsyncTask<Object, String, String> {
    private GoogleMap mMap;
    private String url;
    private Marker searchMark;
    private String googleDirectionsData;
    private String duration;
    private String distance;
    @Override
    protected String doInBackground(Object... objects) {
        mMap = (GoogleMap)objects[0];
        searchMark=(Marker)objects[1];
        url = (String)objects[2];
        googleDirectionsData="";
        try {
            googleDirectionsData = readUrl(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return googleDirectionsData;
    }

    @Override
    protected void onPostExecute(String s) {
        HashMap<String,String> directionList=parseDirection(s);
        duration=directionList.get("duration");
        distance=directionList.get("distance");

        searchMark.setSnippet("Duration = "+duration+", Distance = "+distance);
        searchMark.showInfoWindow();
    }

    private HashMap<String, String> parseDirection(String jsonData) {
        JSONArray jsonArray = null;
        JSONObject jsonObject;

        try {
            jsonObject = new JSONObject(jsonData);
            jsonArray = jsonObject.getJSONArray("rows").getJSONObject(0).getJSONArray("elements");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        HashMap<String, String> map=new HashMap<>();
        String distance="dummy", duration="dummy";
        try {
            duration=jsonArray.getJSONObject(0).getJSONObject("duration").getString("text");
            distance=jsonArray.getJSONObject(0).getJSONObject("distance").getString("text");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        map.put("distance", distance);
        map.put("duration", duration);
        Log.i("download distance", distance);
        Log.i("download duration", duration);
        return map;
    }

    public String readUrl(String myUrl) throws IOException
    {
        String data = "";
        InputStream inputStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(myUrl);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.connect();

            inputStream = urlConnection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            StringBuffer sb = new StringBuffer();

            String line = "";
            while((line = br.readLine()) != null)
            {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            if(inputStream != null)
                inputStream.close();
            urlConnection.disconnect();
        }

        return data;
    }
}
