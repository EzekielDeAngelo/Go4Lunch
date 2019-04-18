package antho.com.go4lunch;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import antho.com.go4lunch.model.restaurant.places.Place;
import antho.com.go4lunch.model.restaurant.places.PlacePhotos;
import antho.com.go4lunch.model.workmate.Workmate;

class TestUtility
{
    //
    public static List<Place> getTestingPlaceListOfSize(int size)
    {
        List<Place> dummyList = new ArrayList<>();
        for (int i = 0 ; i < size ; i++)
        {
            Place news = new Place()
            {

                @Override
                public String name() {
                    return generateRandomString();
                }

                @Override
                public String address() {
                    return generateRandomString();
                }

                @Override
                public String website() {
                    return generateRandomString();
                }

                @Override
                public String phone() {
                    return generateRandomString();
                }

                @Override
                public List<PlacePhotos> photos() {
                    return null;
                }
            };
            dummyList.add(news);
        }
        return dummyList;
    }
    //
    public static List<Workmate> getTestingWorkmateListOfSize(int size)
    {
        List<Workmate> dummyList = new ArrayList<>();
        for(int i = 0; i < size; i++) {
            Workmate popular = new Workmate()
            {

            };
            dummyList.add(popular);
        }
        return dummyList;
    }
    //
    private static String generateRandomString()
    {
        return UUID.randomUUID().toString();
    }
}
