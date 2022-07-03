/**
 * Copyright (c) 2022 Fernando Palomo <mail@fernandopal.es>.
 * All rights reserved.
 */
package es.fernandopal.aforoqr

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanIntentResult
import com.journeyapps.barcodescanner.ScanOptions
import es.fernandopal.aforoqr.data.model.AppUser
import es.fernandopal.aforoqr.data.model.QRCode
import es.fernandopal.aforoqr.data.model.Role
import es.fernandopal.aforoqr.databinding.ActivityMainBinding
import es.fernandopal.aforoqr.ui.activity.LoginActivity
import es.fernandopal.aforoqr.ui.fragment.*
import es.fernandopal.aforoqr.util.Const
import es.fernandopal.aforoqr.util.FabState
import es.fernandopal.aforoqr.util.NetworkUtils
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception


class MainActivity : AppCompatActivity() {
    private val eventBus: EventBus = EventBus.getDefault()
    private val netUtils: NetworkUtils = NetworkUtils()
    private val auth = netUtils.getAuth()
    private val api = netUtils.getApi()
    private var currentFabAlignmentMode = BottomAppBar.FAB_ALIGNMENT_MODE_CENTER
    private var hasAdminPrivileges: Boolean = false
    private var canConnectWithApiServer: Boolean = true
    private lateinit var binding: ActivityMainBinding

