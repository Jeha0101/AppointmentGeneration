package com.example.appointmentgeneration

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.bottomNavView
        val navController = binding.navHostFragment.getFragment<NavHostFragment>().navController

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
        navView.setupWithNavController(navController)

        // 네비게이션 변경 시 앱바와 네비게이션 바 표시/숨김 제어
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.loginPage, R.id.signUpFragment -> { // 숨김: 로그인 및 회원가입 화면
                    supportActionBar?.hide()
                    navView.visibility = View.GONE
                }
                else -> { // 표시: 다른 화면
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
