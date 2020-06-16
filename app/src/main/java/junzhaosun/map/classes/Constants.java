package junzhaosun.map.classes;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Constants {
    private LatLng yates= new LatLng(37.271691, -76.717894); //commuter+resident
    private LatLng barrett=new LatLng(37.269194,-76.7122987); //resident
    private LatLng sadler = new LatLng(37.272562, -76.713270); //resident+commuter+faculty
    private LatLng swem = new LatLng(37.270153, -76.717030); // faculty


    public List<LatLng> getCommuter() {
        return Arrays.asList(yates, sadler);
    }

    public List<LatLng> getResident() {
        return Arrays.asList(yates, barrett, sadler);
    }

    public List<LatLng> getFaculty() {
        return Arrays.asList(swem, sadler);
    }

}
