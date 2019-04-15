package antho.com.go4lunch.model.workmate;
import com.google.auto.value.AutoValue;
import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.Nullable;
/** Data model for user object **/
@AutoValue
public class User
{
    public String name;
    public String id;
    public String restaurantId;
    public List<String> likedRestaurants;

    public User(){}
    public User(String id, String username, @Nullable String restaurantId, @Nullable List<String> likedRestaurants)
    {
        this.name = username;
        this.id = id;
        this.restaurantId = restaurantId;
        this.likedRestaurants = likedRestaurants;
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
        result.put("id", id);
        result.put("name", name);
        result.put("restaurantId", restaurantId);

        return result;
    }
}