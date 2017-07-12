package elezov.com.routeplanner.Direction;


import elezov.com.routeplanner.model.data.Directions;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by USER on 19.02.2017.
 */

public interface DirectionAPI {
    @GET("api/directions/json?key=AIzaSyD4LHN3dqnCOFM9z2loaj3QB_jV2S2ivxk&language=ru")
    Call<Directions> getJson(@Query("origin") String origin,
                             @Query("destination") String destination,
                             @Query("waypoints") String waypoints);
}

