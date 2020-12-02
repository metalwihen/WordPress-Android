package org.wordpress.android.ui.mysite

import android.text.TextUtils
import org.wordpress.android.R
import org.wordpress.android.fluxc.model.SiteModel
import org.wordpress.android.fluxc.store.AccountStore
import org.wordpress.android.ui.main.MySiteFragment
import org.wordpress.android.ui.mysite.MySiteItem.ListItem
import org.wordpress.android.ui.plugins.PluginUtilsWrapper
import org.wordpress.android.ui.themes.ThemeBrowserUtils
import org.wordpress.android.ui.utils.ListItemInteraction
import org.wordpress.android.ui.utils.UiString.UiStringRes
import org.wordpress.android.ui.utils.UiString.UiStringText
import org.wordpress.android.util.DateTimeUtils
import org.wordpress.android.util.ScanFeatureConfig
import org.wordpress.android.util.SiteUtilsWrapper
import java.util.GregorianCalendar
import java.util.TimeZone
import javax.inject.Inject

class SiteListItemBuilder
@Inject constructor(
    private val accountStore: AccountStore,
    private val pluginUtilsWrapper: PluginUtilsWrapper,
    private val siteUtilsWrapper: SiteUtilsWrapper,
    private val scanFeatureConfig: ScanFeatureConfig,
    private val themeBrowserUtils: ThemeBrowserUtils
) {
    fun buildActivityLogItemIfAvailable(site: SiteModel, onClick: ListItemInteraction): ListItem? {
        val isWpComOrJetpack = siteUtilsWrapper.isAccessedViaWPComRest(
                site
        ) || site.isJetpackConnected
        return if (site.hasCapabilityManageOptions && isWpComOrJetpack && !site.isWpForTeamsSite) {
            ListItem(
                    R.drawable.ic_history_alt_white_24dp,
                    UiStringRes(R.string.activity),
                    onClick = onClick
            )
        } else null
    }

    fun buildScanItemIfAvailable(onClick: ListItemInteraction): ListItem? {
        return if (scanFeatureConfig.isEnabled()) {
            ListItem(
                    R.drawable.ic_scan_alt_white_24dp,
                    UiStringRes(R.string.scan),
                    onClick = onClick
            )
        } else null
    }

    fun buildJetpackItemIfAvailable(site: SiteModel, onClick: ListItemInteraction): ListItem? {
        val jetpackSettingsVisible = site.isJetpackConnected && // jetpack is installed and connected
                !site.isWPComAtomic &&
                siteUtilsWrapper.isAccessedViaWPComRest(site) && // is using .com login
                site.hasCapabilityManageOptions // has permissions to manage the site
        return if (jetpackSettingsVisible) {
            ListItem(
                    R.drawable.ic_cog_white_24dp,
                    UiStringRes(R.string.my_site_btn_jetpack_settings),
                    onClick = onClick
            )
        } else null
    }

    fun buildPlanItemIfAvailable(site: SiteModel, onClick: ListItemInteraction): ListItem? {
        val planShortName = site.planShortName
        return if (!TextUtils.isEmpty(planShortName) &&
                site.hasCapabilityManageOptions &&
                !site.isWpForTeamsSite &&
                (site.isWPCom || site.isAutomatedTransfer)) {
            ListItem(
                    R.drawable.ic_plans_white_24dp,
                    UiStringRes(R.string.plan),
                    secondaryText = UiStringText(planShortName),
                    onClick = onClick
            )
        } else null
    }

    fun buildPagesItemIfAvailable(site: SiteModel, onClick: ListItemInteraction): ListItem? {
        return if (site.isSelfHostedAdmin || site.hasCapabilityEditPages) {
            ListItem(
                    R.drawable.ic_pages_white_24dp,
                    UiStringRes(R.string.my_site_btn_site_pages),
                    onClick = onClick
            )
        } else null
    }

    fun buildAdminItemIfAvailable(site: SiteModel, onClick: ListItemInteraction): ListItem? {
        return if (shouldShowWPAdmin(site)) {
            ListItem(
                    R.drawable.ic_my_sites_white_24dp,
                    UiStringRes(R.string.my_site_btn_view_admin),
                    secondaryIcon = R.drawable.ic_external_white_24dp,
                    onClick = onClick
            )
        } else null
    }

    fun buildPeopleItemIfAvailable(site: SiteModel, onClick: ListItemInteraction): ListItem? {
        return if (site.hasCapabilityListUsers) {
            ListItem(
                    R.drawable.ic_user_white_24dp,
                    UiStringRes(R.string.people),
                    onClick = onClick
            )
        } else null
    }

    fun buildPluginItemIfAvailable(site: SiteModel, onClick: ListItemInteraction): ListItem? {
        return if (pluginUtilsWrapper.isPluginFeatureAvailable(site)) {
            ListItem(
                    R.drawable.ic_plugins_white_24dp,
                    UiStringRes(R.string.my_site_btn_plugins),
                    onClick = onClick
            )
        } else null
    }

    fun buildShareItemIfAvailable(site: SiteModel, onClick: ListItemInteraction): ListItem? {
        return if (siteUtilsWrapper.isAccessedViaWPComRest(site)) {
            ListItem(
                    R.drawable.ic_share_white_24dp,
                    UiStringRes(R.string.my_site_btn_sharing),
                    onClick = onClick
            )
        } else null
    }

    fun buildSiteSettingsItemIfAvailable(site: SiteModel, onClick: ListItemInteraction): ListItem? {
        return if (site.hasCapabilityManageOptions || !siteUtilsWrapper.isAccessedViaWPComRest(site)) {
            ListItem(
                    R.drawable.ic_cog_white_24dp,
                    UiStringRes(R.string.my_site_btn_site_settings),
                    onClick = onClick
            )
        } else null
    }

    private fun shouldShowWPAdmin(site: SiteModel): Boolean {
        return if (!site.isWPCom) {
            true
        } else {
            val dateCreated = DateTimeUtils.dateFromIso8601(
                    accountStore.account
                            .date
            )
            val calendar = GregorianCalendar(HIDE_WP_ADMIN_YEAR, HIDE_WP_ADMIN_MONTH, HIDE_WP_ADMIN_DAY)
            calendar.timeZone = TimeZone.getTimeZone(MySiteFragment.HIDE_WP_ADMIN_GMT_TIME_ZONE)
            dateCreated == null || dateCreated.before(calendar.time)
        }
    }

    fun buildThemesItemIfAvailable(site: SiteModel, onClick: ListItemInteraction): MySiteItem? {
        return if (themeBrowserUtils.isAccessible(site)) {
            ListItem(
                    R.drawable.ic_themes_white_24dp,
                    UiStringRes(R.string.themes),
                    onClick = onClick
            )
        } else null
    }

    companion object {
        const val HIDE_WP_ADMIN_YEAR = 2015
        const val HIDE_WP_ADMIN_MONTH = 9
        const val HIDE_WP_ADMIN_DAY = 7
    }
}