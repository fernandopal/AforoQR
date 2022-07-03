package es.fernandopal.aforoqr.ui.fragment
 /**
 * Copyright (c) 2022 Fernando Palomo <mail@fernandopal.es>.
 * All rights reserved.
 */

import android.content.Context
import android.graphics.RenderEffect
import android.graphics.Shader
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import es.fernandopal.aforoqr.MainActivity
import es.fernandopal.aforoqr.data.ReservationsAdapter
import es.fernandopal.aforoqr.data.model.Reservation
import es.fernandopal.aforoqr.databinding.FragmentHomeBinding
import es.fernandopal.aforoqr.util.Const
import es.fernandopal.aforoqr.util.NetworkUtils
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDateTime


class HomeFragment : BottomSheetDialogFragment() {
    private val eventBus: EventBus = EventBus.getDefault()
    private val netUtils: NetworkUtils = NetworkUtils()
    private val auth = netUtils.getAuth()
    private val api = netUtils.getApi()
    private lateinit var binding: FragmentHomeBinding
    private lateinit var linearLayoutManager: LinearLayoutManager

    override fun onAttach(context: Context) {
        super.onAttach(context)
        eventBus.register(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        eventBus.unregister(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        binding.profileName.text = auth.currentUser?.displayName?.split(" ")?.get(0) ?: "Error"
        binding.profileEmail.text = auth.currentUser?.email

        // Load api data
        eventBus.postSticky(MainActivity.RefreshDataEvent())

        binding.homeLayoutRefresh.setOnRefreshListener {
            eventBus.postSticky(MainActivity.RefreshDataEvent())
        }

        return binding.root
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.BACKGROUND)
    fun onRefreshDataReceived(event: MainActivity.RefreshDataEvent?) {
        if (this::binding.isInitialized) {
            setRefreshing(true)

            netUtils.requestWithAuth(
                onError = {
                    eventBus.postSticky(MainActivity.SendErrorToBusEvent(Const.GS_NOT_ALIVE_ERROR))
                },
                onSuccess = { token, uid ->
                    api.getUserReservations(token, uid)!!.enqueue(object : Callback<List<Reservation>?> {
                        override fun onFailure(call: Call<List<Reservation>?>, t: Throwable) {
                            Log.e("ERROR", t.stackTraceToString())
                            eventBus.postSticky(MainActivity.SendErrorToBusEvent(Const.API_NOT_ALIVE_ERROR))
                            setRefreshing(false)
                        }

                        override fun onResponse(call: Call<List<Reservation>?>, res: Response<List<Reservation>?>) {
                            if (!res.isSuccessful) {
                                eventBus.postSticky(MainActivity.ApiHeartbeatEvent())
                            }

                            if (res.isSuccessful) {
                                val now = LocalDateTime.now()
                                // fpalomo - Condiciones para filtrar las reservas que se muestran
                                val reservations = res.body() ?.filter { r ->
                                    r.startDate != null &&
                                    r.endDate != null &&
                                    !r.cancelled
                                } as List<Reservation>

                                binding.homeReservationList.apply {
                                    setHasFixedSize(true)
                                    layoutManager = LinearLayoutManager(context)
                                    adapter = ReservationsAdapter(reservations, context)
                                }

                                setRefreshing(false)
                            }
                        }
                    })
                }
            )
        }
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    fun onChangeBlockingStatus(event: ChangeBlockingStatusEvent) {
        setRefreshing(event.isBlocking)
    }

    fun setRefreshing(isRefreshing: Boolean) {
        if (this::binding.isInitialized) {
            val blur = RenderEffect.createBlurEffect(16F, 16F, Shader.TileMode.MIRROR)
            binding.root.setRenderEffect(if (isRefreshing) blur else null)

            binding.homeLayoutRefresh.post {
                binding.homeLayoutRefresh.isRefreshing = isRefreshing
            }
        }
    }

    // Eventos registrados por HomeFragment
    class ChangeBlockingStatusEvent(var isBlocking: Boolean)
}