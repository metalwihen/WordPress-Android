package org.wordpress.android.ui.bloggingprompts.list

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
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
    private val _uiState = MutableLiveData<UiState>()
    val uiState: LiveData<UiState> = _uiState

    fun onOpen(site: SiteModel?, section: PromptSection?) {
        if (site == null || section == null) {
            _uiState.postValue(UiState(ContentViewState.Hidden, ErrorViewState.Error))
        } else {
            // DUMMY LOGIC
            when (section) {
                ALL -> {
                    _uiState.postValue(UiState(ContentViewState.Hidden, ErrorViewState.NoConnection))
                }
                NOT_ANSWERED -> {
                    _uiState.postValue(UiState(ContentViewState.Hidden, ErrorViewState.Empty))
                }
                ANSWERED -> {
                    _uiState.postValue(UiState(ContentViewState.Hidden, ErrorViewState.Loading))
                }
            }.exhaustive
        }
    }

    fun onClickButtonRetry() {
        _uiState.postValue(
                UiState(
                        ContentViewState.Content(
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
                        ),
                        ErrorViewState.Hidden
                )
        )
    }
}

data class UiState(
    val contentViewState: ContentViewState,
    val errorViewState: ErrorViewState
)

sealed class ErrorViewState(
    val isVisible: Boolean,
    @DrawableRes val imageResId: Int? = null,
    @StringRes val titleTextResId: Int? = null,
    @StringRes val subtitleTextResId: Int? = null,
    @StringRes val buttonTextResId: Int? = null
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
            buttonTextResId = R.string.blogging_prompts_state_error_retry
    )

    object NoConnection : ErrorViewState(
            isVisible = true,
            imageResId = R.drawable.img_illustration_cloud_off_152dp,
            titleTextResId = R.string.blogging_prompts_state_no_connection_title,
            subtitleTextResId = R.string.blogging_prompts_state_no_connection_subtitle,
            buttonTextResId = R.string.blogging_prompts_state_error_retry
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
