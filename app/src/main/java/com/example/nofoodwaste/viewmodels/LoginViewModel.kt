package com.example.nofoodwaste.viewmodels

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.nofoodwaste.models.User
import com.example.nofoodwaste.repositories.FirebaseRepositoryImpl
import com.facebook.AccessToken
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.*
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class LoginViewModel @Inject constructor() : ViewModel() {

    @Inject
    lateinit var firebaseRepository: FirebaseRepositoryImpl

    private object CachedImage {
        var path: String? = null
        var image: Bitmap? = null
    }

    val loginManager: LoginManager by lazy {
        LoginManager.getInstance()
    }

    var googleSignInClient: GoogleSignInClient? = null

    var currentUser = MutableLiveData<User>()

    var isLoading: MutableLiveData<Boolean> = MutableLiveData(false)

    var firebaseUser: MutableLiveData<FirebaseUser?> = MutableLiveData()

    var email: MutableLiveData<String> = MutableLiveData()
    var password: MutableLiveData<String> = MutableLiveData()
    val name: MutableLiveData<String> = MutableLiveData()
    val lastName: MutableLiveData<String> = MutableLiveData()
    var profileImagePath: MutableLiveData<String> = MutableLiveData()

    var errors: MutableLiveData<String> = MutableLiveData()
    var socialErrors: MutableLiveData<String> = MutableLiveData()

    fun getCachedImage( path: String ): Bitmap?{
        return if(path == CachedImage.path ) CachedImage.image
        else null
    }

    fun updateCache(drawable: Bitmap, path: String ){
        CachedImage.image = drawable
        CachedImage.path = path
    }

    var firebaseUserListener = object: ValueEventListener {
        override fun onCancelled(p0: DatabaseError) {
            currentUser.postValue(null)
        }

        override fun onDataChange(p0: DataSnapshot) {
            currentUser.postValue(p0.getValue(User::class.java))
        }

    }

    fun loginWithEmailAndPassword() {
        isLoading.postValue(true)
        firebaseRepository.loginWithEmailAndPassword(
            email.value!!,
            password.value!!,
            OnCompleteListener { p0 ->
                if (p0.isSuccessful) {
                    firebaseUser.postValue(p0.result?.user)
                    listenForUserUpdates(p0.result)
                } else errors.postValue(p0.exception?.message)

                isLoading.postValue(false)
            }
        )
    }

    fun getPendingFirebaseResult(): Task<AuthResult>? {
        return firebaseRepository.getPendingFirebaseResult()
    }

    fun signUpWithEmailAndPassword() {
        isLoading.postValue(true)
        firebaseRepository.registerWithEmailAndPassword(
            email.value!!,
            password.value!!,
            OnCompleteListener { res ->
                if (res.isSuccessful) {
                    firebaseUser.postValue(res.result?.user)
                    addToFirebaseAndListen(res.result)
                } else socialErrors.postValue(res.exception?.message)

                isLoading.postValue(false)
            }
        )
    }

    fun loginWithFacebook(accessToken: AccessToken?) {
        if (accessToken != null)
            firebaseRepository.loginWithFacebook(
                accessToken,
                OnCompleteListener { p0 ->
                    if (p0.isSuccessful) {
                        firebaseUser.postValue(p0.result?.user)
                        if(p0.result?.additionalUserInfo?.isNewUser!!)
                            addToFirebaseAndListen(p0.result)
                    } else socialErrors.postValue(p0.exception?.message)
                    isLoading.postValue(false)
                }
            )
        else {
            firebaseUser.postValue(null)
            isLoading.postValue(false)
        }
    }

    fun loginWithGoogle(account: GoogleSignInAccount?) {
        val credential = GoogleAuthProvider.getCredential(account!!.idToken, null)
        firebaseRepository.loginWithGoogle(
            credential,
            OnCompleteListener { res ->
                if (res.isSuccessful) {
                    firebaseUser.postValue(res.result?.user)
                    if(res.result?.additionalUserInfo?.isNewUser!!)
                        addToFirebaseAndListen(res.result)
                } else {
                    socialErrors.postValue(res.exception?.message)
                    firebaseUser.postValue(null)
                }
                isLoading.postValue(false)
            })
    }


    fun requestLostPassword() {
        if (!email.value.isNullOrEmpty()) firebaseRepository.recoverLostPassword(email.value!!)
    }

    fun getFirebaseAuth(): FirebaseAuth {
        return firebaseRepository.firebaseAuth
    }

    fun listenForAuthResult(authResultTask: Task<AuthResult>) {
        authResultTask.addOnCompleteListener {
            if (it.isSuccessful) {
                firebaseUser.postValue(it.result?.user)
                if(it.result?.additionalUserInfo?.isNewUser!!) addToFirebaseAndListen(it.result)
            } else socialErrors.postValue(it.exception?.message)
        }
    }

    fun logOut() {
        if(!currentUser.value?.firebaseid.isNullOrEmpty()) firebaseRepository.removeFirebaseUserListener(currentUser.value?.firebaseid!!, firebaseUserListener)
        firebaseRepository.signOut()
        loginManager.logOut()
        googleSignInClient?.signOut()
        firebaseUser.postValue(null)
    }

    fun getCurrentFirebaseUser() {
        firebaseUser.postValue(firebaseRepository.getCurrentUser())
    }

    private fun addToFirebaseAndListen( authResult: AuthResult?){
        firebaseRepository.addUserToFirebaseDatabase(
            User().also { user ->
                user.photoLocation = authResult?.user?.photoUrl.toString()
                user.name = authResult?.user?.displayName
                user.firebaseid = authResult?.user?.uid
                user.email = authResult?.user?.email!!
            })
        //addFirebaseUserListener(authResult?.user?.uid!!)
    }

    fun addFirebaseUserListener( uid: String){
        firebaseRepository.addFirebaseUserListener(
            uid,
            firebaseUserListener
        )
    }

    fun updateFirebaseUser( ){
        firebaseRepository.updateFirebaseUser(
            User().also {
                it.photoLocation = profileImagePath.value
                it.email = currentUser.value!!.email
                it.firebaseid = currentUser.value!!.firebaseid
                it.surname = lastName.value
                it.name = name.value
            }
        )
    }

    private fun listenForUserUpdates(authResult: AuthResult?){
        firebaseRepository.addFirebaseUserListener(
            authResult?.user?.uid!!,
            firebaseUserListener
        )
    }

}