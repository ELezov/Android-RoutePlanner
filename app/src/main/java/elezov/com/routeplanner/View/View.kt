package elezov.com.routeplanner.View

import com.google.android.gms.maps.model.LatLng
import java.util.*

/**
 * Created by USER on 14.07.2017.
 */
public interface View {


        fun showDirection(routeList:ArrayList<LatLng>)
        fun showError(error:String)
        fun showEmptyMap();


}