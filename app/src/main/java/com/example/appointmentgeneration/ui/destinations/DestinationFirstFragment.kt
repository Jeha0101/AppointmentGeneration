package com.example.appointmentgeneration.ui.destinations

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.GridLayout
import android.widget.TextView
import androidx.core.view.setMargins
import androidx.fragment.app.Fragment
import com.example.appointmentgeneration.R

class DestinationFirstFragment : Fragment() {

    private lateinit var parentFragment: DestinationSelectionFragment

    // 기본 태그 목록
    private val defaultTags = listOf(
        "포토부스", "방탈출 카페", "노래방", "식당", "만화 카페",
        "영화관", "스티커 사진관", "카페", "사진관", "떡볶이", "애견카페"
    )
    private var filteredTags = defaultTags.toMutableList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_destination_first, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 부모 프래그먼트 확인 후 초기화
        val parent = requireParentFragment()
        if (parent is DestinationSelectionFragment) {
            parentFragment = parent
        } else {
            throw IllegalStateException("Parent fragment is not DestinationSelectionFragment")
        }

        // UI 설정
        setupUI(view)
    }

    private fun setupUI(view: View) {
        val searchEditText = view.findViewById<EditText>(R.id.search_edit_text)
        val tagGridLayout = view.findViewById<GridLayout>(R.id.tag_grid_layout)
        val customTagInput = view.findViewById<EditText>(R.id.custom_tag_input)
        val addCustomTagButton = view.findViewById<Button>(R.id.add_custom_tag_button)

        // 태그 초기화
        updateTagList(tagGridLayout)

        // 검색 기능
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val query = s.toString()
                filterTags(query)
                updateTagList(tagGridLayout)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // 직접 추가 기능
        addCustomTagButton.setOnClickListener {
            val customTag = customTagInput.text.toString().trim()
            if (customTag.isNotEmpty()) {
                parentFragment.addTag(customTag) // AppBar에 추가
                customTagInput.text.clear()
            }
        }
    }

    // 태그 목록 필터링
    private fun filterTags(query: String) {
        filteredTags = if (query.isEmpty()) {
            defaultTags.toMutableList()
        } else {
            defaultTags.filter { it.contains(query, ignoreCase = true) }.toMutableList()
        }
    }

    // 태그 리스트 업데이트
    private fun updateTagList(gridLayout: GridLayout) {
        gridLayout.removeAllViews()

        for (tag in filteredTags) {
            val tagView = createTagView(tag)
            gridLayout.addView(tagView)
        }
    }

    // 태그 뷰 생성
    private fun createTagView(tag: String): View {
        val textView = TextView(requireContext()).apply {
            text = tag
            textSize = 20f
            setTextColor(resources.getColor(android.R.color.white, null))
            setPadding(24, 16, 24, 16)
            setBackgroundResource(R.drawable.tag_background) // 태그 디자인
            setOnClickListener {
                parentFragment.addTag(tag) // AppBar에 추가
            }
        }

        val layoutParams = GridLayout.LayoutParams().apply {
            setMargins(8)
            width = GridLayout.LayoutParams.WRAP_CONTENT
            height = GridLayout.LayoutParams.WRAP_CONTENT
        }
        textView.layoutParams = layoutParams
        return textView
    }
}
