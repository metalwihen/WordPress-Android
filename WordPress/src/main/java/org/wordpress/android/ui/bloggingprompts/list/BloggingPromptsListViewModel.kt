package org.wordpress.android.ui.bloggingprompts.list

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import org.wordpress.android.R
import org.wordpress.android.fluxc.model.SiteModel
import org.wordpress.android.ui.bloggingprompts.list.PromptSection.ALL
import org.wordpress.android.ui.bloggingprompts.list.PromptSection.ANSWERED
import org.wordpress.android.ui.bloggingprompts.list.PromptSection.NOT_ANSWERED
import org.wordpress.android.util.extensions.exhaustive
import javax.inject.Inject

@HiltViewModel
class BloggingPromptsListViewModel @Inject constructor() : ViewModel() {
    private val _contentViewState = MutableLiveData<ContentViewState>()
    val contentViewState = _contentViewState

    private val _errorViewState = MutableLiveData<ErrorViewState>()
    val errorViewState = _errorViewState

    fun onOpen(site: SiteModel?, section: PromptSection?) {
        if (site == null || section == null) {
            contentViewState.postValue(ContentViewState.Hidden)
            errorViewState.postValue(ErrorViewState.Error)
        } else {
            // DUMMY LOGIC
            when (section) {
                ALL -> {
                    contentViewState.postValue(ContentViewState.Hidden)
                    errorViewState.postValue(ErrorViewState.NoConnection)
                }
                NOT_ANSWERED -> {
                    contentViewState.postValue(ContentViewState.Hidden)
                    errorViewState.postValue(ErrorViewState.Empty)
                }
                ANSWERED -> {
                    contentViewState.postValue(ContentViewState.Hidden)
                    errorViewState.postValue(ErrorViewState.Loading)
                }
            }.exhaustive
        }
    }
}

sealed class ErrorViewState(
    val isVisible: Boolean,
    @DrawableRes val imageResId: Int? = null,
    @StringRes val titleTextResId: Int? = null,
    @StringRes val subtitleTextResId: Int? = null
) {
    object Hidden : ErrorViewState(isVisible = false)

    object Loading : ErrorViewState(
            isVisible = true,
            titleTextResId = R.string.blogging_prompts_state_loading_title
    )

    object Empty : ErrorViewState(
            isVisible = true,
            imageResId = R.drawable.img_illustration_empty_results_216dp,
            titleTextResId = R.string.blogging_prompts_state_empty_title,
    )

    object Error : ErrorViewState(
            isVisible = true,
            imageResId = R.drawable.img_illustration_empty_results_216dp,
            titleTextResId = R.string.blogging_prompts_state_error_title,
            subtitleTextResId = R.string.blogging_prompts_state_error_subtitle,
    )

    object NoConnection : ErrorViewState(
            isVisible = true,
            imageResId = R.drawable.img_illustration_cloud_off_152dp,
            titleTextResId = R.string.blogging_prompts_state_no_connection_title,
            subtitleTextResId = R.string.blogging_prompts_state_no_connection_subtitle,
    )
}

sealed class ContentViewState(
    val isVisible: Boolean,
    val list: List<BloggingPromptsListItem>
) {
    object Hidden : ContentViewState(isVisible = false, emptyList())
    data class Content(val listItems: List<BloggingPromptsListItem>) : ContentViewState(
            isVisible = true, list = listItems
    )
}
