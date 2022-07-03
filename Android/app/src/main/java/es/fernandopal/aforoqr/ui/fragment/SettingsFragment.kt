package es.fernandopal.aforoqr.ui.fragment
/**
 * Copyright (c) 2022 Fernando Palomo <mail@fernandopal.es>.
 * All rights reserved.
 */

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import es.fernandopal.aforoqr.R
import es.fernandopal.aforoqr.util.NetworkUtils
import org.greenrobot.eventbus.EventBus

class SettingsFragment : PreferenceFragmentCompat() {
    private val eventBus: EventBus = EventBus.getDefault()
    private val netUtils: NetworkUtils = NetworkUtils()
    private val auth = netUtils.getAuth()
    private val api = netUtils.getApi()

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
    }
}