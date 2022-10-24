package org.wordpress.android.ui.bloggingprompts.list

import androidx.lifecycle.SavedStateHandle
import org.wordpress.android.WordPress
import org.wordpress.android.fluxc.model.SiteModel
import javax.inject.Inject

class BloggingPromptsListSiteProvider @Inject constructor(
    private val savedStateHandle: SavedStateHandle
) {
    fun setSite(site: SiteModel) = savedStateHandle.set(WordPress.SITE, site)

    fun getSite(): SiteModel? = savedStateHandle.get<SiteModel>(WordPress.SITE)
}
