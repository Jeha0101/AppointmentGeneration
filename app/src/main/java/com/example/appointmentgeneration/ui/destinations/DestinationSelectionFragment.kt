package com.example.appointmentgeneration.ui.destinations

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.appointmentgeneration.R
import com.example.appointmentgeneration.databinding.FragmentDestinationSelectionBinding
import com.google.android.material.tabs.TabLayoutMediator

class DestinationSelectionFragment : Fragment() {
    private lateinit var binding: FragmentDestinationSelectionBinding
    private val selectedTags = mutableListOf<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDestinationSelectionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Toolbar 뒤로 가기 버튼 설정
        binding.toolbar.setNavigationOnClickListener {
            // 선택한 태그 전달
            val result = Bundle().apply {
                putStringArrayList("selectedTags", ArrayList(selectedTags))
            }
            parentFragmentManager.setFragmentResult("selectedTagsKey", result)

            findNavController().navigateUp() // 뒤로 가기
        }

        setupViewPagerWithTabs()
        updateTags()
    }

    override fun onDestroyView() {
        super.onDestroyView()

        // 선택한 태그들을 HomeFragment로 전달
        val result = Bundle().apply {
            putStringArrayList("selectedTags", ArrayList(selectedTags)) // 선택한 태그 전달
        }

        parentFragmentManager.setFragmentResult("selectedTagsKey", result) // 프래그먼트 결과 전달
    }

    private fun setupViewPagerWithTabs() {
        val adapter = DestinationPagerAdapter(this)
        binding.viewPager.adapter = adapter

        TabLayoutMediator(binding.destinationsTabLayout, binding.viewPager) { tab, position ->
            when (position) {
                0 -> tab.text = "주제 선택"
                1 -> tab.text = "동네 선택"
            }
        }.attach()
    }

    fun addTag(tag: String) {
        if (tag !in selectedTags) {
            selectedTags.add(tag)
            updateTags() // 태그 UI 업데이트
        }
    }

    private fun removeTag(tag: String) {
        selectedTags.remove(tag)
        updateTags() // 태그 UI 업데이트
    }
    private fun updateTags() {
        val constraintLayout = binding.flowTags.parent as ConstraintLayout

        // 기존 태그 삭제
        for (i in selectedTags.indices) {
            val oldTagView = constraintLayout.findViewWithTag<View>("tag_$i")
            if (oldTagView != null) {
                constraintLayout.removeView(oldTagView)
            }
        }

        // 새 태그 추가
        val referencedIds = mutableListOf<Int>()
        for ((index, tag) in selectedTags.withIndex()) {
            val tagView = createTagView(tag)
            tagView.id = View.generateViewId() // 동적 ID 생성
            tagView.tag = "tag_$index" // 태그 설정
            constraintLayout.addView(tagView) // ConstraintLayout에 추가
            referencedIds.add(tagView.id) // ID 리스트에 추가
        }

        // Flow의 참조 ID 업데이트
        val flow = binding.flowTags
        flow.referencedIds = referencedIds.toIntArray()
    }

    // 태그 뷰 생성
    private fun createTagView(tag: String): View {
        val tagView = layoutInflater.inflate(R.layout.tag_item, null, false) as LinearLayout
        val tagText = tagView.findViewById<TextView>(R.id.tag_text)
        tagText.text = tag

        val removeButton = tagView.findViewById<View>(R.id.tag_remove_button)
        removeButton.setOnClickListener {
            removeTag(tag)
        }

        return tagView
    }
}

class DestinationPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = 2 // 총 3개의 페이지

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> DestinationFirstFragment()  // 주제 선택
            1 -> DestinationSecondFragment() // 동네 선택
            else -> throw IllegalStateException("Invalid position: $position")
        }
    }
}
