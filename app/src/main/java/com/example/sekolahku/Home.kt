package com.example.sekolahku

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.sekolahku.fragment.Home_fragment
import com.example.sekolahku.fragment.Profile_fragment
import com.example.sekolahku.fragment.RiwayatFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class Home : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavigation.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.riwayat -> {
                    replaceFragment(RiwayatFragment())
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.home -> {
                    replaceFragment(Home_fragment())
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.profile -> {
                    replaceFragment(Profile_fragment())
                    return@setOnNavigationItemSelectedListener true
                }
                else -> false
            }
        }

        // Menampilkan fragment HomeFragment saat aplikasi pertama kali dibuka
        bottomNavigation.selectedItemId = R.id.home
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.fragmentContainer, fragment).commit()

    }
}