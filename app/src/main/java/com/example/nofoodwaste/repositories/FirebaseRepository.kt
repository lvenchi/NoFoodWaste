package com.example.nofoodwaste.repositories

import com.example.nofoodwaste.models.User
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.ValueEventListener

interface FirebaseRepository {

    fun registerWithEmailAndPassword( email: String, password: String, onCompleteListener: OnCompleteListener<AuthResult>)

    fun loginWithEmailAndPassword( email: String, password: String, onCompleteListener: OnCompleteListener<AuthResult>)

    fun loginWithFacebook(accessToken: AccessToken, onCompleteListener: OnCompleteListener<AuthResult>)

    fun loginWithGoogle(credential: AuthCredential, onCompleteListener: OnCompleteListener<AuthResult>)

    fun addUserToFirebaseDatabase( user: User)

    fun getPendingFirebaseResult(): Task<AuthResult>?

    fun getCurrentUser(): FirebaseUser?

    fun signOut()

    fun recoverLostPassword(email: String)

    fun addFirebaseUserListener( userId: String, valueEventListener: ValueEventListener)

    fun removeFirebaseUserListener( userId: String, valueEventListener: ValueEventListener)

    fun updateFirebaseUser(updatedUser: User)

}