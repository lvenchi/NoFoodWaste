package com.example.nofoodwaste.ui.main.content


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.example.nofoodwaste.R
import com.example.nofoodwaste.di.ComponentInjector
import com.example.nofoodwaste.ui.main.MainFragmentDirections
import com.example.nofoodwaste.viewmodels.LoginViewModel
import kotlinx.android.synthetic.main.fragment_profile.*
import javax.inject.Inject

class ProfilePage : Fragment() {

    @Inject
    lateinit var loginViewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        ComponentInjector.component.inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        logout_button.setOnClickListener {
            loginViewModel.logOut()
            activity?.findNavController(R.id.nav_host_fragment)?.navigate(MainFragmentDirections.actionMainFragmentToLoginPage())
        }
        super.onViewCreated(view, savedInstanceState)
    }


}
