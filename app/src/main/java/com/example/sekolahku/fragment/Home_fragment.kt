package com.example.sekolahku.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.sekolahku.DataSiswa
import com.example.sekolahku.DataUjian
import com.example.sekolahku.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class Home_fragment : Fragment() {

    private lateinit var namaTextView: TextView
    private lateinit var fotoProfileImageView: ImageView
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var containernutrisi: View
    private lateinit var containerpelacak : View


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home_fragment, container, false)

        // Initialize Firestore and FirebaseAuth
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        // Initialize Views
        namaTextView = view.findViewById(R.id.nama)
        fotoProfileImageView = view.findViewById(R.id.fotoProfile)
        containernutrisi = view.findViewById(R.id.containernutrisi)  // Initialize here
        containerpelacak = view.findViewById(R.id.containerpelacak)
        // Load profile data from Firestore
        loadProfileData()

        containernutrisi.setOnClickListener {
            // Navigate to DataSiswaActivity
            val intent = Intent(activity, DataSiswa::class.java)
            startActivity(intent)
        }
        containerpelacak.setOnClickListener {
            // Navigate to DataSiswaActivity
            val intent = Intent(activity, DataUjian::class.java)
            startActivity(intent)
        }

        return view
    }

    private fun loadProfileData() {
        val userId = auth.currentUser?.uid
        userId?.let { uid ->
            firestore.collection("users").document(uid)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        val nama = document.getString("username")
                        namaTextView.text = nama
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e("ProfileMenu", "Error getting user data", exception)
                }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Ensure fragment is attached to the activity
        val context = context ?: return  // Use context directly if not null

        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val storageRef = FirebaseStorage.getInstance().reference
        val profilePictureRef = storageRef.child("profile_pictures").child("$userId.jpg")

        profilePictureRef.downloadUrl.addOnSuccessListener { uri ->
            Glide.with(context)
                .load(uri)
                .placeholder(R.drawable.profile)
                .into(fotoProfileImageView)
        }.addOnFailureListener { e ->
            Log.e("ProfileMenu", "Error loading profile picture from Firebase Storage", e)
        }
    }
}
