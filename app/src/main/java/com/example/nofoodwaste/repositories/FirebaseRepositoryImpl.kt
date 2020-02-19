package com.example.nofoodwaste.repositories

import com.example.nofoodwaste.models.User
import com.facebook.AccessToken
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.*
import com.google.firebase.database.*

class FirebaseRepositoryImpl(val firebaseAuth: FirebaseAuth, val firebaseDatabase: DatabaseReference): FirebaseRepository {

    override fun registerWithEmailAndPassword(
        email: String,
        password: String,
        onCompleteListener: OnCompleteListener<AuthResult>
    ) {
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(onCompleteListener)
    }

    override fun loginWithEmailAndPassword(
        email: String,
        password: String,
        onCompleteListener: OnCompleteListener<AuthResult>
    ) {
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(onCompleteListener)
    }

    override fun loginWithFacebook(
        accessToken: AccessToken,
        onCompleteListener: OnCompleteListener<AuthResult>
    ) {
        val credential = FacebookAuthProvider.getCredential(accessToken.token)
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(onCompleteListener)
    }

    override fun loginWithGoogle(
        credential: AuthCredential,
        onCompleteListener: OnCompleteListener<AuthResult>
    ) {
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(onCompleteListener)
    }

    override fun addUserToFirebaseDatabase( user: User ){
        firebaseDatabase.child("users").child("${user.firebaseid}").setValue(user)
    }

    override fun getPendingFirebaseResult(): Task<AuthResult>?{
        return firebaseAuth.pendingAuthResult
    }

    override fun getCurrentUser(): FirebaseUser? {
        return firebaseAuth.currentUser
    }

    override fun signOut() {
       firebaseAuth.signOut()
    }

    override fun recoverLostPassword(email: String){
        firebaseAuth.sendPasswordResetEmail(email)
    }

    override fun addFirebaseUserListener( userId: String, valueEventListener: ValueEventListener ) {
        firebaseDatabase.child("users")
            .child(userId)
            .addValueEventListener(valueEventListener)
    }

    override fun removeFirebaseUserListener(userId: String, valueEventListener: ValueEventListener){
        firebaseDatabase.child("users")
            .child(userId)
            .removeEventListener(valueEventListener)
    }

    override fun updateFirebaseUser(updatedUser: User){
        firebaseDatabase.child("users")
            .child(updatedUser.firebaseid!!)
            .setValue(updatedUser)
    }


}