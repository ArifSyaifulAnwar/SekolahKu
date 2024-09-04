package com.example.sekolahku


import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class DataUjian : AppCompatActivity() {

    private lateinit var spinnerNik: Spinner
    private lateinit var semesterInput: TextInputEditText
    private lateinit var nilaiInput: TextInputEditText
    private lateinit var saveButton: Button

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_data_ujian) // Update this to your actual layout file
        // Initialize views
        spinnerNik = findViewById(R.id.spinnerNik)
        semesterInput = findViewById(R.id.durasilatihan)
        nilaiInput = findViewById(R.id.kkal)
        saveButton = findViewById(R.id.button2)

        // Fetch NIK data from Firestore and populate the spinner
        fetchNikData()

        // Set up the save button click listener
        saveButton.setOnClickListener {
            saveDataToFirestore()
        }
    }

    private fun fetchNikData() {
        val userId = auth.currentUser?.uid ?: return

        firestore.collection("users")
            .document(userId)
            .collection("data_siswa")
            .get()
            .addOnSuccessListener { result ->
                val nikList = mutableListOf<String>()
                for (document in result) {
                    val nik = document.getString("NIK Siswa")
                    nik?.let { nikList.add(it) }
                }
                // Create an ArrayAdapter and set it on the Spinner
                val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, nikList)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinnerNik.adapter = adapter
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error fetching data: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun saveDataToFirestore() {
        // Check if the user is logged in
        val currentUser = auth.currentUser
        if (currentUser == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        // Get selected NIK, semester, and nilai
        val selectedNik = spinnerNik.selectedItem.toString()
        val semester = semesterInput.text.toString()
        val nilai = nilaiInput.text.toString()

        if (semester.isEmpty() || nilai.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        // Get the user ID
        val userId = currentUser.uid

        // Create a map to hold the data
        val examData = hashMapOf(
            "NIK Siswa" to selectedNik,
            "Semester" to semester,
            "Nilai" to nilai
        )

        // Save data to Firestore
        firestore.collection("users")
            .document(userId)
            .collection("data_ujian")
            .add(examData)
            .addOnSuccessListener {
                Toast.makeText(this, "Data saved successfully", Toast.LENGTH_SHORT).show()
                clearInputs()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error saving data: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun clearInputs() {
        semesterInput.text?.clear()
        nilaiInput.text?.clear()
    }
}
