package org.wordpress.android.ui.bloggingprompts.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.google.android.material.tabs.TabLayout.Tab
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import org.wordpress.android.R
import org.wordpress.android.databinding.BloggingPromptsParentFragmentBinding

@AndroidEntryPoint
class BloggingPromptsListParentFragment : Fragment() {
    private lateinit var binding: BloggingPromptsParentFragmentBinding

    private val viewModel: BloggingPromptsListParentViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = BloggingPromptsParentFragmentBinding.inflate(inflater)
        setupToolbar(binding)
        setupTabLayout(binding)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val currentTabPosition = binding.tabLayout.selectedTabPosition
        viewModel.onOpen(promptsSections[currentTabPosition])
    }

    private fun setupToolbar(binding: BloggingPromptsParentFragmentBinding) {
        with(binding) {
            with(activity as AppCompatActivity) {
                setSupportActionBar(toolbar)

                title = getString(R.string.blogging_prompts_title)
                supportActionBar?.setDisplayShowTitleEnabled(true)
                supportActionBar?.setDisplayHomeAsUpEnabled(true)
            }
        }
    }

    private fun setupTabLayout(binding: BloggingPromptsParentFragmentBinding) {
        with(binding) {
            val adapter = BloggingPromptsListPagerAdapter(this@BloggingPromptsListParentFragment)
            promptPager.adapter = adapter
            TabLayoutMediator(tabLayout, promptPager) { tab, position ->
                tab.text = adapter.getTabTitle(position)
            }.attach()
            tabLayout.addOnTabSelectedListener(SelectedTabListener(viewModel))
        }
    }
}

private class BloggingPromptsListPagerAdapter(
    private val parent: BloggingPromptsListParentFragment
) : FragmentStateAdapter(parent) {
    override fun getItemCount(): Int = promptsSections.size

    override fun createFragment(position: Int): Fragment {
        return BloggingPromptsListFragment.newInstance(promptsSections[position])
    }

    fun getTabTitle(position: Int): CharSequence {
        return parent.context?.getString(promptsSections[position].titleRes).orEmpty()
    }
}

private class SelectedTabListener(val viewModel: BloggingPromptsListParentViewModel) : OnTabSelectedListener {
    override fun onTabReselected(tab: Tab?) = Unit

    override fun onTabUnselected(tab: Tab?) = Unit

    override fun onTabSelected(tab: Tab) {
        viewModel.onSectionSelected(promptsSections[tab.position])
    }
}
