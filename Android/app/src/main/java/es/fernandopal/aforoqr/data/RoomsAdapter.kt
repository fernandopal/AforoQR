/**
 * Copyright (c) 2022 Fernando Palomo <mail@fernandopal.es>.
 * All rights reserved.
 */
package es.fernandopal.aforoqr.data

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import es.fernandopal.aforoqr.MainActivity
import es.fernandopal.aforoqr.R
import es.fernandopal.aforoqr.data.model.Room
import es.fernandopal.aforoqr.ui.fragment.NewReservationFragment
import es.fernandopal.aforoqr.util.NetworkUtils
import org.greenrobot.eventbus.EventBus
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class RoomsAdapter(private val rooms: List<Room>, val showDeleteBtn: Boolean, val context: Context) : RecyclerView.Adapter<RoomsAdapter.ViewHolder>() {
    private val eventBus: EventBus = EventBus.getDefault()
    private val netUtils: NetworkUtils = NetworkUtils()
    private val auth = netUtils.getAuth()
    private val api = netUtils.getApi()

    // create new views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.rv_item_room_user_generic, parent, false)
        return ViewHolder(view)
    }

    // Return the number of the items in the list
    override fun getItemCount(): Int {
        return rooms.size
    }

    // Bind list items to a new view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val room = rooms[position]
        val (root, title, description, btnDelete) = holder

        if (showDeleteBtn) {
            btnDelete.visibility = View.VISIBLE
            btnDelete.setOnClickListener {
                if (room.id != null) {
                    netUtils.requestWithAuth { token, _ ->
                        api.deleteRoom(token, room.id)!!.enqueue(object : Callback<String> {
                            override fun onFailure(call: Call<String>, t: Throwable) {
                                Log.e("RoomsAdapter", t.stackTraceToString())
                            }

                            @SuppressLint("NotifyDataSetChanged")
                            override fun onResponse(call: Call<String>, response: Response<String>) {
                                if (!response.isSuccessful) {
                                    Toast.makeText(context, response.message(), Toast.LENGTH_LONG).show()
                                }
                                notifyDataSetChanged()
                            }
                        })
                    }
                }
            }
        }

        title.text = room.name

        if (!showDeleteBtn) {
            root.setOnClickListener {
                eventBus.postSticky(MainActivity.RequestFragmentChangeEvent(NewReservationFragment(room, null)))
            }
        }

        description.text = room.address.toString()

    }

    // componentX -> Kotlin allows to destructure declarations if that element follows the defined naming conventions
    // https://kotlinlang.org/docs/destructuring-declarations.html
    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        operator fun component1(): MaterialCardView = itemView.findViewById(R.id.room_item_layout)
        operator fun component2(): TextView = itemView.findViewById(R.id.item_text)
        operator fun component3(): TextView = itemView.findViewById(R.id.item_description)
        operator fun component4(): ImageView = itemView.findViewById(R.id.item_btn_delete)
    }
}