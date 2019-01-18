package antho.com.go4lunch.view.fragments.adapter;
/** **/
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/** **/
public class ParserTask extends AsyncTask<String, Integer, List<HashMap<String, String>>>
{

    JSONObject jObject;
    // Invoked by execute() method of this object
    @Override
    protected List<HashMap<String, String>> doInBackground(String... jsonData)
    {
        List<HashMap<String, String>> places = null;
        Place_JSON placeJson = new Place_JSON();
        try
        {
            jObject = new JSONObject(jsonData[0]);

            places = placeJson.parse(jObject);
        }
        catch (Exception e)
        {
            Log.d("Exception", e.toString());
        }
        return places;
    }
    // Executed after the complete execution of doInBackground() method
    @Override
    protected void onPostExecute(List<HashMap<String, String>> list)
    {
        Log.d("Map", "list size: " + list.size());
        // Clears all the existing markers;
        //mGoogleMap.clear();
        for (int i = 0; i < list.size(); i++)
        {
            // Creating a marker
            MarkerOptions markerOptions = new MarkerOptions();
            // Getting a place from the places list
            HashMap<String, String> hmPlace = list.get(i);
            // Getting latitude of the place
            double lat = Double.parseDouble(hmPlace.get("lat"));
            // Getting longitude of the place
            double lng = Double.parseDouble(hmPlace.get("lng"));
            String openingHours = hmPlace.get("opening_hours");
            Log.d("Map", "place:" + openingHours);
            //
            String placeId = hmPlace.get("place_id");
            Log.d("Map", "place:" + placeId);
            // Getting name
            String name = hmPlace.get("place_name");

            Log.d("Map", "place: " + name);
            // Getting vicinity
            String vicinity = hmPlace.get("vicinity");

            LatLng latLng = new LatLng(lat, lng);
            ArrayList<LatLng> restaurants = new ArrayList<LatLng>();
            restaurants.add(latLng);
            // Setting the position for the marker
            markerOptions.position(latLng);
            markerOptions.title(name + " : " + vicinity);
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
            // Placing a marker on the touched position
            //Marker m = mGoogleMap.addMarker(markerOptions);
        }
    }
}