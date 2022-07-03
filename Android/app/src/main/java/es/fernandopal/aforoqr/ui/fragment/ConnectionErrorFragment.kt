package es.fernandopal.aforoqr.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import es.fernandopal.aforoqr.databinding.FragmentConnectionErrorBinding
import es.fernandopal.aforoqr.util.NetworkUtils
import org.greenrobot.eventbus.EventBus

class ConnectionErrorFragment(var message: String?) : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentConnectionErrorBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentConnectionErrorBinding.inflate(inflater, container, false)

        if (message != null) binding.errorMessage.text = message

        return binding.root
    }
}