package com.example.appointmentgeneration

import android.os.Bundle
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.appointmentgeneration.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private var backPressedTime: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupNavigation()
        handleBackPressed()
    }

    /** 네비게이션 초기화 및 상태 관리 */
    private fun setupNavigation() {
        val sharedPreferences = getSharedPreferences("UserPrefs", 0)
        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        // 로그인 상태에 따라 초기 화면 결정
        if (isLoggedIn) {
            navigateToHomeClearingBackstack()
        } else {
            navController.navigate(R.id.loginPage)
        }

        // BottomNavigationView 및 ActionBar 관리
        val appBarConfiguration = AppBarConfiguration(
            setOf(R.id.navigation_home, R.id.navigation_mypage, R.id.navigation_schedules)
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.bottomNavView.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.loginPage, R.id.signUpFragment -> hideBottomNavAndActionBar()
                R.id.destinationSelectionFragment -> {
                    hideBottomNav()
                    supportActionBar?.hide() // DestinationSelectionFragment에서 AppBar 비활성화
                }
                else -> showBottomNavAndActionBar()
            }
        }
    }

    /** 뒤로가기 버튼 처리 */
    private fun handleBackPressed() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (navController.currentDestination?.id == R.id.navigation_home) {
                    handleExitOnBackPressed()
                } else {
                    navController.navigateUp()
                }
            }
        })
    }

    /** 홈 화면에서 뒤로가기 시 종료 처리 */
    private fun handleExitOnBackPressed() {
        if (System.currentTimeMillis() - backPressedTime < 2000) {
            finish() // 앱 종료
        } else {
            Toast.makeText(this, "뒤로가기를 한 번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show()
            backPressedTime = System.currentTimeMillis()
        }
    }

    /** 로그인 상태에서 홈 화면으로 이동하며 백스택 초기화 */
    private fun navigateToHomeClearingBackstack() {
        navController.navigate(
            R.id.navigation_home,
            null,
            androidx.navigation.NavOptions.Builder()
                .setPopUpTo(R.id.loginPage, true) // 로그인 화면을 백스택에서 제거
                .build()
        )
    }

    private fun hideBottomNav() {
        binding.bottomNavView.visibility = android.view.View.GONE
    }

    /** BottomNavigationView 및 ActionBar 숨기기 */
    private fun hideBottomNavAndActionBar() {
        binding.bottomNavView.visibility = android.view.View.GONE
        supportActionBar?.hide()
    }

    /** BottomNavigationView 및 ActionBar 표시 */
    private fun showBottomNavAndActionBar() {
        binding.bottomNavView.visibility = android.view.View.VISIBLE
        supportActionBar?.show()
    }
}
