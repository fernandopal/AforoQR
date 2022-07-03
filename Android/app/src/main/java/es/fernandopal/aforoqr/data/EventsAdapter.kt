package es.fernandopal.aforoqr.data
/**
 * Copyright (c) 2022 Fernando Palomo <mail@fernandopal.es>.
 * All rights reserved.
 */

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import es.fernandopal.aforoqr.MainActivity
import es.fernandopal.aforoqr.R
import es.fernandopal.aforoqr.data.model.Event
import es.fernandopal.aforoqr.ui.fragment.NewReservationFragment
import org.greenrobot.eventbus.EventBus


class EventsAdapter(private val events: List<Event>, val context: Context) : RecyclerView.Adapter<EventsAdapter.ViewHolder>() {
    private val eventBus: EventBus = EventBus.getDefault()

    // fpalomo - Creamos la nueva vista (Resultado de busqueda evento)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.rv_item_event, parent, false)
        return ViewHolder(view)
    }

    // fpalomo - Devuelve el número de elementos en la lista
    override fun getItemCount(): Int {
        return events.size
    }

    // fpalomo - Vincula los elementos recibidos al recyclerview
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val event = events[position]
        val (root, title, description) = holder

        title.text = event.name

        root.setOnClickListener {
            eventBus.postSticky(MainActivity.RequestFragmentChangeEvent(NewReservationFragment(null, event)))
        }

        val rDate = event.startDate
        val rLocation = event.room?.address ?: event.room?.address
        val rAddress = "${rLocation?.street}, ${rLocation?.buildingNumber}"
        description.text = "$rAddress (${rDate?.dayOfMonth}/${rDate?.monthValue}/${rDate?.year})"

    }

    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        operator fun component1(): MaterialCardView = itemView.findViewById(R.id.event_item_layout)
        operator fun component2(): TextView = itemView.findViewById(R.id.item_text)
        operator fun component3(): TextView = itemView.findViewById(R.id.item_description)
    }
}