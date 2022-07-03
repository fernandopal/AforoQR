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
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import es.fernandopal.aforoqr.R
import es.fernandopal.aforoqr.data.model.ReservationTimeWrapper
import es.fernandopal.aforoqr.ui.fragment.HomeFragment
import es.fernandopal.aforoqr.ui.fragment.NewReservationFragment
import org.greenrobot.eventbus.EventBus


class NewReservationAvailableTimesAdapter(private val reservations: ArrayList<ReservationTimeWrapper>, val context: Context) : RecyclerView.Adapter<NewReservationAvailableTimesAdapter.ViewHolder>() {
    private val eventBus: EventBus = EventBus.getDefault()
    private var currentSelection = 0
    private var performedFirstClick = false

    // fpalomo - Creamos la nueva vista (Reservas del usuario)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.fragment_reservation_available_time, parent, false)
        return ViewHolder(view)
    }

    // fpalomo - Devuelve el número de elementos en la lista
    override fun getItemCount(): Int {
        return reservations.size
    }

    // fpalomo - Vincula los elementos recibidos al recyclerview
    @SuppressLint("NotifyDataSetChanged", "SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val wrapper = reservations[position]
        val view = holder.itemView

        val touchZone = view.findViewById<LinearLayout>(R.id.item_base_layout)
        val time = view.findViewById<TextView>(R.id.reservation_item_time)
        val capacity = view.findViewById<TextView>(R.id.reservation_item_capacity)

        val rUsedCap = wrapper.usedCap
        val rMaxCap = wrapper.maxCap

        // Asignamos los valores y estilos a los TextView de los items
        if (rUsedCap >= rMaxCap) {
            capacity.text = "COMPLETO"
            capacity.setTextColor(
                ContextCompat.getColor(
                    view.context,
                    R.color.aforoqr_orange_400
                )
            )
        } else {
            capacity.text = "$rUsedCap / $rMaxCap"
            capacity.setTextColor(
                ContextCompat.getColor(
                    view.context,
                    R.color.aforoqr_white_500
                )
            )
        }

        time.text = wrapper.hour

        if (performedFirstClick) {
            touchZone.background = if (currentSelection == position) ContextCompat.getDrawable(
                context,
                R.drawable.time_list_selected_item_border
            ) else null
        }

        view.setOnClickListener {
            performedFirstClick = true
            notifyItemChanged(currentSelection)
            currentSelection = holder.adapterPosition

            val parsedHour = wrapper.hour.split(':')[0].toInt()

            Log.d(
                "NewReservationAvailableTimesAdapter",
                "POST :: UpdateSelectedTimeEvent($parsedHour, 0))"
            )
            eventBus.postSticky(NewReservationFragment.UpdateSelectedTimeEvent(parsedHour, 0))
            notifyItemChanged(currentSelection)
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        override fun onClick(v: View) {
            if (adapterPosition == RecyclerView.NO_POSITION) return
        }

        init {
            itemView.setOnClickListener(this)
        }
    }
}