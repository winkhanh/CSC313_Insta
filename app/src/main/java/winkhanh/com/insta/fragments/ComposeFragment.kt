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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.parse.ParseException
import com.parse.ParseFile
import com.parse.ParseUser
import com.parse.SaveCallback
import winkhanh.com.insta.MainActivity
import winkhanh.com.insta.R
import winkhanh.com.insta.models.Post
import java.io.File


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ComposeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ComposeFragment : Fragment() {
    // TODO: Rename and change types of parameters

    lateinit var etDescription : EditText
    lateinit var btSubmit : Button
    lateinit var postedImage : ImageView
    lateinit var loading : ProgressBar
    val photoFileName = "photo.jpg"
    lateinit var photoFile: File
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_compose, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ComposeFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance() =
            ComposeFragment()

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)
        btSubmit = view.findViewById(R.id.btSubmit)
        etDescription = view.findViewById(R.id.etDescription)
        postedImage = view.findViewById(R.id.evImage)
        loading = view.findViewById(R.id.loading)
        loading.visibility=ProgressBar.INVISIBLE
        launchCamera()
        btSubmit.setOnClickListener {
            val description = etDescription.text.toString()
            if (description.isEmpty()){
                Toast.makeText(context,"Description can't be empty",Toast.LENGTH_LONG).show()
            }else if (photoFile == null || postedImage.drawable==null){
                Toast.makeText(context, "Photo can't be empty",Toast.LENGTH_LONG).show()
            }else{
                val currentUser = ParseUser.getCurrentUser()
                savePost(description,currentUser,photoFile)

            }
        }
        postedImage.setOnClickListener {
            launchCamera()
        }
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
            postedImage.setImageBitmap(takenImage)
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
    fun savePost(description: String, currentUser: ParseUser, photo: File){
        loading.visibility=ProgressBar.VISIBLE
        val post = Post()
        post.setDescription(description)
        post.setUser(currentUser)
        post.setImage(ParseFile(photo))
        post.saveInBackground {
            postedImage.setImageResource(0)
            etDescription.setText("")
            (activity as MainActivity).backToHome()
        }
    }
}