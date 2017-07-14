package elezov.com.routeplanner.View

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.google.android.gms.maps.*
import android.location.LocationManager
import android.os.Build
import android.support.design.widget.Snackbar
import android.util.Log

import com.google.android.gms.location.places.Place
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng

import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions

import elezov.com.routeplanner.Instruction.DirectionInstructionActivity
import elezov.com.routeplanner.Presenter.RouteListPresenter
import elezov.com.routeplanner.R
import elezov.com.routeplanner.R.layout.activity_main
import elezov.com.routeplanner.SearchPlaceActivity
import elezov.com.routeplanner.Utils

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import java.io.UnsupportedEncodingException
import java.util.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, View {


    internal var map: MapView? = null
    var googleMap: GoogleMap? = null
    lateinit var utils: Utils
    lateinit private var presenter: RouteListPresenter


    override fun showDirection(routeList: ArrayList<LatLng>) {
        map!!.getMapAsync(OnMapReadyCallback { map->
            if (routeList!=null){
                Log.v("COUNT Direction", "" + routeList.size)
                if (routeList.size > 0) {
                    val rectLine = PolylineOptions().width(10f).color(
                            Color.RED)

                    for (i in routeList.indices) {
                        rectLine.add(routeList[i])
                    }
                    map.addPolyline(rectLine)
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

    override fun showError(error: String) {
        makeToast(error)
    }

    override fun showEmptyMap() {
        map!!.getMapAsync(OnMapReadyCallback { mMap ->
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

                if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                        || checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                    mMap.isMyLocationEnabled = true
                    mMap.uiSettings.isCompassEnabled = true
                    mMap.uiSettings.isMyLocationButtonEnabled = true
                }
            }
        })
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        presenter = RouteListPresenter(findViewById(R.layout.activity_main),applicationContext,this)
        setSupportActionBar(toolbar)



       fab.setOnClickListener { view ->
           presenter!!.OnGetDirectionButtonClick()
        }


        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.setDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)


        map!!.onCreate(savedInstanceState)
        map!!.onResume()


        MapsInitializer.initialize(applicationContext)

        utils= Utils.getInstance()
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
        if (id== R.id.search_route){
            var intent= Intent(applicationContext, SearchPlaceActivity::class.java)
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
            var intent= Intent(applicationContext, DirectionInstructionActivity::class.java)
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

    private fun makeToast(text: String) {
        Snackbar.make(toolbar, text, Snackbar.LENGTH_LONG).show()
    }

    override fun onStop() {
        super.onStop()
        if (presenter != null) {
            presenter!!.OnStop()
        }
    }








}
