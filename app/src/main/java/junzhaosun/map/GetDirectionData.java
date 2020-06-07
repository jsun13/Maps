package junzhaosun.map;

import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
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

public class GetDirectionData extends AsyncTask<Object, String, String> {
    private GoogleMap mMap;
    private String url;
    private LatLng destination;
    private String googleDirectionsData;

    @Override
    protected String doInBackground(Object... objects) {
        mMap = (GoogleMap)objects[0];
        destination=(LatLng)objects[1];
        url = (String)objects[2];
        Log.e("passed url", url);
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
        String duration=directionList.get("duration");
        String distance=directionList.get("distance");


        MarkerOptions markerOptions=new MarkerOptions();
        markerOptions.position(destination).title("Duration = "+duration).snippet("Distance = "+distance);
        mMap.addMarker(markerOptions);
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
        return map;
    }

    private String readUrl(String myUrl) throws IOException
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

        Log.e("data downlaod",data);
        return data;
    }
}
