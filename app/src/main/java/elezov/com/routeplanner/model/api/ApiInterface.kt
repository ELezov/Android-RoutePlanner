package elezov.com.routeplanner.model.api

import elezov.com.routeplanner.model.data.Direction.Route
import retrofit.http.GET
import retrofit.http.Query
import rx.Observable

/**
 * Created by USER on 13.07.2017.
 */

interface ApiInterface {
    @GET("api/directions/json?key=AIzaSyD4LHN3dqnCOFM9z2loaj3QB_jV2S2ivxk&language=ru")
    fun getDirections(@Query("origin") origin: String,
                      @Query("destination") destination: String,
                      @Query("waypoints") waypoints: String): Observable<List<Route>>
}

