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
        setupToolbar()
        setupViewPagerWithTabs()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        sendSelectedTags()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            sendSelectedTags()
            findNavController().navigateUp()
        }
    }

    private fun sendSelectedTags() {
        parentFragmentManager.setFragmentResult(
            "selectedTagsKey",
            Bundle().apply { putStringArrayList("selectedTags", ArrayList(selectedTags)) }
        )
    }

    private fun setupViewPagerWithTabs() {
        binding.viewPager.adapter = DestinationPagerAdapter(this)
        TabLayoutMediator(binding.destinationsTabLayout, binding.viewPager) { tab, position ->
            tab.text = if (position == 0) "주제 선택" else "동네 선택"
        }.attach()
    }

    fun addTag(tag: String) {
        if (tag !in selectedTags) {
            selectedTags.add(tag)
            updateTags()
        }
    }

    private fun removeTag(tag: String) {
        selectedTags.remove(tag)
        updateTags()
    }

    private fun updateTags() {
        val constraintLayout = binding.flowTags.parent as ConstraintLayout
        clearOldTags(constraintLayout)
        val referencedIds = addNewTags(constraintLayout)
        binding.flowTags.referencedIds = referencedIds.toIntArray()
    }

    private fun clearOldTags(constraintLayout: ConstraintLayout) {
        selectedTags.indices.forEach { index ->
            constraintLayout.findViewWithTag<View>("tag_$index")?.let {
                constraintLayout.removeView(it)
            }
        }
    }

    private fun addNewTags(constraintLayout: ConstraintLayout): List<Int> {
        return selectedTags.mapIndexed { index, tag ->
            val tagView = createTagView(tag).apply {
                id = View.generateViewId()
                setTag("tag_$index")
            }
            constraintLayout.addView(tagView)
            tagView.id
        }
    }

    private fun createTagView(tag: String): View {
        return (layoutInflater.inflate(R.layout.tag_item, null, false) as LinearLayout).apply {
            findViewById<TextView>(R.id.tag_text).text = tag
            findViewById<View>(R.id.tag_remove_button).setOnClickListener {
                removeTag(tag)
            }
        }
    }
}

class DestinationPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> DestinationFirstFragment()
            1 -> DestinationSecondFragment()
            else -> throw IllegalStateException("Invalid position: $position")
        }
    }
}
