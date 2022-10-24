package org.wordpress.android.ui.bloggingprompts.list

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import org.wordpress.android.R
import org.wordpress.android.fluxc.model.SiteModel
import org.wordpress.android.modules.UI_THREAD
import org.wordpress.android.util.NetworkUtilsWrapper
import org.wordpress.android.viewmodel.ScopedViewModel
import javax.inject.Inject
import javax.inject.Named

class BloggingPromptsListViewModel @Inject constructor(
    @Named(UI_THREAD) private val mainDispatcher: CoroutineDispatcher,
    private val fetchUsecase: BloggingPromptsListFetchStoredUsecase,
    private val networkUtilsWrapper: NetworkUtilsWrapper,
    private val mapper: BloggingPromptsListItemMapper
) : ScopedViewModel(mainDispatcher) {
    private val _uiState = MutableLiveData<UiState>()

    private lateinit var site: SiteModel
    private lateinit var selected: PromptSection

    val uiState: LiveData<UiState> = _uiState

    fun onOpen(siteModel: SiteModel?, section: PromptSection?) {
        if (siteModel == null || section == null) {
            _uiState.postValue(UiState(ContentViewState.Hidden, ErrorViewState.Error))
        } else {
            this.site = siteModel
            this.selected = section
            load(siteModel, section)
        }
    }

    fun onClickButtonRetry() {
        if (!::site.isInitialized || !::selected.isInitialized) {
            _uiState.postValue(UiState(ContentViewState.Hidden, ErrorViewState.Error))
        } else {
            load(site, selected)
        }
    }

    private fun load(site: SiteModel, selected: PromptSection) {
        _uiState.postValue(UiState(ContentViewState.Hidden, ErrorViewState.Loading))

        launch {
            if (!networkUtilsWrapper.isNetworkAvailable()) {
                _uiState.postValue(UiState(ContentViewState.Hidden, ErrorViewState.NoConnection))
                return@launch
            }

            fetchUsecase.fetch(site, selected)
                    .map { list -> list?.map { item -> mapper.toUiModel(item) } }
                    .catch { emit(null) }
                    .collect { list ->
                        when {
                            list == null -> {
                                _uiState.postValue(UiState(ContentViewState.Hidden, ErrorViewState.Error))
                            }
                            list.isEmpty() -> {
                                _uiState.postValue(UiState(ContentViewState.Hidden, ErrorViewState.Empty))
                            }
                            else -> {
                                _uiState.postValue(UiState(ContentViewState.Content(list), ErrorViewState.Hidden))
                            }
                        }
                    }
        }
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
