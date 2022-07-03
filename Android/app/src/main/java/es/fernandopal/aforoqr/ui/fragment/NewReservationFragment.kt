package es.fernandopal.aforoqr.ui.fragment
/**
 * Copyright (c) 2022 Fernando Palomo <mail@fernandopal.es>.
 * All rights reserved.
 */

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import android.graphics.RenderEffect
import android.graphics.Shader
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import es.fernandopal.aforoqr.MainActivity
import es.fernandopal.aforoqr.data.NewReservationAvailableTimesAdapter
import es.fernandopal.aforoqr.data.model.*
import es.fernandopal.aforoqr.databinding.FragmentNewReservationBinding
import es.fernandopal.aforoqr.util.NetworkUtils
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.util.*
import kotlin.collections.ArrayList


class NewReservationFragment(private var room: Room?, private var event: Event?) : BottomSheetDialogFragment() {
    private val eventBus: EventBus = EventBus.getDefault()
    private val netUtils: NetworkUtils = NetworkUtils()
    private val auth = netUtils.getAuth()
    private val api = netUtils.getApi()
    private val cal = Calendar.getInstance()
    private var selectedTime: Int = -1
    private lateinit var binding: FragmentNewReservationBinding
    private lateinit var reservations: ArrayList<ReservationTimeWrapper?>
    private lateinit var nonNullItems: ArrayList<ReservationTimeWrapper>

    // TODO: Add this to admin config
    private val workingHours: WorkingHours = WorkingHours(
        LocalTime.of(8, 0),
        LocalTime.of(18, 0)
    )

