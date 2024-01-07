package org.secu3.android.ui.splash

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.secu3.android.BuildConfig
import org.secu3.android.R
import org.secu3.android.databinding.SplashScreenBinding

class SplashFragment: Fragment() {

    private lateinit var mBinding: SplashScreenBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = SplashScreenBinding.inflate(inflater)
        return mBinding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val version = "Версия приложения: " + BuildConfig.VERSION_NAME + "Beta"
        mBinding.txtSplashVersionApp.text = version
        CoroutineScope(Dispatchers.Main).launch {
            delay(2000)
            findNavController().navigate(R.id.action_splashFragment_to_btStatusFragment)
            //показываем лого, через 2 сек переходим на экран BT-status
        }
    }
}