package com.example.nofoodwaste.viewmodels

import android.graphics.drawable.Drawable
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.nofoodwaste.models.User
import com.example.nofoodwaste.repositories.FirebaseRepositoryImpl
import com.facebook.AccessToken
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.*
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class LoginViewModel @Inject constructor(): ViewModel() {

    @Inject
    lateinit var firebaseRepository: FirebaseRepositoryImpl

    val cachedImage = object {
        var path : String? = null
        var image : Drawable? = null
    }

    val loginManager: LoginManager by lazy {
        LoginManager.getInstance()
    }

    val Manager: LoginManager by lazy {
        LoginManager.getInstance()
    }

    var isLoading: MutableLiveData<Boolean> = MutableLiveData(false)

    var firebaseUser: MutableLiveData<FirebaseUser?> = MutableLiveData()

    var email: MutableLiveData<String> = MutableLiveData()
    var password: MutableLiveData<String> = MutableLiveData()
    val name: MutableLiveData<String> = MutableLiveData()
    val lastName: MutableLiveData<String> = MutableLiveData()
    var profileImagePath: MutableLiveData<String> = MutableLiveData()

    var errors: MutableLiveData<String> = MutableLiveData()
    var socialErrors: MutableLiveData<String> = MutableLiveData()

    fun loginWithEmailAndPassword() {
        isLoading.postValue(true)
        firebaseRepository.loginWithEmailAndPassword(
            email.value!!,
            password.value!!,
            OnCompleteListener { p0 ->
                if(p0.isSuccessful) firebaseUser.postValue(p0.result?.user)
                else errors.postValue(p0.exception?.message)

                isLoading.postValue(false) }
        )
    }

    fun getPendingFirebaseResult(): Task<AuthResult>?{
        return firebaseRepository.getPendingFirebaseResult()
    }

    fun signUpWithEmailAndPassword() {
        isLoading.postValue(true)
        firebaseRepository.registerWithEmailAndPassword(
            email.value!!,
            password.value!!,
            OnCompleteListener { res ->
                if(res.isSuccessful){
                firebaseUser.postValue(res.result?.user)
                    firebaseRepository.addUserToFirebaseDatabase(
                        User().also {
                            it.name = name.value
                            it.surname = lastName.value
                            it.firebaseid = res.result?.user?.uid
                            it.email = email.value!!
                            it.photoLocation = profileImagePath.value
                    })
                }

                isLoading.postValue(false) }
        )
    }

    fun loginWithFacebook( accessToken: AccessToken?) {
        if(accessToken != null)
            firebaseRepository.loginWithFacebook(
                accessToken,
                OnCompleteListener { p0 ->
                    if(p0.isSuccessful) {
                        firebaseUser.postValue(p0.result?.user)
                        firebaseRepository.addUserToFirebaseDatabase(
                            User().also {
                                it.name = p0.result?.user?.displayName
                                it.firebaseid = p0.result?.user?.uid
                                it.email = p0.result?.user?.email!!
                                it.photoLocation = p0.result?.user?.photoUrl.toString()
                            })
                    }
                    else socialErrors.postValue(p0.exception?.message)
                    isLoading.postValue(false)
                }
            )
        else {
            firebaseUser.postValue(null)
            isLoading.postValue(false)
        }
    }

    fun loginWithGoogle( account: GoogleSignInAccount?) {
        val credential = GoogleAuthProvider.getCredential(account!!.idToken, null)
        firebaseRepository.loginWithGoogle(
            credential,
            OnCompleteListener { res ->
                if(res.isSuccessful){
                    firebaseUser.postValue(res.result?.user)
                    firebaseRepository.addUserToFirebaseDatabase(
                        User().also {
                            it.name = res.result?.user?.displayName
                            it.firebaseid = res.result?.user?.uid
                            it.email = res.result?.user?.email!!
                            it.photoLocation = res.result?.user?.photoUrl.toString()
                        })
                } else {
                    socialErrors.postValue(res.exception?.message)
                    firebaseUser.postValue(null)

                }
                isLoading.postValue(false)
            })
    }


    fun requestLostPassword(){
        if(!email.value.isNullOrEmpty()) firebaseRepository.recoverLostPassword(email.value!!)
    }

    fun getFirebaseAuth() : FirebaseAuth {
        return firebaseRepository.firebaseAuth
    }

    fun listenForAuthResult(authResultTask: Task<AuthResult>){
        authResultTask.addOnCompleteListener {
            if(it.isSuccessful) {
                firebaseUser.postValue(it.result?.user)
                firebaseRepository.addUserToFirebaseDatabase(
                    User().also { user ->
                        user.photoLocation = it.result?.user?.photoUrl.toString()
                        user.name = it.result?.user?.displayName
                        user.firebaseid = it.result?.user?.uid
                        user.email = it.result?.user?.email!!
                })
            }
            else socialErrors.postValue(it.exception?.message)
        }
    }

    fun logOut(){
        firebaseRepository.signOut()
    }

    fun getCurrentFirebaseUser() {
        firebaseUser.postValue(firebaseRepository.getCurrentUser())
    }

}