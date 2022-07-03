/**
 * Copyright (c) 2022 Fernando Palomo <mail@fernandopal.es>.
 * All rights reserved.
 */
package es.fernandopal.aforoqr.data

import android.content.Context
import android.graphics.PorterDuff
import android.transition.Fade
import android.transition.TransitionManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import es.fernandopal.aforoqr.MainActivity
import es.fernandopal.aforoqr.R
import es.fernandopal.aforoqr.data.model.Reservation
import es.fernandopal.aforoqr.ui.fragment.HomeFragment
import es.fernandopal.aforoqr.util.InterfaceUtils
import es.fernandopal.aforoqr.util.NetworkUtils
import org.greenrobot.eventbus.EventBus
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDateTime

class ReservationsAdapter(private val reservations: List<Reservation>, val context: Context) : RecyclerView.Adapter<ReservationsAdapter.ViewHolder>() {
    private val eventBus: EventBus = EventBus.getDefault()
    private val netUtils: NetworkUtils = NetworkUtils()
    private val api = netUtils.getApi()


    // fpalomo - Creamos la nueva vista (Reservas del usuario)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.home_reservation_list_item, parent, false)
        return ViewHolder(view)
    }

    // fpalomo - Devuelve el número de elementos en la lista
    override fun getItemCount(): Int {
        return reservations.size
    }

    // fpalomo - Vincula los elementos recibidos al recyclerview
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val reservation = reservations[position]
        val (image, title, description, dateDetail, btnExpand, btnCancel, btnDelete, btnSendConfirmation, itemActionsLayout, itemBaseLayout) = holder

        // fpalomo - Añadimos un icono al elemento
        image.setImageResource(R.drawable.ic_calendar)
        image.setColorFilter(context.getColor(R.color.aforoqr_orange_400), PorterDuff.Mode.SRC_IN)

        // fpalomo - Añadimos el título al elemento
        title.text = reservation.room?.name ?: reservation.event?.name ?: "Error"

        // fpalomo - Tiempo de creación de la reserva + 15 minutos > tiempo actual = reserva no confirmada expirada
        val expired =
            if (reservation.confirmationDate == null)
                if (reservation.requestDate == null) true
                else reservation.requestDate.plusMinutes(15).isAfter(LocalDateTime.now())
            else false

        // fpalomo - Añadimos la descripción al elemento
        val rLocation = reservation.room?.address ?: reservation.event?.room?.address
        val rAddress = "${rLocation?.street}, ${rLocation?.buildingNumber}"

        if (reservation.confirmed) {
            val startDate = reservation.startDate
            val endDate = reservation.endDate
            description.text = rAddress
            dateDetail.text = if(startDate != null && endDate != null)
                InterfaceUtils().dateToString(startDate, includeDate = true, includeTime = false) +
                " (" +
                    InterfaceUtils().dateToString(startDate, includeDate = false, includeTime = true) +
                    " - " +
                    InterfaceUtils().dateToString(endDate, includeDate = false, includeTime = true) +
                ")"
            else "Error"
        } else {
            description.text = rAddress
            dateDetail.text = if (expired) "RESERVA EXPIRADA" else "PENDIENTE CONFIRMAR"
            dateDetail.setTextColor(
                if (expired) ContextCompat.getColor(context, R.color.aforoqr_orange_400)
                else ContextCompat.getColor(context, R.color.aforoqr_yellow_400)
            )
        }

        // fpalomo - Visibilidad de los botones de acción según el estado de la reserva
        if (reservation.confirmed) {
            btnCancel.visibility = View.VISIBLE
        } else if (!reservation.confirmed && !expired) {
            btnCancel.visibility = View.VISIBLE
            btnSendConfirmation.visibility = View.VISIBLE
        } else if (expired) {
            btnDelete.visibility = View.VISIBLE
        }

        // fpalomo - onClickListener para el botón que expande/contrae el menú de acciones
        btnExpand.setOnClickListener {
            TransitionManager.beginDelayedTransition(itemBaseLayout, Fade())

            val isVisible = itemActionsLayout.visibility == View.VISIBLE
            val visibility = if(isVisible) View.GONE else View.VISIBLE
            val drawable = if(isVisible) R.drawable.ic_caret_down else R.drawable.ic_caret_up

            itemActionsLayout.visibility = visibility
            btnExpand.setImageResource(drawable)
        }

        // fpalomo - onClickListener para el botón de cancelación
        btnCancel.setOnClickListener {
            setBlockingStatus(true)
            netUtils.requestWithAuth { token, _ ->
                api.cancelReservation(token, reservation.id)!!.enqueue(object: Callback<String?> {
                    override fun onFailure(call: Call<String?>, t: Throwable) {
                        Log.e("ReservationsAdapter", t.stackTraceToString())
                    }

                    override fun onResponse(call: Call<String?>, response: Response<String?>) {
                        Log.d("ReservationsAdapter", response.body().toString())
                        if (response.isSuccessful) {
                            eventBus.postSticky(MainActivity.RefreshDataEvent())
                        }
                    }
                })
            }
        }

        // fpalomo - onClickListener para el botón que elimina una reserva
        btnDelete.setOnClickListener {
            setBlockingStatus(true)

            netUtils.requestWithAuth { token, _ ->
                api.deleteReservation(token, reservation.id)!!.enqueue(object: Callback<String?> {
                    override fun onFailure(call: Call<String?>, t: Throwable) {
                        Log.e("ReservationsAdapter", t.stackTraceToString())
                    }

                    override fun onResponse(call: Call<String?>, response: Response<String?>) {
                        Log.d("ReservationsAdapter", response.body().toString())
                        if (response.isSuccessful) {
                            eventBus.post(MainActivity.RefreshDataEvent())
                            setBlockingStatus(false)
                        }
                    }
                })
            }
        }

        // fpalomo - onClickListener para el botón de reenvio de confirmación
        btnSendConfirmation.setOnClickListener {
            setBlockingStatus(true)
            netUtils.requestWithAuth { token, _ ->
                api.sendConfirmationEmail(token, reservation.id)!!.enqueue(object: Callback<String?> {
                    override fun onFailure(call: Call<String?>, t: Throwable) {
                        Log.e("ReservationsAdapter", t.stackTraceToString())
                    }

                    override fun onResponse(call: Call<String?>, response: Response<String?>) {
                        Log.d("ReservationsAdapter", response.body().toString())
                        if (response.isSuccessful) {
                            eventBus.post(MainActivity.RefreshDataEvent())
                            setBlockingStatus(false)
                        }
                    }
                })
            }
        }
    }

    // fpalomo - Bloquea/desbloquea la vista principal
    private fun setBlockingStatus(blocking: Boolean) {
        eventBus.postSticky(HomeFragment.ChangeBlockingStatusEvent(blocking))
    }

    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        operator fun component1(): ImageView = itemView.findViewById(R.id.item_image)
        operator fun component2(): TextView = itemView.findViewById(R.id.item_text)
        operator fun component3(): TextView = itemView.findViewById(R.id.item_description)
        operator fun component4(): TextView = itemView.findViewById(R.id.item_date_detail)
        operator fun component5(): ImageButton = itemView.findViewById(R.id.item_btn_expand)
        operator fun component6(): Button = itemView.findViewById(R.id.item_btn_cancel)
        operator fun component7(): Button = itemView.findViewById(R.id.item_btn_delete)
        operator fun component8(): Button = itemView.findViewById(R.id.item_btn_send_confirmation)
        operator fun component9(): LinearLayout = itemView.findViewById(R.id.item_collapsed_layout)
        operator fun component10(): LinearLayout = itemView.findViewById(R.id.item_base_layout)
    }
}