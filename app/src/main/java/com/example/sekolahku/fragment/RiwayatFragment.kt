package com.example.sekolahku.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.sekolahku.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class RiwayatFragment : Fragment() {

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private lateinit var namaTextView: TextView
    private lateinit var fotoProfileImageView: ImageView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_riwayat, container, false)
        val tableLayout = view.findViewById<android.widget.TableLayout>(R.id.tableLayout)
        namaTextView = view.findViewById(R.id.nama)
        fotoProfileImageView = view.findViewById(R.id.fotoProfile)
        // Fetch and populate data
        fetchDataAndPopulateTable(tableLayout)
        loadProfileData()
        return view
    }

    private fun fetchDataAndPopulateTable(tableLayout: android.widget.TableLayout) {
        val userId = auth.currentUser?.uid ?: return

        // Fetch data from data_siswa
        val siswaData = mutableMapOf<String, Pair<String, String>>() // NIK to (Nama, Kelas)
        firestore.collection("users")
            .document(userId)
            .collection("data_siswa")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val nik = document.getString("NIK Siswa") ?: ""
                    val nama = document.getString("Nama Siswa") ?: ""
                    val kelas = document.getString("Kelas") ?: ""
                    siswaData[nik] = Pair(nama, kelas)
                }

                // Fetch data from data_ujian
                fetchExamDataAndPopulateTable(tableLayout, siswaData)
            }
            .addOnFailureListener { e ->
                // Handle errors
                Toast.makeText(context, "Error fetching siswa data: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun fetchExamDataAndPopulateTable(tableLayout: android.widget.TableLayout, siswaData: Map<String, Pair<String, String>>) {
        val userId = auth.currentUser?.uid ?: return

        firestore.collection("users")
            .document(userId)
            .collection("data_ujian")
            .get()
            .addOnSuccessListener { result ->
                // Remove existing rows except the header
                tableLayout.removeAllViews()
                tableLayout.addView(createTableHeader())

                for (document in result) {
                    val nikSiswa = document.getString("NIK Siswa") ?: ""
                    val semester = document.getString("Semester") ?: ""
                    val nilai = document.getString("Nilai") ?: ""

                    // Get corresponding Nama and Kelas from siswaData
                    val (namaSiswa, kelas) = siswaData[nikSiswa] ?: Pair("", "")

                    // Add a new row with data
                    val row = TableRow(context)
                    val nikTextView = TextView(context).apply {
                        text = nikSiswa
                        setPadding(8.dp, 8.dp, 8.dp, 8.dp)
                    }
                    val namaTextView = TextView(context).apply {
                        text = namaSiswa
                        setPadding(8.dp, 8.dp, 8.dp, 8.dp)
                    }
                    val kelasTextView = TextView(context).apply {
                        text = kelas
                        setPadding(8.dp, 8.dp, 8.dp, 8.dp)
                    }
                    val semesterTextView = TextView(context).apply {
                        text = semester
                        setPadding(8.dp, 8.dp, 8.dp, 8.dp)
                    }
                    val nilaiTextView = TextView(context).apply {
                        text = nilai
                        setPadding(8.dp, 8.dp, 8.dp, 8.dp)
                    }

                    row.addView(nikTextView)
                    row.addView(namaTextView)
                    row.addView(kelasTextView)
                    row.addView(semesterTextView)
                    row.addView(nilaiTextView)
                    tableLayout.addView(row)
                }
            }
            .addOnFailureListener { e ->
                // Handle errors
                Toast.makeText(context, "Error fetching ujian data: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun createTableHeader(): TableRow {
        val headerRow = TableRow(context)
        val headers = arrayOf("NIK", "Nama", "Kelas", "Semester", "Nilai")
        headers.forEach { header ->
            val headerTextView = TextView(context).apply {
                text = header
                textSize = 16f
                setPadding(8.dp, 8.dp, 8.dp, 8.dp)
                setBackgroundColor(android.graphics.Color.parseColor("#0000FF"))
                setTextColor(android.graphics.Color.WHITE)
            }
            headerRow.addView(headerTextView)
        }
        return headerRow
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

    private val Int.dp: Int
        get() = (this * resources.displayMetrics.density).toInt()
}
