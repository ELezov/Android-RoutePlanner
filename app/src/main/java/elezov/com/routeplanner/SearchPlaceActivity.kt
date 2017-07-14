
package elezov.com.routeplanner

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.util.Log
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.places.Place
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment
import com.google.android.gms.location.places.ui.PlaceSelectionListener
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.SphericalUtil
import elezov.com.routeplanner.View.MainActivity

class SearchPlaceActivity : AppCompatActivity(),PlaceSelectionListener {


    lateinit var utils: Utils

    lateinit var mapView: MapView
    lateinit var rv: RecyclerView
    lateinit var adapter:RecyclerPlacesAdapter

    lateinit var locationManager: LocationManager
    internal var loc: Location? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_place)
        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        /*val fab = findViewById(R.id.fab) as FloatingActionButton
        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }*/

        utils = Utils.getInstance()



        mapView=findViewById(R.id.search_map) as MapView
        mapView!!.onCreate(savedInstanceState)
        mapView!!.onResume()

        MapsInitializer.initialize(applicationContext)

        val autocompleteFragment = fragmentManager.findFragmentById(R.id.autocomplete_fragment) as PlaceAutocompleteFragment

        locationManager = applicationContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        loc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
        if (loc == null)
            loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)

        var latlng= LatLng(loc!!.latitude,loc!!.longitude)
        var latLngBounds=toBounds(latlng,50000.0);



        autocompleteFragment.setOnPlaceSelectedListener(this)
        autocompleteFragment.setBoundsBias(latLngBounds)
        rv=findViewById(R.id.place_list_recycler) as RecyclerView
        rv.layoutManager= LinearLayoutManager(applicationContext)
        adapter= RecyclerPlacesAdapter()
        adapter.addToData(utils.getPlaceList())
        adapter.onButtonClickListener={ position ->
            val selectP = utils.getPlaceList()
            selectP.removeAt(position)
            utils.setPlaceList(selectP)
            adapter.notifyDataSetChanged()
        }
        adapter.notifyDataSetChanged()
        rv.adapter=adapter
    }

    override fun onPlaceSelected(place: Place) {
        mapView.getMapAsync(OnMapReadyCallback { mMap->

            mMap!!.clear()
            var currPlace=place.latLng

            if (Build.VERSION.SDK_INT >= 23) {

                if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                        || checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                    mMap.isMyLocationEnabled=true
                    mMap.uiSettings.isCompassEnabled=true
                    mMap.uiSettings.isMyLocationButtonEnabled=true
                }
            }
            else
            {

            }
            mMap.addMarker(MarkerOptions()
                    .title(place.name.toString()
                    )
                    .position(
                            currPlace)
                    .snippet(place.address.toString())
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)))
            var cameraUpdate = CameraUpdateFactory.newLatLngZoom(currPlace, 12f)
            mMap.animateCamera(cameraUpdate)

        })

        utils.addToList(place)
        adapter.notifyDataSetChanged()

        val result = utils.getPlaceList()
        for (i in result.indices) {
            Log.v("Place", result[i].name.toString())
        }
    }

    override fun onError(p0: Status?) {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun toBounds(center: LatLng, radius: Double): LatLngBounds {
        val southwest = SphericalUtil.computeOffset(center, radius * Math.sqrt(2.0), 225.0)
        val northeast = SphericalUtil.computeOffset(center, radius * Math.sqrt(2.0), 45.0)
        return LatLngBounds(southwest, northeast)
    }

    override fun onBackPressed() {
        var intent= Intent(applicationContext, MainActivity::class.java)
        startActivity(intent)
        super.onBackPressed()
    }

}
