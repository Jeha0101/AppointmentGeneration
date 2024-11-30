package com.example.appointmentgeneration

import android.os.Bundle
import android.view.View
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.appointmentgeneration.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_activity_main)

        if (isUserLoggedIn()) {
            navController.navigate(R.id.navigation_home)
        } else {
            navController.navigate(R.id.loginPage)
        }

        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_mypage, R.id.navigation_home, R.id.navigation_schedules
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.loginPage, R.id.signUpFragment -> {
                    supportActionBar?.hide()
                    navView.visibility = View.GONE
                }
                else -> {
                    supportActionBar?.show()
                    navView.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun isUserLoggedIn(): Boolean {
        // 로그인 상태를 확인하는 로직 구현하기
        // 로그인 상태라면 true를 반환하고, 그렇지 않으면 false를 반환합니다.
        return false
    }
}
