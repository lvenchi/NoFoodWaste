package com.example.nofoodwaste


import android.content.Intent
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
import androidx.navigation.ui.NavigationUI
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
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.OAuthProvider
import kotlinx.android.synthetic.main.fragment_login_page.*
import kotlinx.android.synthetic.main.fragment_register_page.*
import javax.inject.Inject

private const val GOOGLE_SIGN_IN = 234

@BindingAdapter("app:errorText")
fun setErrorMessage(view: TextInputLayout, errorMessage: String?) {
    view.error = errorMessage
}

class LoginPage : Fragment() {

    @Inject
    lateinit var loginViewModel: LoginViewModel

    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var callbackManager: CallbackManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ComponentInjector.component.inject(this)
        callbackManager = CallbackManager.Factory.create()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding : FragmentLoginPageBinding = DataBindingUtil.inflate(inflater,  R.layout.fragment_login_page, container, false)
        binding.loginViewModel = loginViewModel
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()
        initButtons()
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onStart() {
        loginViewModel.firebaseUser.observe(this, Observer {
            if(it != null){
                //findNavController().navigate(LoginPageDirections.actionFragmentSignupToMainFragment())
            } else {
                Snackbar.make(btn_login, "Failed to login", Snackbar.LENGTH_SHORT)
            }
        })

        loginViewModel.socialErrors.observe( this, Observer {
            Snackbar.make(btn_login, it, Snackbar.LENGTH_SHORT).show()
        })
        loginViewModel.getCurrentFirebaseUser()
        super.onStart()
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
            googleSignInClient = GoogleSignIn.getClient(activity!!, gso)
            startActivityForResult(googleSignInClient.signInIntent, GOOGLE_SIGN_IN)
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
            loginViewModel.loginWithTwitter()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if( FacebookSdk.isFacebookRequestCode(requestCode)) callbackManager.onActivityResult(requestCode, resultCode, data)
        else if(requestCode == GOOGLE_SIGN_IN ){
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
