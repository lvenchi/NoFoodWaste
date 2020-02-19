package com.example.nofoodwaste.ui.main.content

import android.animation.ObjectAnimator
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.animation.doOnStart
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.example.nofoodwaste.R
import com.example.nofoodwaste.di.ComponentInjector
import com.example.nofoodwaste.viewmodels.MainViewModel
import kotlinx.android.synthetic.main.main_fragment.*
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent
import net.yslibrary.android.keyboardvisibilityevent.Unregistrar
import javax.inject.Inject


class MainFragment : Fragment() {

    @Inject
    lateinit var viewModel: MainViewModel

    lateinit var listener: Unregistrar

    override fun onCreate(savedInstanceState: Bundle?) {
        ComponentInjector.component.inject(this)

        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.main_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val navHostFragment = childFragmentManager.findFragmentById(R.id.nav_host_fragment_2) as NavHostFragment
        NavigationUI.setupWithNavController(
            bottom_navigation,
            navHostFragment.navController
        )

        listener = KeyboardVisibilityEvent.registerEventListener(activity) { isOpen ->
            if(isOpen) bottom_navigation.visibility = View.GONE else Handler().postDelayed(  { bottom_navigation.visibility = View.VISIBLE }, 200  )
        }

        super.onViewCreated(view, savedInstanceState)
    }

    override fun onDestroyView() {
        listener.unregister()
        super.onDestroyView()
    }

}
