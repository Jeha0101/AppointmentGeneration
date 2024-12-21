package com.example.appointmentgeneration.ui.destinations

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.view.setMargins
import androidx.fragment.app.Fragment
import com.example.appointmentgeneration.R

class DestinationSecondFragment : Fragment() {

    private lateinit var parentFragment: DestinationSelectionFragment
    private val defaultTags = listOf(
        "숙명여대", "건대 입구", "마포구", "홍대", "건대",
        "강남", "성수", "영등포구", "종로구", "익선동",
        "경복궁", "신촌", "부천"
    )
    private var filteredTags = defaultTags.toMutableList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_destination_second, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeParentFragment()
        setupUI(view)
    }

    private fun initializeParentFragment() {
        val parent = requireParentFragment()
        if (parent is DestinationSelectionFragment) {
            parentFragment = parent
        } else {
            throw IllegalStateException("Parent fragment is not DestinationSelectionFragment")
        }
    }

    private fun setupUI(view: View) {
        val searchEditText = view.findViewById<EditText>(R.id.search_edit_text)
        val tagGridLayout = view.findViewById<GridLayout>(R.id.tag_grid_layout)
        val customTagInput = view.findViewById<EditText>(R.id.custom_tag_input)
        val addCustomTagButton = view.findViewById<Button>(R.id.add_custom_tag_button)

        updateTagList(tagGridLayout)
        setupSearchFilter(searchEditText, tagGridLayout)
        setupCustomTagAddition(customTagInput, addCustomTagButton)
    }

    private fun setupSearchFilter(searchEditText: EditText, tagGridLayout: GridLayout) {
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                filterTags(s.toString())
                updateTagList(tagGridLayout)
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun setupCustomTagAddition(customTagInput: EditText, addCustomTagButton: Button) {
        addCustomTagButton.setOnClickListener {
            val customTag = customTagInput.text.toString().trim()
            if (customTag.isNotEmpty()) {
                parentFragment.addTag(customTag)
                customTagInput.text.clear()
            }
        }
    }

    private fun filterTags(query: String) {
        filteredTags = if (query.isEmpty()) {
            defaultTags.toMutableList()
        } else {
            defaultTags.filter { it.contains(query, ignoreCase = true) }.toMutableList()
        }
    }

    private fun updateTagList(gridLayout: GridLayout) {
        gridLayout.removeAllViews()
        filteredTags.forEach { tag ->
            gridLayout.addView(createTagView(tag))
        }
    }

    private fun createTagView(tag: String): View {
        return TextView(requireContext()).apply {
            text = tag
            textSize = 16f
            setTextColor(resources.getColor(android.R.color.white, null))
            setPadding(24, 16, 24, 16)
            setBackgroundResource(R.drawable.tag_background)
            layoutParams = createTagLayoutParams()
            setOnClickListener { parentFragment.addTag(tag) }
        }
    }

    private fun createTagLayoutParams(): GridLayout.LayoutParams {
        return GridLayout.LayoutParams().apply {
            setMargins(8)
            width = GridLayout.LayoutParams.WRAP_CONTENT
            height = GridLayout.LayoutParams.WRAP_CONTENT
        }
    }
}
