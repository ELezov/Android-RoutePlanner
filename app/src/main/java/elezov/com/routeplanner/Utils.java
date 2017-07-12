package elezov.com.routeplanner;

import com.google.android.gms.location.places.Place;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by USER on 22.03.2017.
 */

public class Utils {

    public List<Place> placeList=new ArrayList<Place>();
    private static Utils utils;

    Utils(){

    }

    public static Utils getInstance(){
        if(utils==null){
            utils=new Utils();
        }
        return utils;
    }

    public List<Place> getPlaceList() {
        return placeList;
    }

    public void setPlaceList(List<Place> placeList) {
        this.placeList = placeList;
    }

    public void addToList(Place item){
        placeList.add(item);
    }

}



