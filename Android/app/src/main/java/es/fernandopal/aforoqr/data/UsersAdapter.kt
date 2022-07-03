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
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import es.fernandopal.aforoqr.MainActivity
import es.fernandopal.aforoqr.R
import es.fernandopal.aforoqr.data.model.AppUser
import es.fernandopal.aforoqr.data.model.Role
import es.fernandopal.aforoqr.data.model.SearchType
import es.fernandopal.aforoqr.ui.fragment.NewReservationFragment
import es.fernandopal.aforoqr.util.Const
import es.fernandopal.aforoqr.util.NetworkUtils
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.greenrobot.eventbus.EventBus
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class UsersAdapter(private val users: List<AppUser>, val showExtraIcon: Boolean, val context: Context) : RecyclerView.Adapter<UsersAdapter.ViewHolder>() {
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
        return users.size
    }

    // Bind list items to a new view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = users[position]
        val (root, title, description, btnDelete) = holder

        if (showExtraIcon) {
            btnDelete.visibility = View.VISIBLE

            if (user.admin) {
                btnDelete.setOnClickListener {
                    setAdmin(false, user, position)
                    Toast.makeText(context, "Este usuario ya no es un administrador", Toast.LENGTH_SHORT).show()
                }
            } else {
                btnDelete.setOnClickListener {
                    setAdmin(true, user, position)
                    Toast.makeText(context, "Este usuario ahora es un administrador", Toast.LENGTH_SHORT).show()
                }
            }

            if(user.admin) {
                btnDelete.setImageResource(R.drawable.ic_baseline_toggle_on)
            } else {
                btnDelete.setImageResource(R.drawable.ic_baseline_toggle_off)
            }
        }

        title.text = user.name

        description.text = user.email

    }

    private fun setAdmin(admin: Boolean, appUser: AppUser, position: Int): Boolean {
        var success = false
        netUtils.requestWithAuth { token, _ ->

            appUser.admin = admin

            val jsonObject = JSONObject()
                .put("uid", appUser.uid)
                .put("name", appUser.name)
                .put("email", appUser.email)
                .put("issuer", appUser.issuer)
                .put("picture", appUser.picture)
                .put("reputation", appUser.reputation)
                .put("active", appUser.active)
                .put("admin", appUser.admin)

            val requestBody = jsonObject.toString().toRequestBody("application/json".toMediaTypeOrNull())
            Log.d("UsersAdapter", "Request: $jsonObject")

            api.saveUser(token, requestBody)?.enqueue(object : Callback<String> {
                override fun onFailure(call: Call<String>, t: Throwable) {
                    Log.e("UsersAdapter", t.stackTraceToString())
                }
                override fun onResponse(call: Call<String>, response: Response<String>) {

                    if (!response.isSuccessful) {
                        Log.e("UsersAdapter", "Error giving admin role: $response")
                    } else {
                        success = true
                        notifyItemChanged(position)
                    }
                }
            })
        }

        return success
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