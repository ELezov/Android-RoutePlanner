package elezov.com.routeplanner.model


import elezov.com.routeplanner.model.api.ApiModule
import elezov.com.routeplanner.model.data.Direction.Route
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

class ModelImpl : Model {

    internal var apiInterface = ApiModule.getApiInterface()

    override fun getRouteList(origin: String, destination: String, waypoints: String): Observable<List<Route>> {
        return apiInterface.getDirections(origin, origin, origin)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }


}
