package elezov.com.routeplanner

import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.google.android.gms.location.places.Place
import java.util.*

/**
 * Created by USER on 23.03.2017.
 */

class RecyclerPlacesAdapter : RecyclerView.Adapter<RecyclerPlacesAdapter.ViewHolder>() {

    var utils: Utils= Utils.getInstance()

    var data: List<Place> = ArrayList<Place>()

    lateinit var onButtonClickListener:((Int)->Unit)



    fun addToData(items: List<Place>) {
        data = items
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_recycler_place, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.placeNameText.text=data[position].address



        /*holder.v.setOnClickListener { v ->
            val context = v.context
            val intent = Intent(context, PlaceDetailActivity::class.java)
            intent.putExtra(PlaceDetailActivity.PlACE_NAME, data[position].getName())
            intent.putExtra(PlaceDetailActivity.ID_PLACE, data[position].getPlaceId())
            context.startActivity(intent)
        }*/
    }


    override fun getItemCount(): Int {
        return data.size
    }

    inner class ViewHolder(val v: View) : RecyclerView.ViewHolder(v) {
        lateinit var placeNameText: TextView
        lateinit var deleteBtn: Button


        init {
            placeNameText = v.findViewById(R.id.text_item) as TextView
            deleteBtn = v.findViewById(R.id.deleteButton) as Button

            deleteBtn.setOnClickListener(View.OnClickListener {
                onButtonClickListener!!.invoke(adapterPosition)
            })

        }
    }
}