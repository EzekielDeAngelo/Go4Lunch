package antho.com.go4lunch.model.workmate;
import com.google.auto.value.AutoValue;

import androidx.annotation.Nullable;
/** Data model for workmate object **/
@AutoValue
public class Workmate
{
    public String name;
    private String id;
    public String restaurantId;


    public Workmate(){}
    public Workmate(/*String id,*/ String username, @Nullable String restaurantId) {
        this.name = username;
        //this.id = id;
        this.restaurantId = restaurantId;

    }
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }



}