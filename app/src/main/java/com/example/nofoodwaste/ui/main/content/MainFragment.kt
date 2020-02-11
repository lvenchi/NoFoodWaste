package com.example.nofoodwaste.ui.main.content

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.example.nofoodwaste.R
import com.example.nofoodwaste.di.ComponentInjector
import com.example.nofoodwaste.viewmodels.MainViewModel
import kotlinx.android.synthetic.main.main_fragment.*
import javax.inject.Inject


class MainFragment : Fragment() {

    @Inject
    lateinit var viewModel: MainViewModel

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
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {

        super.onActivityCreated(savedInstanceState)

    }

}
