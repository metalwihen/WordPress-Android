package org.wordpress.android.ui.accounts.login

import android.content.Context
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.lifecycle.viewmodel.compose.viewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.isActive
import org.wordpress.android.ui.accounts.login.components.ColumnWithFrostedGlassBackground
import org.wordpress.android.ui.accounts.login.components.JetpackLogo
import org.wordpress.android.ui.accounts.login.components.LoopingTextWithBackground
import org.wordpress.android.ui.accounts.login.components.PrimaryButton
import org.wordpress.android.ui.accounts.login.components.SecondaryButton
import org.wordpress.android.ui.accounts.login.components.TopLinearGradient
import org.wordpress.android.ui.compose.theme.AppTheme

val LocalPosition = compositionLocalOf { 0f }

@AndroidEntryPoint
class LoginPrologueRevampedFragment : Fragment() {
    private lateinit var loginPrologueListener: LoginPrologueListener

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = ComposeView(requireContext()).apply {
        setContent {
            AppTheme {
                PositionProvider {
                    LoginScreenRevamped(
                            onWpComLoginClicked = loginPrologueListener::showEmailLoginScreen,
                            onSiteAddressLoginClicked = loginPrologueListener::loginViaSiteAddress,
                    )
                }
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        check(context is LoginPrologueListener) { "$context must implement LoginPrologueListener" }
        loginPrologueListener = context
    }

    override fun onResume() {
        super.onResume()
        requireActivity().window.addFlags(FLAG_LAYOUT_NO_LIMITS)
    }

    override fun onPause() {
        super.onPause()
        requireActivity().window.clearFlags(FLAG_LAYOUT_NO_LIMITS)
    }

    companion object {
        const val TAG = "login_prologue_revamped_fragment_tag"
    }
}

/**
 * This composable launches an effect to continuously update the view model by providing the elapsed
 * time between frames. Velocity and position are recalculated for each frame, with the resulting
 * position provided here to be consumed by nested children composables.
 */
@Composable
private fun PositionProvider(
    viewModel: LoginPrologueRevampedViewModel = viewModel(),
    content: @Composable () -> Unit
) {
    val position = viewModel.positionData.observeAsState(0f)
    CompositionLocalProvider(LocalPosition provides position.value) {
        LaunchedEffect(Unit) {
            var lastFrameNanos: Long? = null
            while (isActive) {
                val currentFrameNanos = withFrameNanos { it }
                // Calculate elapsed time (in seconds) since the last frame
                val elapsed = (currentFrameNanos - (lastFrameNanos ?: currentFrameNanos)) / 1e9.toFloat()
                // Update viewModel for frame
                viewModel.updateForFrame(elapsed)
                // Update frame timestamp reference
                lastFrameNanos = currentFrameNanos
            }
        }

        content()
    }
}

@Composable
private fun LoginScreenRevamped(
    onWpComLoginClicked: () -> Unit,
    onSiteAddressLoginClicked: () -> Unit,
) {
    Box {
        LoopingTextWithBackground()
        TopLinearGradient()
        JetpackLogo(
                modifier = Modifier
                        .padding(top = 60.dp)
                        .size(60.dp)
                        .align(Alignment.TopCenter)
        )
        ColumnWithFrostedGlassBackground(
                background = { modifier, textModifier -> LoopingTextWithBackground(modifier, textModifier) }
        ) {
            PrimaryButton(onClick = onWpComLoginClicked)
            SecondaryButton(onClick = onSiteAddressLoginClicked)
        }
    }
}

@Preview(showBackground = true, device = Devices.PIXEL_4_XL)
@Preview(showBackground = true, device = Devices.PIXEL_4_XL, uiMode = UI_MODE_NIGHT_YES)
@Composable
fun PreviewLoginScreenRevamped() {
    AppTheme {
        LoginScreenRevamped(
                onWpComLoginClicked = {},
                onSiteAddressLoginClicked = {}
        )
    }
}
