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
import dagger.hilt.android.AndroidEntryPoint
import org.wordpress.android.databinding.BloggingPromptsListFragmentBinding
import org.wordpress.android.ui.ViewPagerFragment
import org.wordpress.android.ui.utils.UiHelpers
import org.wordpress.android.util.extensions.setVisible
import javax.inject.Inject

@AndroidEntryPoint
class BloggingPromptsListFragment : ViewPagerFragment() {

    @Inject lateinit var uiHelpers: UiHelpers

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

    private fun setupObservers() {
        viewModel.uiState.observe(viewLifecycleOwner) { uiState ->
            uiState.contentViewState.let {
                setContentViewVisibility(
                        it.isVisible,
                        it.list
                )
            }
            uiState.errorViewState.let {
                setErrorViewVisibility(
                        it.isVisible,
                        it.imageResId,
                        it.titleTextResId,
                        it.subtitleTextResId,
                        it.buttonTextResId
                )
            }
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
            uiHelpers.setImageOrHide(image, imageResId)
            uiHelpers.setTextOrHide(title, titleTextResId)
            uiHelpers.setTextOrHide(subtitle, subtitleTextResId)
            uiHelpers.setTextOrHide(button, buttonTextResId)
            button.setOnClickListener {
                viewModel.onClickButtonRetry()
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
