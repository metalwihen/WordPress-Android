package org.wordpress.android.ui.bloggingprompts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.wordpress.android.R
import org.wordpress.android.databinding.BloggingPromptsListFragmentBinding
import org.wordpress.android.ui.ViewPagerFragment
import org.wordpress.android.util.extensions.exhaustive
import org.wordpress.android.util.extensions.setVisible
import java.util.concurrent.TimeUnit

class BloggingPromptsListFragment : ViewPagerFragment() {
    private lateinit var binding: BloggingPromptsListFragmentBinding
    private lateinit var promptsListAdapter: BloggingPromptsListAdapter

    override fun getScrollableViewForUniqueIdProvision(): View = binding.promptsList

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = BloggingPromptsListFragmentBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val section = arguments?.getSerializable(LIST_TYPE) as? PromptSection
                ?: PromptSection.ALL
        initializeViews()

        // DUMMY LOGIC
        showLoading()
        binding.root.postDelayed(Runnable {
            if (!isAdded) return@Runnable
            when (section) {
                PromptSection.ALL -> showContent(
                        listOf(
                                generateDummyData(),
                                generateDummyData(),
                                generateDummyData(),
                                generateDummyData(),
                                generateDummyData(),
                                generateDummyData(),
                                generateDummyData(),
                                generateDummyData(),
                                generateDummyData()
                        )
                )
                PromptSection.NOT_ANSWERED -> showError()
                PromptSection.ANSWERED -> showNoConnection()
            }.exhaustive
        }, TimeUnit.SECONDS.toMillis(2))
    }

    private fun initializeViews() {
        with(binding) {
            promptsList.layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
            promptsList.addItemDecoration(DividerItemDecoration(context, LinearLayout.VERTICAL))
            promptsListAdapter = BloggingPromptsListAdapter()
            promptsList.adapter = promptsListAdapter
        }
    }

    private fun showContent(list: List<BloggingPromptsListItem>) {
        with(binding) {
            promptsListAdapter.update(list)
            promptsList.setVisible(true)
            actionableEmptyView.setVisible(false)
        }
    }

    private fun showEmpty() {
        with(binding) {
            promptsList.setVisible(false)
            with(actionableEmptyView) {
                setVisible(true)
                image.apply {
                    setVisible(true)
                    setImageResource(R.drawable.img_illustration_empty_results_216dp)
                }
                title.apply {
                    setVisible(true)
                    setText(R.string.blogging_prompts_state_empty_title)
                }
                subtitle.setVisible(false)
            }
        }
    }

    private fun showError() {
        with(binding) {
            promptsList.setVisible(false)
            with(actionableEmptyView) {
                setVisible(true)
                image.apply {
                    setVisible(true)
                    setImageResource(R.drawable.img_illustration_empty_results_216dp)
                }
                title.apply {
                    setVisible(true)
                    setText(R.string.blogging_prompts_state_error_title)
                }
                subtitle.apply {
                    setVisible(true)
                    setText(R.string.blogging_prompts_state_error_subtitle)
                }
            }
        }
    }

    private fun showNoConnection() {
        with(binding) {
            promptsList.setVisible(false)
            with(actionableEmptyView) {
                setVisible(true)
                image.apply {
                    setVisible(true)
                    setImageResource(R.drawable.img_illustration_cloud_off_152dp)
                }
                title.apply {
                    setVisible(true)
                    setText(R.string.blogging_prompts_state_no_connection_title)
                }
                subtitle.apply {
                    setVisible(true)
                    setText(R.string.blogging_prompts_state_no_connection_subtitle)
                }
            }
        }
    }

    fun showLoading() {
        with(binding) {
            promptsList.setVisible(false)
            with(actionableEmptyView) {
                setVisible(true)
                image.setVisible(false)
                title.apply {
                    setVisible(true)
                    setText(R.string.blogging_prompts_state_loading_title)
                }
                subtitle.setVisible(false)
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
