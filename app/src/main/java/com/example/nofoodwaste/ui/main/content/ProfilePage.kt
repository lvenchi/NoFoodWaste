package com.example.nofoodwaste.ui.main.content


import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
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
import androidx.core.graphics.drawable.toDrawable
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import com.example.nofoodwaste.R
import com.example.nofoodwaste.databinding.FragmentProfileBinding
import com.example.nofoodwaste.di.ComponentInjector
import com.example.nofoodwaste.utils.Utils
import com.example.nofoodwaste.viewmodels.LoginViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.fragment_profile.gallery_button
import kotlinx.android.synthetic.main.fragment_profile.photo_button
import kotlinx.android.synthetic.main.fragment_register_page.*
import kotlinx.android.synthetic.main.main_fragment.*
import java.io.File
import java.io.IOException
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject


private const val IMAGE_REQUEST_CODE = 222
private const val REQUEST_TAKE_PHOTO = 223
private const val CAMERA_REQUEST_CODE = 224
private const val READ_WRITE_REQUEST_CODE = 225

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

        val binding : FragmentProfileBinding = DataBindingUtil.inflate( inflater, R.layout.fragment_profile, container, false)
        binding.lifecycleOwner = this
        binding.loginviewmodel = loginViewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        logout_button.setOnClickListener {
            loginViewModel.logOut()
            activity?.findNavController(R.id.nav_host_fragment)?.navigate(MainFragmentDirections.actionMainFragmentToLoginPage())
        }

        edit_button.setOnClickListener {
            loginViewModel.updateFirebaseUser()
            BottomSheetBehavior.from(bottom_sheet_container).state = BottomSheetBehavior.STATE_COLLAPSED
        }

        gallery_button.setOnClickListener {
            if(ContextCompat.checkSelfPermission(activity!!, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(activity!!, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
            )
                requestPermissions(
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    READ_WRITE_REQUEST_CODE
                )
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
            if(ContextCompat.checkSelfPermission(activity!!, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
                requestPermissions(
                    arrayOf(Manifest.permission.CAMERA),
                    CAMERA_REQUEST_CODE
                )
            else dispatchTakePictureIntent()
        }




        loginViewModel.profileImagePath.observe( viewLifecycleOwner, Observer {


                val cachedImage = loginViewModel.getCachedImage(it)
                if(cachedImage != null ) profile_pic_image.setImageBitmap(cachedImage) else {

                    val imageTarget = object : Target {
                        override fun onPrepareLoad(placeHolderDrawable: Drawable?) {

                        }

                        override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {
                            profile_pic_image.setImageDrawable(context?.getDrawable(R.drawable.default_user))
                        }

                        override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                            loginViewModel.updateCache(bitmap!!, it)
                            if(profile_pic_image != null ) profile_pic_image.setImageBitmap(bitmap)
                        }

                    }
                    Picasso.get().load(it).into(imageTarget)
                }

        })


        loginViewModel.currentUser.observe( viewLifecycleOwner, Observer {

            if(it != null ){
                loginViewModel.email.postValue(it.email)
                loginViewModel.name.postValue(it.name)
                loginViewModel.lastName.postValue(it.surname)
                loginViewModel.profileImagePath.postValue(it.photoLocation)
            }

        })

        super.onViewCreated(view, savedInstanceState)
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if(requestCode == CAMERA_REQUEST_CODE){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED) dispatchTakePictureIntent()
            else Snackbar.make(photo_button, "Camera Permission Denied", Snackbar.LENGTH_SHORT).show()
        }
        else if(requestCode == READ_WRITE_REQUEST_CODE){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED){
                val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
                    .setType("image/*")
                    .addCategory(Intent.CATEGORY_OPENABLE)
                startActivityForResult(
                    Intent.createChooser(intent, "Select Picture"),
                    IMAGE_REQUEST_CODE
                )
            } else Snackbar.make(gallery_button, "Storage Permission Denied", Snackbar.LENGTH_SHORT).show()
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if( requestCode == IMAGE_REQUEST_CODE && resultCode == Activity.RESULT_OK){
            val uri = data?.data
            val resUri: Uri? = Utils.getImageUri(
                activity!!, BitmapFactory.decodeStream(
                    activity!!.contentResolver.openInputStream(Uri.parse(uri.toString()))!!
                )!!, Locale.ENGLISH
            )
            loginViewModel.profileImagePath.postValue(resUri.toString())

        } else if(requestCode == REQUEST_TAKE_PHOTO && resultCode == Activity.RESULT_OK){
            loginViewModel.profileImagePath.postValue(currentPhotoPath)
            Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE).also { mediaScanIntent ->
                val f = File(photoPath2)
                mediaScanIntent.data = Uri.fromFile(f)
                activity?.sendBroadcast(mediaScanIntent)
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    lateinit var photoPath2: String
    lateinit var currentPhotoPath: String

    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
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
