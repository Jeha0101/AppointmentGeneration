import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.appointmentgeneration.databinding.FragmentDestinationSelectionBinding
import com.example.appointmentgeneration.ui.destinations.DestinationFirstFragment
import com.example.appointmentgeneration.ui.destinations.DestinationSecondFragment
import com.example.appointmentgeneration.ui.destinations.DestinationThirdFragment
import com.google.android.material.tabs.TabLayoutMediator

class DestinationSelectionFragment : Fragment() {

    private lateinit var binding: FragmentDestinationSelectionBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDestinationSelectionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // ViewPager2와 TabLayout 연결 설정
        setupViewPagerWithTabs()
    }

    private fun setupViewPagerWithTabs() {
        // ViewPager2 어댑터 설정
        val adapter = DestinationPagerAdapter(this)
        binding.viewPager.adapter = adapter

        // TabLayout과 ViewPager2 연결
        TabLayoutMediator(binding.destinationsTabLayout, binding.viewPager) { tab, position ->
            when (position) {
                0 -> tab.text = "주제 선택"
                1 -> tab.text = "지도 검색"
                2 -> tab.text = "동네 선택"
            }
        }.attach()
    }
}

class DestinationPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = 3 // 총 3개의 페이지

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> DestinationFirstFragment()  // 주제 선택
            1 -> DestinationSecondFragment() // 지도 검색
            2 -> DestinationThirdFragment()  // 동네 선택
            else -> throw IllegalStateException("Invalid position: $position")
        }
    }
}
