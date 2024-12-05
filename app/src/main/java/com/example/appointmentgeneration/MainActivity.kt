package com.example.appointmentgeneration
import androidx.navigation.NavController
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.appointmentgeneration.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.bottomNavView
        navController = binding.navHostFragment.getFragment<NavHostFragment>().navController

        if (isUserLoggedIn()) {
            navController.navigate(R.id.navigation_home)
        } else {
            navController.navigate(R.id.loginPage)
        }

        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home,
                R.id.navigation_mypage,
                R.id.navigation_schedules
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)

        // 하단 네비게이션 클릭 리스너 추가
        navView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    navController.navigate(R.id.navigation_home)
                    true
                }
                R.id.navigation_mypage -> {
                    navController.navigate(R.id.navigation_mypage)
                    true
                }
                R.id.navigation_schedules -> {
                    navController.navigate(R.id.navigation_schedules)
                    true
                }
                else -> false
            }
        }

        // 기존 destination changed 리스너 유지
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
        return false
    }
}