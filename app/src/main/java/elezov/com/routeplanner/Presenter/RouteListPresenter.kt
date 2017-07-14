package elezov.com.routeplanner.Presenter

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.location.LocationManager
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.android.gms.location.places.Place
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PolylineOptions
import elezov.com.routeplanner.Utils
import elezov.com.routeplanner.model.ModelImpl
import elezov.com.routeplanner.model.data.Direction.Location
import elezov.com.routeplanner.model.data.Direction.Route
import elezov.com.routeplanner.model.data.Direction.RouteDecode
import elezov.com.routeplanner.model.data.Direction.Steps
import elezov.com.routeplanner.model.data.Place.Geometry
import rx.Observer
import rx.subscriptions.Subscriptions
import java.io.UnsupportedEncodingException
import java.util.*

/**
 * Created by USER on 13.07.2017.
 */
class RouteListPresenter:Presenter {

    override fun OnStop() {
        if (!subscription.isUnsubscribed)
            subscription.unsubscribe()
    }

    var longitude: Double? = null
    var latitude: Double? = null
    var origin:String=""
    var destination:String=""
    var waypoint=""

    var model=ModelImpl()
    var subscription=Subscriptions.empty()

    var context:Context?=null
    lateinit private var view:View
    lateinit var activity: Activity

    constructor(view: View,context: Context,activity: Activity){
        this.view=view
        this.context=context
        this.activity=activity
    }

    override fun OnGetDirectionButtonClick() {
        if (!subscription.isUnsubscribed)
            subscription.unsubscribe()

            var placeList=Utils.getInstance().getPlaceList()

            initLocation(placeList)

            subscription = model.getRouteList(origin, destination, waypoint)
                .subscribe(object : Observer<List<Route>> {
                    override fun onCompleted() {

                    }

                    override fun onError(e: Throwable) {
                       // view.showError(e.message)
                    }

                    override fun onNext(data: List<Route>?) {
                       /* if (data != null && !data.isEmpty()) {
                            view.showData(data)
                        } else {
                            view.showEmptyList()
                        }*/
                       // Log.v("URL", "" + call.request().url().toString())
                        var routelist = ArrayList<LatLng>()
                        if (data!!.size > 0) {
                            var decodelist: ArrayList<LatLng>
                            val routeA = data!!.get(0)
                            Log.v("Count Routes", "" + data.size)
                            if (routeA.legs.size > 0) {
                                for (k in 0..routeA.legs.size - 1) {
                                    val steps = routeA.getLegs().get(k).getSteps()
                                    var step: Steps
                                    var location: Location
                                    var polyline: String
                                    for (i in steps.indices) {
                                        step = steps[i]
                                        location = step.start_location
                                        //Log.v("1111", step.getStart_location().getLat() + "," + step.getStart_location().getLng());
                                        routelist.add(LatLng(location.lat, location.lng))
                                        polyline = step.getPolyline().getPoints()
                                        decodelist = RouteDecode.decodePoly(polyline)
                                        routelist.addAll(decodelist)
                                        location = step.getEnd_location()
                                        step.html_instructions=step.html_instructions.replace("<b>","")
                                        step.html_instructions=step.html_instructions.replace("</b>","")
                                        Log.v("Haha",step.html_instructions)
                                        //Log.v("2222", step.getEnd_location().getLat() + "," + step.getEnd_location().getLng());
                                        routelist.add(LatLng(location.getLat(), location.getLng()))
                                    }
                                }
                            }
                        }
                        Log.v("COUNT DIRECTION MAKE", "" + routelist.size)


                    }
                })
    }

    fun getCurrLocation(): elezov.com.routeplanner.model.data.Place.Place
    {
        var myLocation: elezov.com.routeplanner.model.data.Place.Place?=elezov.com.routeplanner.model.data.Place.Place()
        myLocation!!.name="My current location"

        myLocation!!.placeId="111"

        getCurrLatLon()
        //Log.v("CurrLoc",latitude.toString()+"  "+longitude.toString())
        var geometry: elezov.com.routeplanner.model.data.Place.Geometry?= Geometry()
        var location: elezov.com.routeplanner.model.data.Place.Location?= elezov.com.routeplanner.model.data.Place.Location()
        location!!.lat=latitude!!
        location!!.lng=longitude!!
        geometry!!.location=location
        myLocation.geometry=geometry

        return myLocation
    }

    fun getCurrLatLon() {
        var locationManager = context!!.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        var loc = locationManager!!.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
        if (loc == null)
            loc = locationManager!!.getLastKnownLocation(LocationManager.GPS_PROVIDER)

        if (locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager!!.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            try {
                if (loc!!.getProvider() == LocationManager.NETWORK_PROVIDER ||
                        loc!!.getProvider() == LocationManager.GPS_PROVIDER) {
                    latitude = loc!!.getLatitude()
                    longitude = loc!!.getLongitude()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Включите GPS", Toast.LENGTH_LONG).show()
            }

        } else {
            Toast.makeText(context, "Включите GPS", Toast.LENGTH_LONG).show()
        }
    }

    fun initLocation(data: List<Place>){
        var currLocation=getCurrLocation()

        val size = data.size

        var lat1 = currLocation.geometry.location.lat;
        var lon1 = currLocation.geometry.location.lng
        var lat2 = lat1; var lon2=lon1

        for (i in 0..size - 1) {
            Log.v("WayPoints", data[i].name.toString())
        }

        if (size > 2) {
            waypoint = "optimize:true|"
            for (i in 0..size - 1) {
                if (i == size - 1) {
                    waypoint += data[i].latLng.latitude.toString() +
                            "," + data[i].latLng.longitude.toString()
                } else {
                    waypoint += data[i].latLng.latitude.toString() +
                            "," + data[i].latLng.longitude.toString() + "|"
                }
            }
        }

        Log.v("WAYPOINT", waypoint)
        origin=lat1!!.toString() + "," + lon1!!.toString()
        destination=lat2!!.toString() + "," + lon2!!.toString()
    }

}