package com.example.musicapp.ui.activity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.musicapp.R
import com.example.musicapp.databinding.ActivityDashboardBinding
import com.example.musicapp.ui.fragment.HomeFragment
import com.example.musicapp.ui.fragment.LibraryFragment
import com.example.musicapp.ui.fragment.NowPlayingFragment
import com.example.musicapp.ui.fragment.ProfileFragment
import com.example.musicapp.ui.fragment.SearchFragment

class DashboardActivity : AppCompatActivity() {

    lateinit var binding: ActivityDashboardBinding

    fun replaceFragment(fragment: Fragment) {
        val fragmentManager : FragmentManager = supportFragmentManager
        val fragmentTransaction : FragmentTransaction =
            fragmentManager.beginTransaction()

        fragmentTransaction.replace(R.id.frameBottom,fragment)
        fragmentTransaction.commit()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding =ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        replaceFragment(HomeFragment())
        binding.bottomView.setOnItemSelectedListener {
            // it stores value of function
            when(it.itemId){
                R.id.navHome->replaceFragment(HomeFragment())
                R.id.navPlay-> replaceFragment(NowPlayingFragment())
                R.id.navSearch->replaceFragment(SearchFragment())
                R.id.navLib-> replaceFragment(LibraryFragment())
                R.id.navProfile-> replaceFragment(ProfileFragment())
                else->{}
            }
            true

        }
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}