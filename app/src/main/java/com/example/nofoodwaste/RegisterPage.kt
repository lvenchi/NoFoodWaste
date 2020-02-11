package com.example.nofoodwaste


import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.ShapeDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.example.nofoodwaste.databinding.FragmentRegisterPageBinding
import com.example.nofoodwaste.di.ComponentInjector
import com.example.nofoodwaste.utils.Utils
import com.example.nofoodwaste.utils.Utils.Companion.getImageUri
import com.example.nofoodwaste.viewmodels.LoginViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_register_page.*
import java.util.*
import javax.inject.Inject

private const val IMAGE_REQUEST_CODE = 222
private const val REQUEST_IMAGE_CAPTURE = 223
private const val CAMERA_REQUEST_CODE = 224
private const val READ_WRITE_REQUEST_CODE = 225

class RegisterPage : Fragment() {

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
        val binding : FragmentRegisterPageBinding = DataBindingUtil.inflate( inflater, R.layout.fragment_register_page, container, false)
        binding.lifecycleOwner = this
        binding.viewmodel = loginViewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        button_register.setOnClickListener {
            var isWellFormed = true

            if(!Utils.isEmail(loginViewModel.email.value)) {
                user_email.error = "Not a valid Email"
                isWellFormed = false
            }
            if(loginViewModel.password.value.isNullOrEmpty()) {
                password.error = "Invalid Password"
                isWellFormed = false
            } else
                if(loginViewModel.password.value!!.length < 6) {
                    password.error = "Password must be at least 6 char"
                    isWellFormed = false
                }

            if( isWellFormed ) loginViewModel.signUpWithEmailAndPassword()
        }

        gallery_button.setOnClickListener {
            if(ContextCompat.checkSelfPermission(activity!!, Manifest.permission.READ_EXTERNAL_STORAGE) != PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(activity!!, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PERMISSION_GRANTED)
                requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE), READ_WRITE_REQUEST_CODE)
            else {
                val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
                    .setType("image/*")
                    .addCategory(Intent.CATEGORY_OPENABLE)
                startActivityForResult(
                    Intent.createChooser(intent, "Select Picture"),
                    IMAGE_REQUEST_CODE
                )
            }
        }

        photo_button.setOnClickListener {
            if(ContextCompat.checkSelfPermission(activity!!, Manifest.permission.CAMERA) != PERMISSION_GRANTED)
                requestPermissions( arrayOf(Manifest.permission.CAMERA), CAMERA_REQUEST_CODE)
            else startActivityForResult(Intent(MediaStore.ACTION_IMAGE_CAPTURE), REQUEST_IMAGE_CAPTURE)
        }



        super.onViewCreated(view, savedInstanceState)
    }

    override fun onStart() {
        loginViewModel.profileImagePath.observe( this, androidx.lifecycle.Observer {
            if(it == null) register_image.setImageDrawable(activity!!.getDrawable(R.drawable.default_user))
            else register_image.setImageDrawable(ShapeDrawable.createFromStream(activity?.contentResolver?.openInputStream(Uri.parse(it)), it))
        })
        super.onStart()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if( requestCode == IMAGE_REQUEST_CODE && resultCode == Activity.RESULT_OK){
            val uri = data?.data
            val resUri: Uri? = getImageUri(activity!!, BitmapFactory.decodeStream(
                activity!!.contentResolver.openInputStream(Uri.parse(uri.toString()))!!)!!, Locale.ENGLISH)
            loginViewModel.profileImagePath.postValue(resUri.toString())

        } else if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK){
            val bm: Bitmap?
            if (data?.extras != null && data.extras?.get("data") as Bitmap? != null) {
                bm = data.extras?.get("data") as Bitmap
                val uri: Uri? = getImageUri(activity!!, bm, Locale.ENGLISH)
                loginViewModel.profileImagePath.postValue(uri.toString())
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if(requestCode == CAMERA_REQUEST_CODE ){
            if(grantResults[0] == PERMISSION_GRANTED) startActivityForResult(Intent(MediaStore.ACTION_IMAGE_CAPTURE), REQUEST_IMAGE_CAPTURE)
            else Snackbar.make(photo_button, "Camera Permission Denied", Snackbar.LENGTH_SHORT).show()
        }
        else if(requestCode == READ_WRITE_REQUEST_CODE ){
            if(grantResults[0] == PERMISSION_GRANTED && grantResults[1] == PERMISSION_GRANTED){
                val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
                    .setType("image/*")
                    .addCategory(Intent.CATEGORY_OPENABLE)
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), IMAGE_REQUEST_CODE)
            } else Snackbar.make(gallery_button, "Storage Permission Denied", Snackbar.LENGTH_SHORT).show()
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }


}
