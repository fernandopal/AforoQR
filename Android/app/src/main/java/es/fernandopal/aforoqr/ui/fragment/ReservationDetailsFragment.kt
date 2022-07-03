package es.fernandopal.aforoqr.ui.fragment

import android.graphics.RenderEffect
import android.graphics.Shader
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import es.fernandopal.aforoqr.MainActivity
import es.fernandopal.aforoqr.data.RetrofitClientInstance
import es.fernandopal.aforoqr.data.model.Reservation
import es.fernandopal.aforoqr.data.request.ApiEndpoints
import es.fernandopal.aforoqr.databinding.FragmentReservationDetailsBinding
import es.fernandopal.aforoqr.util.NetworkUtils
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.greenrobot.eventbus.EventBus
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ReservationDetailsFragment(private var reservationId: Long) : BottomSheetDialogFragment() {
    private val netUtils: NetworkUtils = NetworkUtils()
    private val api = netUtils.getApi()
    private val auth = netUtils.getAuth()
    private lateinit var binding: FragmentReservationDetailsBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentReservationDetailsBinding.inflate(inflater, container, false)

        Log.d("ReservationDetailsFragment", "ReservationId: $reservationId")
        retrieveReservation()

        return binding.root
    }



    private fun retrieveReservation(): Reservation? {
        setRefreshing(true)
        binding.tvReservationRoomName.text = "$reservationId"
        binding.tvReservationTimeDisplay.text = "kapasao"

        var reservation: Reservation? = null
        netUtils.requestWithAuth { token, _ ->
            api.getReservation(token, reservationId)!!.enqueue(object: Callback<Reservation?> {
                override fun onFailure(call: Call<Reservation?>, t: Throwable) {
                    Log.e("ReservationDetailsFragment", t.stackTraceToString())
                    Toast.makeText(context, t.message, Toast.LENGTH_LONG).show()
                }

                override fun onResponse(call: Call<Reservation?>, res: Response<Reservation?>) {
                    Log.d("ReservationDetailsFragment", "Response: ${res.body()}")
                    if (res.isSuccessful) {
                        reservation = res.body()

                        val room = res.body()?.room ?: res.body()?.event?.room
                        val date = res.body()?.startDate
                        val roomName = room?.name
                        val address = room?.address

                        binding.tvReservationRoomName.text = "$roomName - ${address.toString()}"
                        binding.tvReservationStartDate.text = "${date?.dayOfMonth}/${date?.monthValue}/${date?.year}"
                        binding.tvReservationTimeDisplay.text = "${if (date?.hour!! < 10) "0${date?.hour}" else date?.hour}:${if (date?.minute!! < 10) "0${date?.minute}" else date?.minute} - ${if (date?.hour.plus(1)!! < 10) "0${date?.hour.plus(1)}" else date?.hour.plus(1)}:${if (date?.minute!! < 10) "0${date?.minute}" else date?.minute}"
                        binding.tvReservationEmailSentText.text = binding.tvReservationEmailSentText.text.toString().replace("{!email}", auth.currentUser?.email!!)
                        setRefreshing(false)
                    }
                }
            })
        }
        return reservation
    }

    fun setRefreshing(isRefreshing: Boolean) {
        if (this::binding.isInitialized) {
            val blur = RenderEffect.createBlurEffect(16F, 16F, Shader.TileMode.MIRROR)
            binding.root.setRenderEffect(if (isRefreshing) blur else null)
        }
    }
}