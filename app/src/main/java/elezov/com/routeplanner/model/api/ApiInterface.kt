package elezov.com.routeplanner.model.api

import elezov.com.routeplanner.model.data.Directions
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Created by USER on 12.07.2017.
 */
interface ApiInterface {
    @GET("api/directions/json?key=AIzaSyD4LHN3dqnCOFM9z2loaj3QB_jV2S2ivxk&language=ru")
    abstract fun getDirections(@Query("origin") origin: String,
                         @Query("destination") destination: String,
                         @Query("waypoints") waypoints: String): Call<Directions>
}