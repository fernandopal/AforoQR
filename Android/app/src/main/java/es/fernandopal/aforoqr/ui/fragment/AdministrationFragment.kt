package es.fernandopal.aforoqr.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import es.fernandopal.aforoqr.R
import es.fernandopal.aforoqr.data.RoomsAdapter
import es.fernandopal.aforoqr.data.UsersAdapter
import es.fernandopal.aforoqr.data.model.AppUser
import es.fernandopal.aforoqr.data.model.Room
import es.fernandopal.aforoqr.data.model.Stats
import es.fernandopal.aforoqr.databinding.FragmentAdministrationBinding
import es.fernandopal.aforoqr.util.NetworkUtils
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.greenrobot.eventbus.EventBus
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdministrationFragment() : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentAdministrationBinding
    private val eventBus: EventBus = EventBus.getDefault()
    private val netUtils: NetworkUtils = NetworkUtils()
    private val auth = netUtils.getAuth()
    private val api = netUtils.getApi()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAdministrationBinding.inflate(inflater, container, false)

        updateStats()

        binding.btnCreateRoom.setOnClickListener {
            toggleView(binding.layoutCreateRoom)
        }

        binding.btnListRooms.setOnClickListener {
            toggleView(binding.layoutRecycler)
            binding.fragmentAdminActionsRecyclerTitle.text = getString(R.string.fragment_administration_actions_list_rooms_title)
            binding.fragmentAdminActionsRecyclerDescription.text = getString(R.string.fragment_administration_actions_list_rooms_description)
            getAllRooms()
        }

        binding.btnListUsers.setOnClickListener {
            toggleView(binding.layoutRecycler)
            binding.fragmentAdminActionsRecyclerTitle.text = getString(R.string.fragment_administration_actions_list_users_title)
            binding.fragmentAdminActionsRecyclerDescription.text = getString(R.string.fragment_administration_actions_list_users_description)
            getAllUsers()
        }

        binding.btnCreateRoomSave.setOnClickListener {
            createRoom()
        }

        return binding.root
    }

    private fun createRoom() {
        val name = binding.inputRoomName.text
        val code = binding.inputRoomCode.text
        val capacity = binding.inputRoomCapacity.text
        val country = binding.inputRoomAddressCountry.text
        val city = binding.inputRoomAddressCity.text
        val street = binding.inputRoomAddressStreet.text
        val buildingNumber = binding.inputRoomAddressStreetNumber.text

        if (
            name.isNullOrEmpty() ||
            code.isNullOrEmpty() ||
            capacity.isNullOrEmpty() ||
            country.isNullOrEmpty() ||
            city.isNullOrEmpty() ||
            street.isNullOrEmpty() ||
            buildingNumber.isNullOrEmpty()
        ) {
            Toast.makeText(context, "Debes rellenar todos los campos.", Toast.LENGTH_LONG).show()
            return
        }

        val addressJson = JSONObject()
            .put("buildingNumber", buildingNumber)
            .put("street", street)
            .put("country", country)
            .put("city", city)

        val roomJson = JSONObject()
            .put("name", name)
            .put("code", code)
            .put("capacity", capacity)
            .put("active", true)
            .put("address", addressJson)


        val requestBody = roomJson.toString().toRequestBody("application/json".toMediaTypeOrNull())
        Log.d("AdministrationFragment", "Request: $roomJson")

        netUtils.requestWithAuth { token, _ ->
            api.saveRoom(token, requestBody)!!.enqueue(object : Callback<Long?> {
                override fun onFailure(call: Call<Long?>, t: Throwable) {
                    Log.e("AdministrationFragment", t.stackTraceToString())
                }

                override fun onResponse(call: Call<Long?>, response: Response<Long?>) {
                    if (response.isSuccessful) {
                        val roomId = response.body()
                        Log.d("AdministrationFragment", "Created room: $roomId")

                        toggleView(binding.layoutRecycler)
                        binding.fragmentAdminActionsRecyclerTitle.text = getString(R.string.fragment_administration_actions_list_rooms_title)
                        binding.fragmentAdminActionsRecyclerDescription.text = getString(R.string.fragment_administration_actions_list_rooms_description)
                        getAllRooms()

                    } else if (response.code() == 500) {
                        if (response.message().contains("ConstraintViolationException")) {
                            Toast.makeText(context, "ERROR: Código de sala duplicado", Toast.LENGTH_LONG).show()
                        } else {
                            Toast.makeText(context, response.message(), Toast.LENGTH_LONG).show()
                        }
                    }
                }
            })
        }
    }

    private fun getAllRooms() {
        netUtils.requestWithAuth { token, _ ->
            api.getAllRooms(token)!!.enqueue(object : Callback<List<Room>> {
                override fun onFailure(call: Call<List<Room>>, t: Throwable) {
                    Log.e("AdministrationFragment", t.stackTraceToString())
                }

                override fun onResponse(call: Call<List<Room>>, response: Response<List<Room>>) {
                    if (response.isSuccessful) {
                        val allRooms = response.body() as List<Room>

                        binding.recyclerView.apply {
                            setHasFixedSize(true)
                            layoutManager = LinearLayoutManager(context)
                            adapter = RoomsAdapter(allRooms, showDeleteBtn = true, context)
                        }
                    }
                }
            })
        }
    }

    private fun updateStats() {
        netUtils.requestWithAuth { token, _ ->
            api.getStats(token)!!.enqueue(object : Callback<Stats?> {
                override fun onFailure(call: Call<Stats?>, t: Throwable) {
                    Log.e("AdministrationFragment", t.stackTraceToString())
                }

                override fun onResponse(call: Call<Stats?>, response: Response<Stats?>) {
                    if (response.isSuccessful) {
                        val stats = response.body()

                        if (stats != null) {
                            binding.reservationsCount.text = "Reservas realizadas: ${stats.totalReservations}"
                            binding.eventsCount.text = "Eventos: ${stats.totalEvents}"
                            binding.roomsCount.text = "Salas: ${stats.totalRooms}"
                            binding.usersCount.text = "Usuarios: ${stats.totalUsers}"
                        }
                    }
                }
            })
        }
    }

    private fun getAllUsers() {
        netUtils.requestWithAuth { token, _ ->

            api.getAllUsers(token)!!.enqueue(object : Callback<List<AppUser>> {
                override fun onFailure(call: Call<List<AppUser>>, t: Throwable) {
                    Log.e("AdministrationFragment", t.stackTraceToString())
                }

                override fun onResponse(
                    call: Call<List<AppUser>>,
                    response: Response<List<AppUser>>
                ) {
                    if (response.isSuccessful) {
                        val allUsers = response.body() as List<AppUser>

                        binding.recyclerView.apply {
                            setHasFixedSize(true)
                            layoutManager = LinearLayoutManager(context)
                            adapter = UsersAdapter(allUsers, showExtraIcon = true, context)
                        }
                    }
                }
            })
        }
    }

    private fun toggleView(setVisible: View) {
        setVisible.visibility = View.VISIBLE

        if (setVisible == binding.layoutCreateRoom) {
            binding.layoutRecycler.visibility = View.GONE
        }

        if (setVisible == binding.layoutRecycler) {
            binding.layoutCreateRoom.visibility = View.GONE
        }
    }
}