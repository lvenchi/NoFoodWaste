package com.example.nofoodwaste.ui.main.login


import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.BindingAdapter
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.nofoodwaste.NoFoodWasteApplication
import com.example.nofoodwaste.R
import com.example.nofoodwaste.databinding.FragmentLoginPageBinding
import com.example.nofoodwaste.di.ComponentInjector
import com.example.nofoodwaste.utils.Utils
import com.example.nofoodwaste.viewmodels.LoginViewModel
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.FacebookSdk
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.OAuthProvider
import kotlinx.android.synthetic.main.fragment_login_page.*
import java.util.*
import javax.inject.Inject

private const val GOOGLE_SIGN_IN = 234

@BindingAdapter("app:errorText")
fun setErrorMessage(view: TextInputLayout, errorMessage: String?) {
    view.error = errorMessage
}

class LoginPage : Fragment() {

    @Inject
    lateinit var loginViewModel: LoginViewModel

    private lateinit var callbackManager: CallbackManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ComponentInjector.component.inject(this)
        callbackManager = CallbackManager.Factory.create()
        loginViewModel.firebaseUser.observe(this, Observer {
            if(it != null){
                loginViewModel.addFirebaseUserListener(it.uid)
                findNavController().navigate(LoginPageDirections.actionLoginPageToMainFragment())
            } else {
                Snackbar.make(btn_login, "Failed to login", Snackbar.LENGTH_SHORT)
            }
        })

        loginViewModel.socialErrors.value = null

        loginViewModel.socialErrors.observe( this, Observer {
            if(!it.isNullOrEmpty())Snackbar.make(btn_login, it, Snackbar.LENGTH_SHORT).show()
        })

        loginViewModel.getCurrentFirebaseUser()

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding : FragmentLoginPageBinding = DataBindingUtil.inflate(inflater,
            R.layout.fragment_login_page, container, false)
        binding.loginViewModel = loginViewModel
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()
        initButtons()
        super.onViewCreated(view, savedInstanceState)
    }


    private fun initButtons(){
        btn_login.setOnClickListener {
            var isWellFormed = true

            if(!Utils.isEmail(loginViewModel.email.value)) {
                input_layout_email.error = "Not a valid Email"
                isWellFormed = false
            } else input_layout_password.error = null
            if(loginViewModel.password.value.isNullOrEmpty()) {
                input_layout_password.error = "Invalid Password"
                isWellFormed = false
            } else input_layout_password.error = null
            if(isWellFormed) loginViewModel.loginWithEmailAndPassword()
        }

        btn_facebook.setOnClickListener {
            loginViewModel.isLoading.postValue(true)
            loginViewModel.loginManager.registerCallback(callbackManager, object: FacebookCallback<LoginResult>{
                override fun onSuccess(result: LoginResult?) {
                    loginViewModel.loginWithFacebook( result?.accessToken )
                }

                override fun onCancel() {
                    loginViewModel.isLoading.postValue(false)
                }

                override fun onError(error: FacebookException?) {
                    loginViewModel.isLoading.postValue(false)
                }

            })
            loginViewModel.loginManager.logIn(this, arrayListOf("email", "public_profile"))
        }

        btn_google.setOnClickListener {
            loginViewModel.isLoading.postValue(true)
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestProfile()
                .requestIdToken("171347500769-n1dknadvkv3o7fpobv6sl2707sineb0q.apps.googleusercontent.com")
                .requestEmail()
                .build()
            loginViewModel.googleSignInClient = GoogleSignIn.getClient(activity!!, gso)
            startActivityForResult(
                loginViewModel.googleSignInClient!!.signInIntent,
                GOOGLE_SIGN_IN
            )
        }

        btn_signup.setOnClickListener {
            findNavController().navigate(LoginPageDirections.actionFragmentLoginPageToRegisterPage())
        }

        forgot_password_text_view.setOnClickListener {
            loginViewModel.requestLostPassword()
            loginViewModel.socialErrors.postValue("Password reset sent")
        }

        btn_twitter.setOnClickListener {
            val provider = OAuthProvider.newBuilder("twitter.com")
            val pendingResultTask: Task<AuthResult>? = loginViewModel.getPendingFirebaseResult()
            if (pendingResultTask != null) {
                loginViewModel.listenForAuthResult(pendingResultTask)
            } else {
                loginViewModel.listenForAuthResult(
                loginViewModel.getFirebaseAuth()
                    .startActivityForSignInWithProvider( activity!!, provider.build()))
            }
        }

        flag_italy.setOnClickListener {
            Locale.setDefault(Locale.ITALIAN)
            activity?.getSharedPreferences("my_preferences", Context.MODE_PRIVATE)?.edit()?.putString("locale", "it")?.commit()
            activity?.recreate()
        }

        flag_uk.setOnClickListener {
            Locale.setDefault(Locale.ENGLISH)
            activity?.getSharedPreferences("my_preferences", Context.MODE_PRIVATE)?.edit()?.putString("locale", "en")?.commit()
            activity?.recreate()
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if( FacebookSdk.isFacebookRequestCode(requestCode)) callbackManager.onActivityResult(requestCode, resultCode, data)
        else if(requestCode == GOOGLE_SIGN_IN){
            GoogleSignIn.getSignedInAccountFromIntent(data).addOnCompleteListener {
                if(it.isSuccessful){
                    loginViewModel.loginWithGoogle(it.result)
                } else {
                    loginViewModel.isLoading.postValue(false)
                    loginViewModel.socialErrors.postValue(it.exception?.message)
                }

            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

}
