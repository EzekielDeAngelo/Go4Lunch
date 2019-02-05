package antho.com.go4lunch.model.workmate;


import com.google.auto.value.AutoValue;
import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.Nullable;

@AutoValue
public /*abstract*/ class Workmate
{
    public /*abstract*/ String name;
    public /*abstract*/ String id;
    public String restaurantId;
    public Workmate(){}
    public Workmate(String id, String username, @Nullable String restaurantId) {
        this.name = username;
        this.id = id;
        this.restaurantId = restaurantId;
    }
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }
    public void setRestaurantId(String restaurantId) {


        this.restaurantId = restaurantId;
    }

    public String getRestaurantId() {
        return restaurantId;
    }


    public void setName(String name) {
        this.name = name;
    }
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        //result.put("id", id);
        result.put("name", name);
        result.put("restaurantid", restaurantId);


        return result;
    }

}