    override fun onAttach(context: Context) {
        super.onAttach(context)
        eventBus.register(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        eventBus.unregister(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentNewReservationBinding.inflate(inflater, container, false)

        cal.timeZone = TimeZone.getTimeZone(ZoneId.of("Europe/Madrid"))

        val availableTimes = binding.recyclerViewAvailableTimes

        binding.newReservationDate.setOnClickListener {
            DatePickerDialog(
                binding.root.context,
                datePickerListener,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        binding.newReservationConfirmBtn.setOnClickListener {
            if (selectedTime == -1) {
                Toast.makeText(context, "Debes seleccionar una hora", Toast.LENGTH_LONG).show() // TODO: Translate
            } else {
                if (room?.id == null && event?.id == null) {
                    Toast.makeText(context, "Error desconocido", Toast.LENGTH_LONG).show()
                } else {
                    createReservation(room?.id, event?.id)
                }
            }
        }

        reservations = ArrayList(24)
        nonNullItems = ArrayList(reservations.filterNotNull())

        availableTimes.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = NewReservationAvailableTimesAdapter(nonNullItems, context)
        }

        return binding.root
    }

    private fun createReservation(roomId: Long?, eventId: Long?) {
        setRefreshing(true)

        val calendarDate = LocalDateTime.ofInstant(cal.toInstant(), cal.timeZone.toZoneId())

        netUtils.requestWithAuth { token, uid ->
            val jsonObject = JSONObject()
                .put("userId", uid)
                .put("eventId", eventId)
                .put("roomId", roomId)
                .put("startDate", calendarDate)
                .put("endDate", calendarDate.plusHours(1)) // Las reservas son periodos de 1 hora

            val requestBody = jsonObject.toString().toRequestBody("application/json".toMediaTypeOrNull())
            Log.d("NewReservationFragment", "Request: $jsonObject")

            api.createReservation(token, requestBody)!!.enqueue(object: Callback<Long?> {
                override fun onFailure(call: Call<Long?>, t: Throwable) {
                    Log.e("NewReservationFragment", t.stackTraceToString())
                    Toast.makeText(context, t.message, Toast.LENGTH_LONG).show()
                    eventBus.postSticky(MainActivity.RequestFragmentChangeEvent(HomeFragment()))
                }

                override fun onResponse(call: Call<Long?>, res: Response<Long?>) {
                    if (res.isSuccessful && res.body() != null) {
                        setRefreshing(false)
                        eventBus.postSticky(MainActivity.RequestFragmentChangeEvent(ReservationDetailsFragment(res.body()!!.toLong())))
                    }
                }
            })
        }
    }

    private fun retrieveAvailableTimes(date: LocalDateTime, roomId: Long?, eventId: Long?) {
        setRefreshing(true)

        val now = LocalDateTime.now()
        // val queryStartDate = date.withHour(23).withMinute(59)
        // val queryEndDate = date.plusDays(1).withHour(0).withMinute(0)

        // Sea o no exitosa la respuesta, borramos la lista de reservas que teníamos antes para evitar duplicados
        reservations.clear()
        nonNullItems.clear()

        netUtils.requestWithAuth { token, uid ->
            val jsonObject = JSONObject()
                .put("userId", uid)
                .put("eventId", eventId)
                .put("roomId", roomId)
                .put("startDate", null)
                .put("endDate", null)


            val requestBody = jsonObject.toString().toRequestBody("application/json".toMediaTypeOrNull())
            Log.d("NewReservationFragment", "Request: $jsonObject")

            api.getReservationComplex(token, requestBody)!!.enqueue(object: Callback<List<Reservation>?> {
                override fun onFailure(call: Call<List<Reservation>?>, t: Throwable) {
                    Log.e("NewReservationFragment", t.stackTraceToString())
                }

                @SuppressLint("NotifyDataSetChanged")
                override fun onResponse(call: Call<List<Reservation>?>, res: Response<List<Reservation>?>) {
                    if (res.isSuccessful && room != null) {
                        val rMaxCap: Int = if(room!!.capacity != null && room!!.capacity!! > 0) room!!.capacity!! else 0

                        // Rellenamos la lista de posibles reservas con un hueco por hora
                        Log.d("NewReservationFragment", "Populating array")
                        for (hora in 0..23) {
                            val startDate = (if(hora < 10) "0$hora" else hora).toString()

                            // Si se selecciona el día de hoy, evitamos que se añadan las horas que ya han pasado y las que no estén entre las horas de apertura
                            // Para las horas de apertura lo condicionamos con un > en lugar de >= para evitar que puedan hacer reservas que se salgan del horario
                            val inWorkHours = (hora >= workingHours.from.hour && hora < workingHours.to.hour)
                            val xpr = ((isSameDay(date, now) && hora >= now.hour) || !isSameDay(date, now) || hora == 0) && inWorkHours

                            if (xpr) {
                                val reservationWrapper = ReservationTimeWrapper("$startDate:00", 0, rMaxCap)
                                nonNullItems.add(reservationWrapper)
                                reservations.add(reservationWrapper)
                            } else {
                                reservations.add(null)
                            }
                        }
                        Log.d("NewReservationFragment", "NonNullItems Array size 1: ${nonNullItems.size}")

                        // Si el resultado de la respuesta es OK (200), calculamos la capacidad usada para la sala
                        // en cada una de los horarios habilitados
                        Log.d("NewReservationFragment", "Response: ${res.body()}")
                        val filteredResponse = res.body()?.filter { r ->
                            r.startDate != null &&
                            r.startDate.isBefore(LocalDateTime.now().withHour(23).withMinute(59)) &&
                            r.endDate?.isAfter(r.startDate) == true &&
                            r.endDate.isBefore(date.withHour(23).withMinute(59)) && // TODO: NO FILTRA HORAS BIEN
                            r.confirmed &&
                            !r.cancelled
                        }

                        Log.d("NewReservationFragment", "filteredResponse: ${res.body()}")
                        Log.d("NewReservationFragment", "filteredResponse: ${filteredResponse?.size}")

                        filteredResponse?.forEach { r ->
                            if (!r.cancelled && r.startDate != null && (r.room != null || r.event?.room != null)) {
                                val isSameDay = isSameDay(r.startDate, date)
                                val rStartDateHour = r.startDate.hour
                                if ((isSameDay && rStartDateHour >= now.hour) || !isSameDay && reservations[rStartDateHour] != null && reservations[rStartDateHour]?.usedCap != null && reservations[rStartDateHour]?.hour != null) {
                                    val startDate = (if(rStartDateHour < 10) "0$rStartDateHour" else rStartDateHour).toString()

                                    reservations[rStartDateHour]!!.usedCap = reservations[rStartDateHour]!!.usedCap + 1
                                    reservations[rStartDateHour]!!.hour = "$startDate:00"
                                }
                            }
                        }

                        nonNullItems = ArrayList(reservations.filterNotNull())
                        Log.d("NewReservationFragment", "NonNullItems Array size 2: ${nonNullItems.size}")
                        Log.d("NewReservationFragment", "NonNullItems Array: $nonNullItems")
                        binding.recyclerViewAvailableTimes.visibility = View.VISIBLE
                        binding.recyclerViewAvailableTimes.adapter?.notifyDataSetChanged()
                        setRefreshing(false)
                    }
                }
            })
        }
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    fun updateSelectedTime(event: UpdateSelectedTimeEvent) {
        cal.set(Calendar.HOUR, event.hours)
        cal.set(Calendar.MINUTE, event.minutes)

        selectedTime = event.hours
    }

    private val datePickerListener = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
        cal.set(Calendar.YEAR, year)
        cal.set(Calendar.MONTH, monthOfYear)
        cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
        cal.set(Calendar.HOUR, 0)
        cal.set(Calendar.MINUTE, 0)

        val now = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59)
        val selected = LocalDateTime.of(year, monthOfYear+1, dayOfMonth, 0, 0)

        if (selected.isBefore(now) && !isSameDay(selected, now)) {
            Toast.makeText(context, "No puedes seleccionar una fecha anterior", Toast.LENGTH_LONG).show() // TODO: Translate
        } else {
            val localDateTime = LocalDateTime.ofInstant(cal.toInstant(), cal.timeZone.toZoneId())

            // Recuperamos las reservas existentes para la sala/evento sobre el que el usuario quiere realizar
            // la reserva, una vez tengamos las reservas -> generamos la lista de horas disponibles segun el aforo
            retrieveAvailableTimes(localDateTime, room?.id, event?.id)

            updateUi()
        }
    }

    private fun isSameDay(date1: LocalDateTime, date2: LocalDateTime): Boolean {
        return date1.year == date2.year && date1.dayOfMonth == date2.dayOfMonth && date1.month == date2.month
    }

    private fun updateUi() {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale("es", "ES"))
        binding.newReservationDate.text = Editable.Factory.getInstance().newEditable(dateFormat.format(cal.time))
    }

    private fun setRefreshing(isRefreshing: Boolean) {
        if (this::binding.isInitialized) {
            val blur = RenderEffect.createBlurEffect(16F, 16F, Shader.TileMode.MIRROR)
            binding.root.setRenderEffect(if (isRefreshing) blur else null)
        }
    }

    // Eventos registrados por NewReservationFragment
    class UpdateSelectedTimeEvent(var hours: Int = 0, var minutes: Int = 0)
}