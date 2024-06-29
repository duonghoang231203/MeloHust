package com.example.melohust

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.melohust.databinding.ActivitySongUploadBinding
import com.example.melohust.models.SongModel
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.example.melohust.util.UiUtil

class SongUploadActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySongUploadBinding
    private var selectedSongUri: Uri? = null
    private lateinit var songLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySongUploadBinding.inflate(layoutInflater)
        setContentView(binding.root)

        songLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                selectedSongUri = result.data?.data
                showPostView()
            }
        }

        binding.uploadView.setOnClickListener {
            checkPermissionAndOpenSongPicker()
        }

        binding.submitPostBtn.setOnClickListener {
            postSong()
        }

        binding.cancelPostBtn.setOnClickListener {
            finish()
        }
    }

    private fun postSong() {
        if (binding.postCaptionInput.text.toString().isEmpty()) {
            binding.postCaptionInput.error = "Write something"
            return
        }
        setInProgress(true)
        selectedSongUri?.let {
            val songRef = FirebaseStorage.getInstance()
                .reference
                .child("songs/${System.currentTimeMillis()}_${it.lastPathSegment}")
            songRef.putFile(it)
                .addOnSuccessListener {
                    songRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                        postToFirestore(downloadUrl.toString())
                    }.addOnFailureListener { e ->
                        setInProgress(false)
                        UiUtil.showToast(applicationContext, "Failed to get download URL: ${e.message}")
                    }
                }
                .addOnFailureListener { e ->
                    setInProgress(false)
                    UiUtil.showToast(applicationContext, "Failed to upload song: ${e.message}")
                }
        } ?: run {
            setInProgress(false)
            UiUtil.showToast(applicationContext, "No song selected")
        }
    }

    private fun postToFirestore(url: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: run {
            setInProgress(false)
            UiUtil.showToast(applicationContext, "User not authenticated")
            return
        }

        val songModel = SongModel(
            id = "$userId${System.currentTimeMillis()}",
            title = binding.postCaptionInput.text.toString(),
            subtitle = "", // Assuming subtitle is empty or you can set it to a meaningful value
            url = url,
            coverUrl = "", // Assuming coverUrl is empty or you can set it to a meaningful value
            createdTime = Timestamp.now()
        )

        Firebase.firestore.collection("songs")
            .document(songModel.id)
            .set(songModel)
            .addOnSuccessListener {
                setInProgress(false)
                UiUtil.showToast(applicationContext, "Song uploaded")
                finish()
            }
            .addOnFailureListener { e ->
                setInProgress(false)
                UiUtil.showToast(applicationContext, "Failed to upload song details: ${e.message}")
            }
    }


    private fun setInProgress(inProgress: Boolean) {
        if (inProgress) {
            binding.progressBar.visibility = View.VISIBLE
            binding.submitPostBtn.visibility = View.GONE
        } else {
            binding.progressBar.visibility = View.GONE
            binding.submitPostBtn.visibility = View.VISIBLE
        }
    }

    private fun showPostView() {
        selectedSongUri?.let {
            binding.postView.visibility = View.VISIBLE
            binding.uploadView.visibility = View.GONE
            Glide.with(binding.postThumbnailView).load(it).into(binding.postThumbnailView)
        }
    }

    private fun checkPermissionAndOpenSongPicker() {
        val readExternalAudio = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            android.Manifest.permission.READ_MEDIA_AUDIO
        } else {
            android.Manifest.permission.READ_EXTERNAL_STORAGE
        }
        if (ContextCompat.checkSelfPermission(this, readExternalAudio) == PackageManager.PERMISSION_GRANTED) {
            openSongPicker()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(readExternalAudio),
                100
            )
        }
    }

    private fun openSongPicker() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "audio/*"
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        songLauncher.launch(intent)
    }

}
