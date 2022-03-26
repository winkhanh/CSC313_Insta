package winkhanh.com.insta.fragments

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.FragmentManager
import com.bumptech.glide.Glide
import com.parse.*
import winkhanh.com.insta.R
import winkhanh.com.insta.models.Post
import java.io.File

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"


/**
 * A simple [Fragment] subclass.
 * Use the [ProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProfileFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var userId: String = ""
    lateinit var tvProfileName: TextView
    lateinit var ivProfilePhoto: ImageView
    lateinit var feedFragmentManager: FragmentManager
    val PROFILE_PIC_KEY = "profilePicture"

    val photoFileName = "photo.jpg"
    lateinit var photoFile: File

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            userId = it.getString(ARG_PARAM1)?:""

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.

         * @return A new instance of fragment ProfileFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(userId : String) =
            ProfileFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, userId)
                }
            }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ivProfilePhoto = view.findViewById(R.id.ivProfilePihoto)
        tvProfileName = view.findViewById(R.id.tvProfileName)

        val userQuery : ParseQuery<ParseUser> = ParseQuery.getQuery(ParseUser::class.java)
        userQuery.whereEqualTo(ParseUser.KEY_OBJECT_ID, userId)
        userQuery.findInBackground(object: FindCallback<ParseUser> {
            override fun done(objects: MutableList<ParseUser>?, e: ParseException?) {
                if (objects==null || e!=null)
                    return
                val user  : ParseUser = objects[0]
                tvProfileName.text = user.username
                val photoUrl : String = user.getParseFile(PROFILE_PIC_KEY)?.url ?: ""
                context?.let { Glide.with(it).load(photoUrl).placeholder(R.drawable.default_avatar).error(R.drawable.default_avatar).into(ivProfilePhoto) }
            }

        })



        feedFragmentManager = childFragmentManager
        val childFragment :Fragment = FeedFragment.newInstanceWithId(userId)

        feedFragmentManager.beginTransaction().replace(R.id.flFeedContainer, childFragment).commit()
        if (ParseUser.getCurrentUser().objectId==userId)
            ivProfilePhoto.setOnClickListener { launchCamera() }
    }
    fun launchCamera(){
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        photoFile = getPhotoFileUri(photoFileName)

        if (photoFile != null) {
            val fileProvider: Uri? =
                context?.let { FileProvider.getUriForFile(it, "com.codepath.fileprovider", photoFile!!) }
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider)


            // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
            // So as long as the result is not null, it's safe to use the intent.
            resultLauncher.launch(intent)
        }
    }

    var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result->
        if (result.resultCode == Activity.RESULT_OK ){
            val takenImage : Bitmap = BitmapFactory.decodeFile(photoFile.absolutePath)
            ivProfilePhoto.setImageBitmap(takenImage)
            ParseUser.getCurrentUser().put(PROFILE_PIC_KEY,ParseFile(photoFile)!!)
            ParseUser.getCurrentUser().saveInBackground()
        }
    }

    private fun getPhotoFileUri(fileName: String): File {
        // Get safe storage directory for photos
        // Use `getExternalFilesDir` on Context to access package-specific directories.
        // This way, we don't need to request external read/write runtime permissions.
        val mediaStorageDir =
            File(requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), "Compose")

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
            Log.d("Compose", "failed to create directory")
        }

        // Return the file target for the photo based on filename
        return File(mediaStorageDir.path + File.separator + fileName)
    }
}