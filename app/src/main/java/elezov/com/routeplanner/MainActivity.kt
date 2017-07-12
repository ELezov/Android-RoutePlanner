package elezov.com.routeplanner

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.design.widget.NavigationView
import android.support.v4.app.ActivityCompat
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.google.android.gms.maps.*
import android.Manifest
import android.content.Context
import android.graphics.Color
import android.location.LocationManager
import android.os.Build
import android.widget.Toast
import com.google.android.gms.location.places.Place
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import elezov.com.routeplanner.Direction.*
import elezov.com.routeplanner.Instruction.DirectionInstructionActivity
import elezov.com.routeplanner.Place.Geometry
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.UnsupportedEncodingException
import java.util.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {


    internal var mapView: MapView? = null
    var googleMap: GoogleMap? = null

    var locationManager: LocationManager?=null
    internal var loc: android.location.Location? = null
    var longitude: Double? = null
    var latitude: Double? = null

    lateinit var utils:Utils




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        //locationManager = applicationContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager


        val fab = findViewById(R.id.fab) as FloatingActionButton
        fab.setOnClickListener { view ->
            //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
               //     .setAction("Action", null).show()





            build_retrofit_and_get_duration_in_response(utils.getPlaceList())

        }

        val drawer = findViewById(R.id.drawer_layout) as DrawerLayout
        val toggle = ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer.setDrawerListener(toggle)
        toggle.syncState()

        val navigationView = findViewById(R.id.nav_view) as NavigationView
        navigationView.setNavigationItemSelectedListener(this)

        mapView=findViewById(R.id.map) as MapView
        mapView!!.onCreate(savedInstanceState)
        mapView!!.onResume()

        MapsInitializer.initialize(applicationContext)

        utils= Utils.getInstance()

        mapView!!.getMapAsync(OnMapReadyCallback { mMap ->
            googleMap = mMap
            googleMap!!.clear()

            var result:List<Place>
            result=utils.getPlaceList()
            if (result.size>0) {
                for (i in 0..result.size - 1) {
                    mMap.addMarker(MarkerOptions()
                            .title(result[i].name.toString()
                            )
                            .position(
                                    result[i].latLng)
                            .snippet(result[i].address.toString())
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)))
                }
                var cameraUpdate = CameraUpdateFactory.newLatLngZoom(result[0].latLng, 12f)
                mMap.animateCamera(cameraUpdate)
            }




            if (Build.VERSION.SDK_INT >= 23) {

                if (checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                        || checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                    mMap.isMyLocationEnabled = true
                    mMap.uiSettings.isCompassEnabled = true
                    mMap.uiSettings.isMyLocationButtonEnabled = true
                }
            }
        })

    }

    override fun onBackPressed() {
        val drawer = findViewById(R.id.drawer_layout) as DrawerLayout
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId


        if (id == R.id.action_settings) {
            return true
        }
        if (id==R.id.search_route){
            var intent= Intent(applicationContext,SearchPlaceActivity::class.java)
            startActivity(intent)
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        val id = item.itemId

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {
            var intent=Intent(applicationContext,DirectionInstructionActivity::class.java)
            startActivity(intent)

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        val drawer = findViewById(R.id.drawer_layout) as DrawerLayout
        drawer.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun build_retrofit_and_get_duration_in_response(data: List<Place>) {
        val url = "https://maps.googleapis.com/maps/"
        val retrofit = Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        val service = retrofit.create<DirectionAPI>(DirectionAPI::class.java!!)
        Log.v("COUNT DATA", "" + data.size)
        var currLocation=getCurrLocation();

        val size = data.size

        var lat1 = currLocation.geometry.location.lat; var lon1 = currLocation.geometry.location.lng
        var lat2 = lat1; var lon2=lon1

        for (i in 0..size - 1) {
            Log.v("WayPoints", data[i].name.toString())
        }
        var waypoint = ""
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

        val call = service.getJson(lat1!!.toString() + "," + lon1!!.toString(), lat2!!.toString() + "," + lon2!!.toString(), waypoint)
        Log.v("URL", call.request().url().toString())
        call.enqueue(object : Callback<DirectionResults> {

            override fun onResponse(call: Call<DirectionResults>, response: Response<DirectionResults>) {
                Log.v("URL", "" + call.request().url().toString())
                val directionResults = response.body()
                var routelist = ArrayList<LatLng>()
                if (directionResults.routes.size > 0) {
                    var decodelist: ArrayList<LatLng>
                    val routeA = directionResults.getRoutes().get(0)
                    Log.v("Count Routes", "" + directionResults.routes.size)
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
                mapView!!.getMapAsync(OnMapReadyCallback { mMap->
                    if (routelist != null) {
                        Log.v("COUNT Direction", "" + routelist.size)
                        if (routelist.size > 0) {
                            val rectLine = PolylineOptions().width(10f).color(
                                    Color.RED)

                            for (i in routelist.indices) {
                                rectLine.add(routelist[i])
                            }
                            mMap.addPolyline(rectLine)
                        }
                    }
                })

                try {
                    // Convert from Unicode to UTF-8
                    var string = "abc\u5639\u563b"
                    var utf8 = string.toByteArray(charset("UTF-8"))

                    // Convert from UTF-8 to Unicode
                    string = kotlin.text.String(utf8)
                    Log.v("DIRECTIONS",string)
                           // String(utf8, "UTF-8")

                } catch (e: UnsupportedEncodingException) {
                }


            }

            override fun onFailure(call: Call<DirectionResults>, t: Throwable) {

            }
        })
    }

    fun getCurrLatLon() {
        locationManager = applicationContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION),
                    1)
        } else {
            Log.e("DB", "PERMISSION GRANTED")
        }
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
                Toast.makeText(this, "Включите GPS", Toast.LENGTH_LONG).show()
            }

        } else {
            Toast.makeText(this, "Включите GPS", Toast.LENGTH_LONG).show()
        }
    }

    fun getCurrLocation():elezov.com.routeplanner.Place.Place
    {
        var myLocation:elezov.com.routeplanner.Place.Place?=elezov.com.routeplanner.Place.Place()
        myLocation!!.name="My current location"
        myLocation!!.placeId="111"

        getCurrLatLon()
        Log.v("CurrLoc",latitude.toString()+"  "+longitude.toString())
        var geometry:Geometry?= Geometry()
        var location:elezov.com.routeplanner.Place.Location?=elezov.com.routeplanner.Place.Location()
        location!!.lat=latitude!!
        location!!.lng=longitude!!
        geometry!!.location=location
        myLocation.geometry=geometry

        return myLocation
    }


}
