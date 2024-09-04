package com.example.sekolahku



import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class DataSiswa : AppCompatActivity() {

    private lateinit var nikSiswaInput: TextInputEditText
    private lateinit var namaSiswaInput: TextInputEditText
    private lateinit var kelasInput: TextInputEditText
    private lateinit var saveButton: Button

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_data_siswa) // Update this to your actual layout file

        // Initialize views
        nikSiswaInput = findViewById(R.id.namakegiatan)
        namaSiswaInput = findViewById(R.id.durasilatihan)
        kelasInput = findViewById(R.id.kkal)
        saveButton = findViewById(R.id.button2)

        // Set up the save button click listener
        saveButton.setOnClickListener {
            saveDataToFirestore()
        }
    }

    private fun saveDataToFirestore() {
        // Check if the user is logged in
        val currentUser = auth.currentUser
        if (currentUser == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        // Get input data
        val nikSiswa = nikSiswaInput.text.toString()
        val namaSiswa = namaSiswaInput.text.toString()
        val kelas = kelasInput.text.toString()

        if (nikSiswa.isEmpty() || namaSiswa.isEmpty() || kelas.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        // Get the user ID
        val userId = currentUser.uid

        // Create a map to hold the data
        val studentData = hashMapOf(
            "NIK Siswa" to nikSiswa,
            "Nama Siswa" to namaSiswa,
            "Kelas" to kelas
        )

        // Save data to Firestore
        firestore.collection("users")
            .document(userId)
            .collection("data_siswa")
            .add(studentData)
            .addOnSuccessListener {
                Toast.makeText(this, "Data saved successfully", Toast.LENGTH_SHORT).show()
                clearInputs()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error saving data: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun clearInputs() {
        nikSiswaInput.text?.clear()
        namaSiswaInput.text?.clear()
        kelasInput.text?.clear()
    }
}