    private var BottomAppBar.fabState: FabState
        get() = getTag(R.id.fab_state) as FabState
        set(value) = setTag(R.id.fab_state, value)


    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        eventBus.register(this)
    }

    override fun onStart() {
        super.onStart()

        // Comprobamos que tenemos conexión con el servidor de la API
        eventBus.postSticky(ApiHeartbeatEvent())
    }

    override fun onDestroy() {
        super.onDestroy()
        eventBus.unregister(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Evitamos que se cargue la activity si no tenemos conexión con el servidor de la API
        if (canConnectWithApiServer) {
            binding = ActivityMainBinding.inflate(layoutInflater)
            setContentView(binding.root)

            setCurrentFragment(HomeFragment(), FabState.QR_SCANNER)

            eventBus.postSticky(RequestInterfaceUpdateEvent())

            binding.bottomAppBar.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.bottom_nav_search -> {
                        setCurrentFragment(SearchFragment(), FabState.SEARCH_GO_BACK)
                        doFabTransition()
                        true
                    }
                    /**R.id.bottom_nav_settings -> {
                        setCurrentFragment(SettingsFragment(), FabState.SETTINGS_GO_BACK)
                        doFabTransition()
                        true
                    }**/
                    R.id.bottom_nav_sign_out -> {
                        signOut()
                        true
                    }
                    R.id.bottom_nav_administration -> {
                        if (!hasAdminPrivileges) {
                            false
                        } else {
                            setCurrentFragment(
                                AdministrationFragment(),
                                FabState.ADMINISTRATION_GO_BACK
                            )
                            doFabTransition()
                            true
                        }
                    }
                    else -> false
                }
            }

            /**
             * Zxing docs: https://github.com/journeyapps/zxing-android-embedded
             */
            binding.bottomNavFab.setOnClickListener {
                when (binding.bottomAppBar.fabState) {
                    FabState.QR_SCANNER -> {
                        val options = ScanOptions()
                        options.setDesiredBarcodeFormats(ScanOptions.QR_CODE)
                        options.setPrompt("Focus the QR Code")
                        options.setBarcodeImageEnabled(false)
                        options.setBeepEnabled(false)

                        qrCodeScanner.launch(options)
                    }
                    FabState.SEARCH_GO_BACK,
                    FabState.SETTINGS_GO_BACK,
                    FabState.NEW_RESERVATION_GO_BACK,
                    FabState.ADMINISTRATION_GO_BACK -> {
                        setCurrentFragment(HomeFragment(), FabState.QR_SCANNER)
                        doFabTransition()
                    }
                    FabState.BLOCKED -> { /* Do nothing */ }
                }
            }
        }
    }

    private val qrCodeScanner = registerForActivityResult(ScanContract()) { result: ScanIntentResult ->
        Log.d("MainActivity", "QR contents: ${result.contents}")
        if (result.contents != null) {
            netUtils.requestWithAuth { token, _ ->
                api.getQRCode(token, result.contents.toLong())?.enqueue(object : Callback<QRCode?> {
                    override fun onFailure(call: Call<QRCode?>, t: Throwable) {
                        Log.e("MainActivity", t.stackTraceToString())
                    }

                    override fun onResponse(call: Call<QRCode?>, res: Response<QRCode?>) {
                        if (res.isSuccessful) {
                            val qrCode = res.body()
                            eventBus.postSticky(RequestFragmentChangeEvent(NewReservationFragment(qrCode?.room, qrCode?.event)))
                        }
                    }
                })
            }
        }
    }

    private fun setCurrentFragment(fragment: Fragment, fabState: FabState) {
        supportFragmentManager.beginTransaction().replace(R.id.main_fragment_container, fragment).commit()
        binding.bottomAppBar.fabState = fabState
    }

    private fun doFabTransition() {
        val addVisibilityChanged: FloatingActionButton.OnVisibilityChangedListener = object : FloatingActionButton.OnVisibilityChangedListener() {
            override fun onShown(fab: FloatingActionButton?) {
                super.onShown(fab)
            }
            override fun onHidden(fab: FloatingActionButton?) {
                super.onHidden(fab)
                binding.bottomAppBar.toggleFabAlignment()
                binding.bottomAppBar.replaceMenu(
                    if(currentFabAlignmentMode == BottomAppBar.FAB_ALIGNMENT_MODE_CENTER) {
                        R.menu.bottom_app_bar_secondary
                    } else {
                        R.menu.bottom_app_bar
                    }
                )
                fab?.setImageDrawable(
                    if(currentFabAlignmentMode == BottomAppBar.FAB_ALIGNMENT_MODE_CENTER) {
                        AppCompatResources.getDrawable(binding.bottomNavFab.context, R.drawable.ic_arrow_small_left)
                    } else {
                        AppCompatResources.getDrawable(binding.bottomNavFab.context, R.drawable.ic_scan)
                    }
                )
                fab?.show()
            }
        }

        binding.bottomNavFab.hide(addVisibilityChanged)
        invalidateOptionsMenu()
    }

    private fun BottomAppBar.toggleFabAlignment() {
        currentFabAlignmentMode = fabAlignmentMode
        fabAlignmentMode = currentFabAlignmentMode.xor(1)
    }

    private fun signOut() {
        auth.signOut()

        val myIntent = Intent(this@MainActivity, LoginActivity::class.java)
        this@MainActivity.startActivity(myIntent)
        finish()
    }

    // Eventos accesibles desde el EventBus
    @Subscribe(sticky = true, threadMode = ThreadMode.BACKGROUND)
    fun onFragmentChangeDataReceived(event: RequestFragmentChangeEvent) {
        setCurrentFragment(event.fragment, FabState.NEW_RESERVATION_GO_BACK)
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.BACKGROUND)
    fun checkAdminPrivileges(event: RefreshDataEvent?) {
        netUtils.requestWithAuth(
            onError = {
                eventBus.postSticky(SendErrorToBusEvent(Const.GS_NOT_ALIVE_ERROR))
            },
            onSuccess = { token, _ ->
                api.getUser(token)?.enqueue(object : Callback<AppUser?> {
                    override fun onFailure(call: Call<AppUser?>, t: Throwable) {
                        eventBus.postSticky(SendErrorToBusEvent(Const.API_NOT_ALIVE_ERROR))
                    }

                    override fun onResponse(call: Call<AppUser?>, response: Response<AppUser?>) {
                        if (!response.isSuccessful) {
                            eventBus.postSticky(ApiHeartbeatEvent())
                        }

                        if (response.isSuccessful) {
                            hasAdminPrivileges = response.body()?.admin as Boolean
                            eventBus.postSticky(RequestInterfaceUpdateEvent())
                        }
                    }
                })
            }
        )
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.BACKGROUND)
    fun onServerConnectionError(event: SendErrorToBusEvent) {
        setCurrentFragment(ConnectionErrorFragment(event.message), FabState.BLOCKED)
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.BACKGROUND)
    fun checkApiConnection(event: ApiHeartbeatEvent) {
        val alive = netUtils.ping(Const.API_BASE_URL, Const.API_HTTP_PORT, 1500)

        canConnectWithApiServer = alive

        if (!alive) eventBus.postSticky(SendErrorToBusEvent(Const.API_NOT_ALIVE_ERROR))
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    fun updateUI(event: RequestInterfaceUpdateEvent) {
        try {
            if (hasAdminPrivileges) {
                binding.bottomAppBar.menu.findItem(R.id.bottom_nav_administration).isVisible = true
            }
        } catch (e: Exception) { }
    }

    // Eventos registrados por MainActivity
    class RefreshDataEvent
    class ApiHeartbeatEvent
    class SendErrorToBusEvent(var message: String)
    class RequestFragmentChangeEvent(var fragment: Fragment)
    class RequestInterfaceUpdateEvent
}