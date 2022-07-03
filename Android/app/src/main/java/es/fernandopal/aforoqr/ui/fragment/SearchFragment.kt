package es.fernandopal.aforoqr.ui.fragment
/**
 * Copyright (c) 2022 Fernando Palomo <mail@fernandopal.es>.
 * All rights reserved.
 */

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import es.fernandopal.aforoqr.data.EventsAdapter
import es.fernandopal.aforoqr.data.RoomsAdapter
import es.fernandopal.aforoqr.data.model.Event
import es.fernandopal.aforoqr.data.model.Room
import es.fernandopal.aforoqr.data.model.SearchType
import es.fernandopal.aforoqr.data.model.Tuple
import es.fernandopal.aforoqr.databinding.FragmentSearchBinding
import es.fernandopal.aforoqr.util.NetworkUtils
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class SearchFragment() : BottomSheetDialogFragment() {
    private val netUtils: NetworkUtils = NetworkUtils()
    private val auth = netUtils.getAuth()
    private val api = netUtils.getApi()
    private lateinit var binding: FragmentSearchBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentSearchBinding.inflate(inflater, container, false)

        binding.inputSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) { }
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) { }
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val searchTerm = binding.inputSearch.text.toString()
                search(searchTerm)
            }
        })

        /**binding.searchBarBtnSearch.setOnClickListener {
            val searchTerm = binding.inputSearch.text.toString()
            search(searchTerm)
        }*/

        return binding.root
    }

    private fun search(searchTerm: String) {
        netUtils.requestWithAuth { token, _ ->
            val jsonObject = JSONObject()
                .put("search", searchTerm)
                .put("type", SearchType.UNKNOWN)

            val requestBody = jsonObject.toString().toRequestBody("application/json".toMediaTypeOrNull())
            Log.d("SearchFragment", "Request: $jsonObject")

            api.searchEventsAndRooms(token, requestBody)!!.enqueue(object: Callback<Tuple<List<Room>, List<Event>>?> {
                override fun onFailure(call: Call<Tuple<List<Room>, List<Event>>?>, t: Throwable) {
                    Log.e("SearchFragment", t.stackTraceToString())
                }

                override fun onResponse(call: Call<Tuple<List<Room>, List<Event>>?>, response: Response<Tuple<List<Room>, List<Event>>?>) {
                    if (response.isSuccessful) {
                        val searchResults = response.body() as Tuple<List<Room>, List<Event>>

                        val eventsVisibility = if (searchResults.events.isEmpty()) View.GONE else View.VISIBLE
                        val roomsVisibility = if (searchResults.rooms.isEmpty()) View.GONE else View.VISIBLE

                        val eventsListView = binding.searchResultEventsList
                        val roomsListView = binding.searchResultRoomsList

                        eventsListView.visibility = eventsVisibility
                        roomsListView.visibility = roomsVisibility

                        eventsListView.apply {
                            setHasFixedSize(true)
                            layoutManager = LinearLayoutManager(context)
                            adapter = EventsAdapter(searchResults.events, context)
                        }

                        roomsListView.apply {
                            setHasFixedSize(true)
                            layoutManager = LinearLayoutManager(context)
                            adapter = RoomsAdapter(searchResults.rooms, false, context)
                        }
                    }
                }
            })
        }
    }
}