package org.wordpress.android.ui.bloggingprompts.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.wordpress.android.databinding.BloggingPromptsListFragmentBinding
import org.wordpress.android.ui.ViewPagerFragment
import org.wordpress.android.util.extensions.setVisible

class BloggingPromptsListFragment : ViewPagerFragment() {

    private lateinit var binding: BloggingPromptsListFragmentBinding
    private lateinit var promptsListAdapter: BloggingPromptsListAdapter

    private val parentViewModel: BloggingPromptsListParentViewModel by activityViewModels()
    private val viewModel: BloggingPromptsListViewModel by viewModels()

    override fun getScrollableViewForUniqueIdProvision(): View = binding.promptsList

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = BloggingPromptsListFragmentBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val section = arguments?.getSerializable(LIST_TYPE) as? PromptSection
        val site = parentViewModel.getSite()
        initializeViews()
        setupObservers()

        viewModel.onOpen(site, section)
    }

    private fun initializeViews() {
        with(binding.promptsList) {
            layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
            addItemDecoration(DividerItemDecoration(context, LinearLayout.VERTICAL))
            promptsListAdapter = BloggingPromptsListAdapter()
            adapter = promptsListAdapter
        }
    }

    private fun setupObservers(){
        viewModel.contentViewState.observe(viewLifecycleOwner) {
            setContentViewVisibility(
                    it.isVisible,
                    it.list
            )
        }
        viewModel.errorViewState.observe(viewLifecycleOwner) {
            setErrorViewVisibility(
                    it.isVisible,
                    it.imageResId,
                    it.titleTextResId,
                    it.subtitleTextResId,
                    it.buttonTextResId
            )
        }
    }

    private fun setContentViewVisibility(
        isVisible: Boolean = false,
        list: List<BloggingPromptsListItem> = emptyList()
    ) {
        promptsListAdapter.update(list)
        binding.promptsList.setVisible(isVisible)
    }

    private fun setErrorViewVisibility(
        isVisible: Boolean = false,
        @DrawableRes imageResId: Int? = null,
        @StringRes titleTextResId: Int? = null,
        @StringRes subtitleTextResId: Int? = null,
        @StringRes buttonTextResId: Int? = null
    ) {
        with(binding.actionableEmptyView) {
            setVisible(isVisible)
            with(image) {
                imageResId?.let {
                    setVisible(true)
                    setImageResource(it)
                } ?: setVisible(false)
            }
            with(title) {
                titleTextResId?.let {
                    setVisible(true)
                    setText(it)
                } ?: setVisible(false)
            }
            with(subtitle) {
                subtitleTextResId?.let {
                    setVisible(true)
                    setText(it)
                } ?: setVisible(false)
            }
            with(button) {
                buttonTextResId?.let {
                    setVisible(true)
                    setText(it)
                    setOnClickListener {
                        viewModel.onClickButtonRetry()
                    }
                } ?: setVisible(false)
            }
        }
    }

    companion object {
        const val LIST_TYPE = "type_key"

        fun newInstance(section: PromptSection): BloggingPromptsListFragment {
            val fragment = BloggingPromptsListFragment()
            val bundle = Bundle()
            bundle.putSerializable(LIST_TYPE, section)
            fragment.arguments = bundle
            return fragment
        }
    }
}
