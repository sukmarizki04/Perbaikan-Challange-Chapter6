package com.example.challenge6.ui.profile

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import coil.load
import coil.transform.RoundedCornersTransformation
import com.example.challenge6.R
import com.example.challenge6.databinding.FragmentProfileBinding
import com.example.challenge6.utils.Utils
import dagger.hilt.android.AndroidEntryPoint
import java.io.ByteArrayOutputStream


const val REQUEST_CODE_PERMISSION = 201
@AndroidEntryPoint
class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val profileViewModel: ProfileViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnChangeImage.setOnClickListener { checkingPermission() }
        binding.btnProfileUpdate.setOnClickListener { toUpdateAccount() }
        binding.btnProfileLogout.setOnClickListener { toLogout() }

        profileViewModel.getImage().observe(viewLifecycleOwner) {
            if (it.isNullOrEmpty().not()) {
                setProfilePicture(Utils.convertStringToBitmap(it))
            } else {
                binding.ivProfile.setImageResource(R.drawable.ic_photo_profile)
            }
        }
    }

    private fun setProfilePicture(bitmap: Bitmap) {
        binding.ivProfile.load(bitmap) {
            crossfade(true)
            placeholder(R.drawable.ic_photo_profile)
            transformations(RoundedCornersTransformation())
        }
    }

    private fun checkingPermission() {
        if(isGranted(
                requireActivity(), Manifest.permission.CAMERA,
                arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.MANAGE_EXTERNAL_STORAGE
                ), REQUEST_CODE_PERMISSION
            )
        ){
            chooseImageDialog()
        }
    }

    private fun chooseImageDialog() {
        AlertDialog.Builder(requireContext())
            .setMessage("Choose Picture")
            .setPositiveButton("Gallery") { _, _ -> openGallery()}
            .show()
    }

    private fun isGranted(
        activity: Activity,
        permission: String,
        permissions: Array<String>,
        request: Int
    ): Boolean {
        val permissionCheck = ActivityCompat.checkSelfPermission(activity, permission)
        return if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                showPermissionDeniedDialog()
            } else {
                ActivityCompat.requestPermissions(activity, permissions, request)
            }
            false
        } else {
            true
        }
    }

    private fun openGallery() {
        requireActivity().intent.type = "image/*"
        galleryResult.launch("image/*")
    }

    private val galleryResult =
        registerForActivityResult(ActivityResultContracts.GetContent()) { result ->
            binding.ivProfile.load(result) {
                crossfade(true)
                placeholder(R.drawable.ic_photo_profile)
                target { result ->
                    val bitmap = (result as BitmapDrawable).bitmap
                    profileViewModel.uploadImage(Utils.convertBitmapToString(bitmap))
                    profileViewModel.applyBlur(getImageUri(bitmap))
                }
                transformations(RoundedCornersTransformation())
            }
        }

    private fun getImageUri(bitmap: Bitmap): Uri {
        val bytes = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path: String = MediaStore.Images.Media.insertImage(
            requireContext().contentResolver,
            bitmap,
            "Initial Profile Picture",
            null
        )
        return Uri.parse(path)
    }

    private fun showPermissionDeniedDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Permission Denied")
            .setMessage("Permission is Denied, pleas Allow Permission from settings")
            .setPositiveButton(
                "App Settings"
            ) { _, _ ->
                val intent = Intent()
                intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                startActivity(intent)
            }
            .setNegativeButton("Cancecl") { dialog, _ -> dialog.cancel() }
            .show()
    }

    private fun toUpdateAccount() {
        val usernameProfile = binding.etProfileUsername.text.toString()
        val fullnameProfile = binding.etProfileFullname.text.toString()
        val address = binding.etProfileAddress.text.toString()

        profileViewModel.editAccount(usernameProfile, fullnameProfile, address)
        Toast.makeText(requireContext(), "Update Success", Toast.LENGTH_SHORT).show()
        findNavController().navigate(R.id.action_profileFragment_to_homeFragment)
    }

    private fun toLogout() {
        val option = NavOptions.Builder()
            .setPopUpTo(R.id.profileFragment, true)
            .build()

        profileViewModel.statusLogin(false)
        findNavController().navigate(R.id.action_profileFragment_to_loginFragment, null, option)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}