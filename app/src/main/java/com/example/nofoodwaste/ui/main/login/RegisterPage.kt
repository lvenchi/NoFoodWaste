package com.example.nofoodwaste.ui.main.login


import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.graphics.drawable.ShapeDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.example.nofoodwaste.R
import com.example.nofoodwaste.databinding.FragmentRegisterPageBinding
import com.example.nofoodwaste.di.ComponentInjector
import com.example.nofoodwaste.utils.Utils
import com.example.nofoodwaste.utils.Utils.Companion.getImageUri
import com.example.nofoodwaste.viewmodels.LoginViewModel
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.fragment_register_page.*
import kotlinx.android.synthetic.main.fragment_register_page.gallery_button
import kotlinx.android.synthetic.main.fragment_register_page.photo_button
import java.io.File
import java.io.IOException
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

private const val REQUEST_TAKE_PHOTO = 223
private const val IMAGE_REQUEST_CODE = 222
private const val CAMERA_REQUEST_CODE = 224
private const val READ_WRITE_REQUEST_CODE = 225

class RegisterPage : Fragment() {

    @Inject
    lateinit var loginViewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        ComponentInjector.component.inject(this)
        loginViewModel.firebaseUser.observe( this, androidx.lifecycle.Observer {
            if(it != null ){
                findNavController().navigate(RegisterPageDirections.actionRegisterPageToMainFragment())
            }
        })

        loginViewModel.socialErrors.observe( this, androidx.lifecycle.Observer {
            if(it!= null) Snackbar.make(button_register, it, Snackbar.LENGTH_SHORT).show()
        })

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
                user_email.error = view.context.getString(R.string.invalid_mail)
                isWellFormed = false
            }
            if(loginViewModel.password.value.isNullOrEmpty()) {
                password.error = getString(R.string.invalid_password)
                isWellFormed = false
            } else
                if(loginViewModel.password.value!!.length < 6) {
                    password.error = getString(R.string.password_length_error)
                    isWellFormed = false
                }

            if( isWellFormed ) loginViewModel.signUpWithEmailAndPassword()
        }

        gallery_button.setOnClickListener {
            if(ContextCompat.checkSelfPermission(activity!!, Manifest.permission.READ_EXTERNAL_STORAGE) != PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(activity!!, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PERMISSION_GRANTED)
                requestPermissions(
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    READ_WRITE_REQUEST_CODE
                )
            else {
                val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
                    .setType("image/*")
                    .addCategory(Intent.CATEGORY_OPENABLE)
                startActivityForResult(
                    Intent.createChooser(intent, getString(R.string.select_picture)),
                    IMAGE_REQUEST_CODE
                )
            }
        }

        photo_button.setOnClickListener {
            if(ContextCompat.checkSelfPermission(activity!!, Manifest.permission.CAMERA) != PERMISSION_GRANTED)
                requestPermissions( arrayOf(Manifest.permission.CAMERA),
                    CAMERA_REQUEST_CODE
                )
            else dispatchTakePictureIntent()
        }

        loginViewModel.profileImagePath.observe( viewLifecycleOwner, androidx.lifecycle.Observer {
            if(it != null){
                val cachedImage = loginViewModel.getCachedImage(it)
                if(cachedImage != null ) register_image.setImageBitmap(cachedImage) else {

                    val imageTarget = object : Target {
                        override fun onPrepareLoad(placeHolderDrawable: Drawable?) {

                        }

                        override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {
                            register_image.setImageDrawable(context?.getDrawable(R.drawable.default_user))
                        }

                        override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                            loginViewModel.updateCache(bitmap!!, it)
                            register_image.setImageBitmap(bitmap)
                        }

                    }
                    Picasso.get().load(it).into(imageTarget)
                }
            }
        })

        super.onViewCreated(view, savedInstanceState)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if( requestCode == IMAGE_REQUEST_CODE && resultCode == Activity.RESULT_OK){
            val uri = data?.data
            val resUri: Uri? = getImageUri(activity!!, BitmapFactory.decodeStream(
                activity!!.contentResolver.openInputStream(Uri.parse(uri.toString()))!!)!!, Locale.ENGLISH)
            loginViewModel.profileImagePath.postValue(resUri.toString())

        } else  if(requestCode == REQUEST_TAKE_PHOTO && resultCode == Activity.RESULT_OK){
            loginViewModel.profileImagePath.postValue(currentPhotoPath)
            Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE).also { mediaScanIntent ->
                val f = File(photoPath2)
                mediaScanIntent.data = Uri.fromFile(f)
                activity?.sendBroadcast(mediaScanIntent)
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if(requestCode == REQUEST_TAKE_PHOTO){
            if(grantResults[0] == PERMISSION_GRANTED) dispatchTakePictureIntent()
            else Snackbar.make(photo_button, getString(R.string.camera_permission_error), Snackbar.LENGTH_SHORT).show()
        }
        else if(requestCode == READ_WRITE_REQUEST_CODE){
            if(grantResults[0] == PERMISSION_GRANTED && grantResults[1] == PERMISSION_GRANTED){
                val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
                    .setType("image/*")
                    .addCategory(Intent.CATEGORY_OPENABLE)
                startActivityForResult(Intent.createChooser(intent, getString(R.string.select_picture)),
                    IMAGE_REQUEST_CODE
                )
            } else Snackbar.make(gallery_button, getString(R.string.storage_permission_error), Snackbar.LENGTH_SHORT).show()
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    lateinit var photoPath2: String
    lateinit var currentPhotoPath: String

    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Utils.currentLocale).format(Date())
        val storageDir: File = activity?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            photoPath2 = absolutePath
            currentPhotoPath = absolutePath
        }
    }



    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(activity!!.packageManager)?.also {
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    null
                }
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        activity!!,
                        "com.example.android.fileprovider",
                        it
                    )
                    currentPhotoPath = photoURI.toString()
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO)
                }
            }
        }
    }


}
