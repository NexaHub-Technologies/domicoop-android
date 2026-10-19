package com.nexahub.domicoop.ui.common

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.EventBusy
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Rule
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.nexahub.domicoop.R
import com.nexahub.domicoop.core.Currency
import com.nexahub.domicoop.theme.BrandGradientEnd
import com.nexahub.domicoop.theme.BrandGradientStart
import com.nexahub.domicoop.theme.Dimens
import com.nexahub.domicoop.theme.Inter
import com.nexahub.domicoop.theme.PlusJakartaSans
import com.nexahub.domicoop.theme.isDarkMode
import com.nexahub.domicoop.theme.primaryBright

// ============================================================================
// Screen & Layout Wrappers
// ============================================================================

@Composable
fun DomiScreen(
    modifier: Modifier = Modifier,
    scroll: Boolean = true,
    padded: Boolean = true,
    header: (@Composable () -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .navigationBarsPadding(),
    ) {
        header?.invoke()
        if (scroll) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .then(if (padded) Modifier.padding(horizontal = 20.dp) else Modifier),
                content = content,
            )
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .then(if (padded) Modifier.padding(horizontal = 20.dp) else Modifier),
                content = content,
            )
        }
    }
}

// ============================================================================
// Screen Header (mirrors components/common/ScreenHeader/index.tsx)
// ============================================================================

@Composable
fun ScreenHeader(
    title: String,
    onBack: (() -> Unit)? = null,
    rightAction: (@Composable () -> Unit)? = null,
    large: Boolean = false,
    modifier: Modifier = Modifier,
    /**
     * Detail routes sit at the nav-graph root with no ancestor Scaffold
     * padding, so the header must clear the status bar / display cutout
     * itself (edge-to-edge). Nested tab screens already sit inside the
     * outer tab Scaffold's content padding — pass false there to avoid
     * doubling the top offset.
     */
    applyTopInset: Boolean = true,
) {
    val showRow = !large || onBack != null || rightAction != null

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .then(if (applyTopInset) Modifier.statusBarsPadding().displayCutoutPadding() else Modifier)
            .padding(horizontal = 20.dp, vertical = 12.dp),
    ) {
        if (showRow) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (onBack != null) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceContainer)
                            .clickable(onClick = onBack),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(22.dp),
                        )
                    }
                } else if (!large && rightAction != null) {
                    Spacer(Modifier.size(40.dp))
                } else {
                    Spacer(Modifier.size(0.dp))
                }

                if (!large) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 12.dp),
                    )
                }

                if (rightAction != null) {
                    rightAction()
                } else if (!large && onBack != null) {
                    Spacer(Modifier.size(40.dp))
                } else {
                    Spacer(Modifier.size(0.dp))
                }
            }
        }

        if (large) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(top = if (showRow) 16.dp else 0.dp),
            )
        }
    }
}

// ============================================================================
// Back Button & Security Badge
// ============================================================================

@Composable
fun BackButton(
    onPress: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .clickable(onClick = onPress),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Back",
            tint = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.size(22.dp),
        )
    }
}

@Composable
fun SecurityBadge(
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(999.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        shadowElevation = 2.dp,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Icon(
                imageVector = Icons.Default.VerifiedUser,
                contentDescription = null,
                tint = primaryBright(),
                modifier = Modifier.size(14.dp),
            )
            Text(
                text = "SECURED VAULT",
                fontFamily = Inter,
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp,
                letterSpacing = 0.5.sp,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}

// ============================================================================
// Auth Hero Section & Form Card
// ============================================================================

@Composable
fun HeroSection(
    title: String,
    subtitle: String? = null,
    showLogo: Boolean = false,
    icon: ImageVector? = null,
    height: Dp = 280.dp,
    imageRes: Int? = null,
    modifier: Modifier = Modifier,
    children: (@Composable ColumnScope.() -> Unit)? = null,
) {
    val isDark = isDarkMode()
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height),
    ) {
        if (imageRes != null) {
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
        } else {
            Box(
                Modifier
                    .fillMaxSize()
                    .background(
                        Brush.linearGradient(
                            listOf(
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                                MaterialTheme.colorScheme.primaryContainer,
                            )
                        )
                    )
            )
        }

        // Dark overlay / brand tint
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                            MaterialTheme.colorScheme.background.copy(alpha = 0.95f),
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .padding(top = 40.dp, bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            if (showLogo) {
                Image(
                    painter = painterResource(
                        id = if (isDark) R.drawable.domicoop_logo else R.drawable.domicoop_logo_black
                    ),
                    contentDescription = "DOMICOOP",
                    modifier = Modifier
                        .size(120.dp)
                        .padding(bottom = 12.dp),
                    contentScale = ContentScale.Fit,
                )
            }

            if (icon != null) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surface,
                    shadowElevation = 4.dp,
                    modifier = Modifier.padding(bottom = 16.dp),
                ) {
                    Box(modifier = Modifier.padding(16.dp)) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = primaryBright(),
                            modifier = Modifier.size(36.dp),
                        )
                    }
                }
            }

            Text(
                text = title,
                fontFamily = PlusJakartaSans,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 26.sp,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                letterSpacing = (-0.5).sp,
            )

            if (!subtitle.isNullOrBlank()) {
                Spacer(Modifier.height(6.dp))
                Text(
                    text = subtitle,
                    fontFamily = Inter,
                    fontWeight = FontWeight.Normal,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp,
                    modifier = Modifier.padding(horizontal = 16.dp),
                )
            }

            children?.invoke(this)
        }
    }
}

@Composable
fun FormCard(
    modifier: Modifier = Modifier,
    overlap: Boolean = true,
    content: @Composable ColumnScope.() -> Unit,
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .then(if (overlap) Modifier.padding(top = 0.dp) else Modifier),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 2.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            content = content,
        )
    }
}

// ============================================================================
// Buttons (Primary, Tonal, Outlined, Ghost)
// ============================================================================

enum class ButtonVariant {
    PRIMARY, SECONDARY, TONAL, OUTLINED, GHOST;
    companion object {
        val Primary = PRIMARY
        val Secondary = SECONDARY
        val Tonal = TONAL
        val Outlined = OUTLINED
        val Ghost = GHOST
    }
}
enum class ButtonSize {
    SM, MD, LG;
    companion object {
        val Small = SM
        val Medium = MD
        val Large = LG
        val Sm = SM
        val Md = MD
        val Lg = LG
    }
}
typealias DomiButtonVariant = ButtonVariant
typealias DomiButtonSize = ButtonSize

@Composable
fun DomiButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    loading: Boolean = false,
    variant: ButtonVariant = ButtonVariant.PRIMARY,
    size: ButtonSize = ButtonSize.MD,
    tonal: Boolean = false,
    icon: ImageVector? = null,
    iconPositionRight: Boolean = true,
) {
    val actualVariant = if (tonal) ButtonVariant.TONAL else variant
    val height = when (size) {
        ButtonSize.SM -> 40.dp
        ButtonSize.MD -> 52.dp
        ButtonSize.LG -> 56.dp
    }
    val fontSize = when (size) {
        ButtonSize.SM -> 13.sp
        ButtonSize.MD -> 15.sp
        ButtonSize.LG -> 16.sp
    }

    when (actualVariant) {
        ButtonVariant.PRIMARY -> {
            Button(
                onClick = onClick,
                enabled = enabled && !loading,
                modifier = modifier
                    .fillMaxWidth()
                    .height(height),
                shape = RoundedCornerShape(Dimens.radiusXl),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.45f),
                    disabledContentColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f),
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp),
            ) {
                ButtonContent(text, icon, iconPositionRight, loading, MaterialTheme.colorScheme.onPrimary, fontSize)
            }
        }
        ButtonVariant.TONAL -> {
            Button(
                onClick = onClick,
                enabled = enabled && !loading,
                modifier = modifier
                    .fillMaxWidth()
                    .height(height),
                shape = RoundedCornerShape(Dimens.radiusXl),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                ),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
            ) {
                ButtonContent(text, icon, iconPositionRight, loading, MaterialTheme.colorScheme.onSurface, fontSize)
            }
        }
        ButtonVariant.SECONDARY -> {
            Button(
                onClick = onClick,
                enabled = enabled && !loading,
                modifier = modifier
                    .fillMaxWidth()
                    .height(height),
                shape = RoundedCornerShape(Dimens.radiusXl),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                ),
            ) {
                ButtonContent(text, icon, iconPositionRight, loading, MaterialTheme.colorScheme.onSurface, fontSize)
            }
        }
        ButtonVariant.OUTLINED -> {
            OutlinedButton(
                onClick = onClick,
                enabled = enabled && !loading,
                modifier = modifier
                    .fillMaxWidth()
                    .height(height),
                shape = RoundedCornerShape(Dimens.radiusXl),
                border = androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.outline),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.primary,
                ),
            ) {
                ButtonContent(text, icon, iconPositionRight, loading, MaterialTheme.colorScheme.primary, fontSize)
            }
        }
        ButtonVariant.GHOST -> {
            TextButton(
                onClick = onClick,
                enabled = enabled && !loading,
                modifier = modifier
                    .fillMaxWidth()
                    .height(height),
                shape = RoundedCornerShape(Dimens.radiusXl),
            ) {
                ButtonContent(text, icon, iconPositionRight, loading, primaryBright(), fontSize)
            }
        }
    }
}

@Composable
private fun ButtonContent(
    text: String,
    icon: ImageVector?,
    iconPositionRight: Boolean,
    loading: Boolean,
    contentColor: Color,
    fontSize: androidx.compose.ui.unit.TextUnit,
) {
    if (loading) {
        CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp, color = contentColor)
    } else {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            if (icon != null && !iconPositionRight) {
                Icon(icon, contentDescription = null, tint = contentColor, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
            }
            Text(
                text = text,
                fontFamily = PlusJakartaSans,
                fontWeight = FontWeight.Bold,
                fontSize = fontSize,
                color = contentColor,
            )
            if (icon != null && iconPositionRight) {
                Spacer(Modifier.width(8.dp))
                Icon(icon, contentDescription = null, tint = contentColor, modifier = Modifier.size(18.dp))
            }
        }
    }
}

// ============================================================================
// Modern Input Field (mirrors components/common/Input/index.tsx)
// ============================================================================

@Composable
fun DomiTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String? = null,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    isPassword: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text,
    error: String? = null,
    helper: String? = null,
    leftIcon: ImageVector? = null,
    rightIcon: ImageVector? = null,
    onRightIconClick: (() -> Unit)? = null,
    editable: Boolean = true,
    singleLine: Boolean = true,
    maxLines: Int = 1,
    multiline: Boolean = false,
    lines: Int = 1,
) {
    var isFocused by remember { mutableStateOf(false) }
    var passwordVisible by remember { mutableStateOf(!isPassword) }
    val effectiveMaxLines = if (lines > 1) lines else if (multiline) 4 else maxLines
    val effectiveSingleLine = if (multiline || effectiveMaxLines > 1) false else singleLine

    Column(modifier = modifier.fillMaxWidth()) {
        if (!label.isNullOrBlank()) {
            Text(
                text = label.uppercase(),
                fontFamily = Inter,
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp,
                letterSpacing = 2.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(start = 4.dp, bottom = 8.dp),
            )
        }

        val containerBg = when {
            error != null -> MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.35f)
            !editable -> MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.6f)
            else -> MaterialTheme.colorScheme.surfaceContainerLow
        }

        val borderColor = when {
            error != null -> MaterialTheme.colorScheme.error
            isFocused -> MaterialTheme.colorScheme.primary
            else -> Color.Transparent
        }

        val borderWidth = if (isFocused || error != null) 2.dp else 0.dp

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .then(if (effectiveMaxLines > 1) Modifier.height(100.dp) else Modifier.height(56.dp))
                .clip(RoundedCornerShape(Dimens.radiusXl))
                .background(containerBg)
                .then(
                    if (borderWidth > 0.dp) Modifier.border(
                        borderWidth,
                        borderColor,
                        RoundedCornerShape(Dimens.radiusXl)
                    ) else Modifier
                )
                .padding(horizontal = 16.dp, vertical = if (effectiveMaxLines > 1) 12.dp else 0.dp),
            contentAlignment = if (effectiveMaxLines > 1) Alignment.TopStart else Alignment.CenterStart,
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = if (effectiveMaxLines > 1) Alignment.Top else Alignment.CenterVertically,
            ) {
                if (leftIcon != null) {
                    Icon(
                        imageVector = leftIcon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier
                            .padding(end = 12.dp)
                            .size(20.dp),
                    )
                }

                Box(modifier = Modifier.weight(1f)) {
                    if (value.isEmpty() && placeholder.isNotEmpty()) {
                        Text(
                            text = placeholder,
                            fontFamily = Inter,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        )
                    }

                    BasicTextField(
                        value = value,
                        onValueChange = onValueChange,
                        modifier = Modifier
                            .fillMaxWidth()
                            .onFocusChanged { isFocused = it.isFocused },
                        enabled = editable,
                        singleLine = effectiveSingleLine,
                        maxLines = effectiveMaxLines,
                        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
                        visualTransformation = if (isPassword && !passwordVisible) PasswordVisualTransformation() else VisualTransformation.None,
                        textStyle = TextStyle(
                            fontFamily = Inter,
                            fontWeight = FontWeight.Medium,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                        ),
                        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                    )
                }

                if (isPassword) {
                    IconButton(
                        onClick = { passwordVisible = !passwordVisible },
                        modifier = Modifier.size(36.dp),
                    ) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = if (passwordVisible) "Hide password" else "Show password",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp),
                        )
                    }
                } else if (rightIcon != null) {
                    if (onRightIconClick != null) {
                        IconButton(onClick = onRightIconClick, modifier = Modifier.size(36.dp)) {
                            Icon(rightIcon, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(20.dp))
                        }
                    } else {
                        Icon(rightIcon, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(20.dp))
                    }
                }
            }
        }

        if (error != null) {
            Text(
                text = error,
                fontFamily = Inter,
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 4.dp, start = 8.dp),
            )
        } else if (helper != null) {
            Text(
                text = helper,
                fontFamily = Inter,
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp, start = 8.dp),
            )
        }
    }
}

// ============================================================================
// Amount Input (mirrors components/forms/AmountInput/index.tsx)
// ============================================================================

@Composable
fun AmountInput(
    value: String,
    onValueChange: (String) -> Unit,
    label: String = "Amount",
    error: String? = null,
    minAmount: Double = 5000.0,
    maxAmount: Double = 10000000.0,
    modifier: Modifier = Modifier,
) {
    val rawDigits = value.filter { it.isDigit() }
    val displayValue = if (rawDigits.isEmpty()) "" else {
        try {
            val num = rawDigits.toLong()
            String.format(java.util.Locale.US, "%,d", num)
        } catch (e: Exception) {
            rawDigits
        }
    }

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = label.uppercase(),
            fontFamily = Inter,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            letterSpacing = 0.5.sp,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(start = 4.dp, bottom = 6.dp),
        )

        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            border = if (error != null) androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.error)
            else androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.outlineVariant),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "₦",
                    fontFamily = PlusJakartaSans,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(end = 6.dp),
                )
                BasicTextField(
                    value = displayValue,
                    onValueChange = { input ->
                        val cleaned = input.filter { it.isDigit() }
                        onValueChange(cleaned)
                    },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    textStyle = TextStyle(
                        fontFamily = PlusJakartaSans,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                    ),
                    cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp, start = 4.dp, end = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = "Min: ₦${String.format(java.util.Locale.US, "%,.0f", minAmount)}",
                fontFamily = Inter,
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = "Max: ₦${String.format(java.util.Locale.US, "%,.0f", maxAmount)}",
                fontFamily = Inter,
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        if (error != null) {
            Text(
                text = error,
                fontFamily = Inter,
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 4.dp, start = 4.dp),
            )
        }
    }
}

// ============================================================================
// Dropdown Select (mirrors components/forms/DropdownSelect)
// ============================================================================

data class DropdownOption(val value: String, val label: String)

@Composable
fun DropdownSelect(
    label: String,
    value: String = "",
    options: List<DropdownOption>,
    onSelect: (String) -> Unit,
    placeholder: String = "Select an option",
    icon: ImageVector? = null,
    modifier: Modifier = Modifier,
    selectedValue: String = value,
    leadingIcon: ImageVector? = icon,
) {
    var showDialog by remember { mutableStateOf(false) }
    val effectiveValue = selectedValue.ifEmpty { value }
    val effectiveIcon = leadingIcon ?: icon
    val selectedLabel = options.find { it.value == effectiveValue }?.label ?: placeholder

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = label.uppercase(),
            fontFamily = Inter,
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp,
            letterSpacing = 1.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(start = 4.dp, bottom = 8.dp),
        )

        Surface(
            shape = RoundedCornerShape(Dimens.radiusXl),
            color = MaterialTheme.colorScheme.surfaceContainerLow,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .clickable { showDialog = true },
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (effectiveIcon != null) {
                        Icon(effectiveIcon, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(12.dp))
                    }
                    Text(
                        text = selectedLabel,
                        fontFamily = Inter,
                        fontWeight = if (effectiveValue.isNotEmpty()) FontWeight.Medium else FontWeight.Normal,
                        fontSize = 15.sp,
                        color = if (effectiveValue.isNotEmpty()) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    )
                }
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }

    if (showDialog) {
        Dialog(
            onDismissRequest = { showDialog = false },
            properties = DialogProperties(usePlatformDefaultWidth = false),
        ) {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .padding(vertical = 32.dp),
                shadowElevation = 8.dp,
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = label,
                        fontFamily = PlusJakartaSans,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(bottom = 16.dp),
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState()),
                    ) {
                        options.forEach { option ->
                            val isSelected = option.value == value
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.15f) else Color.Transparent)
                                    .clickable {
                                        onSelect(option.value)
                                        showDialog = false
                                    }
                                    .padding(vertical = 12.dp, horizontal = 12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(
                                    text = option.label,
                                    fontFamily = Inter,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 15.sp,
                                    color = if (isSelected) primaryBright() else MaterialTheme.colorScheme.onSurface,
                                )
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = primaryBright(),
                                        modifier = Modifier.size(18.dp),
                                    )
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(16.dp))
                    DomiButton(
                        text = "Close",
                        onClick = { showDialog = false },
                        tonal = true,
                    )
                }
            }
        }
    }
}

// ============================================================================
// Money Component (mirrors components/common/Money/index.tsx)
// ============================================================================

enum class MoneySize { SM, MD, LG, XL, HERO }
enum class MoneyTone { DEFAULT, MUTED, ON_PRIMARY, SUCCESS, ERROR, PRIMARY }

fun formatAmount(amount: Double, decimals: Boolean? = null): String {
    val abs = kotlin.math.abs(amount)
    val showDecimals = decimals ?: (abs % 1.0 != 0.0)
    return if (showDecimals) {
        String.format(java.util.Locale.US, "%,.2f", abs)
    } else {
        String.format(java.util.Locale.US, "%,.0f", abs)
    }
}

@Composable
fun Money(
    amount: Double,
    modifier: Modifier = Modifier,
    currency: String = "₦",
    size: MoneySize = MoneySize.MD,
    tone: MoneyTone = MoneyTone.DEFAULT,
    signed: Boolean = false,
    decimals: Boolean? = null,
    style: TextStyle? = null,
) {
    val (fontSize, lineHeight, fontWeight) = when (size) {
        MoneySize.SM -> Triple(14.sp, 18.sp, FontWeight.Bold)
        MoneySize.MD -> Triple(18.sp, 23.sp, FontWeight.Bold)
        MoneySize.LG -> Triple(24.sp, 30.sp, FontWeight.Bold)
        MoneySize.XL -> Triple(32.sp, 38.sp, FontWeight.ExtraBold)
        MoneySize.HERO -> Triple(40.sp, 46.sp, FontWeight.ExtraBold)
    }

    val color = style?.color?.takeIf { it != Color.Unspecified } ?: when (tone) {
        MoneyTone.DEFAULT -> MaterialTheme.colorScheme.onSurface
        MoneyTone.MUTED -> MaterialTheme.colorScheme.onSurfaceVariant
        MoneyTone.ON_PRIMARY -> MaterialTheme.colorScheme.onPrimary
        MoneyTone.SUCCESS -> Color(0xFF22C55E)
        MoneyTone.ERROR -> MaterialTheme.colorScheme.error
        MoneyTone.PRIMARY -> MaterialTheme.colorScheme.primary
    }

    val currencyColor = if (tone == MoneyTone.ON_PRIMARY) color else MaterialTheme.colorScheme.onSurfaceVariant
    val currencySize = (fontSize.value * 0.7f).sp

    val sign = if (amount < 0) "−" else if (signed && amount > 0) "+" else ""
    val formatted = formatAmount(amount, decimals)

    val annotatedString = androidx.compose.ui.text.buildAnnotatedString {
        if (sign.isNotEmpty()) {
            withStyle(
                androidx.compose.ui.text.SpanStyle(
                    fontFamily = PlusJakartaSans,
                    fontWeight = fontWeight,
                    fontSize = fontSize,
                    color = color,
                )
            ) {
                append(sign)
            }
        }
        withStyle(
            androidx.compose.ui.text.SpanStyle(
                fontFamily = PlusJakartaSans,
                fontWeight = FontWeight.SemiBold,
                fontSize = currencySize,
                color = currencyColor.copy(alpha = 0.9f),
            )
        ) {
            append(currency)
        }
        withStyle(
            androidx.compose.ui.text.SpanStyle(
                fontFamily = PlusJakartaSans,
                fontWeight = fontWeight,
                fontSize = fontSize,
                color = color,
            )
        ) {
            append(formatted)
        }
    }

    Text(
        text = annotatedString,
        lineHeight = lineHeight,
        modifier = modifier,
    )
}

@Composable
fun MoneyText(
    amount: Double,
    modifier: Modifier = Modifier,
    large: Boolean = false,
    color: Color = Color.Unspecified,
) {
    Money(
        amount = amount,
        size = if (large) MoneySize.XL else MoneySize.MD,
        style = TextStyle(color = color),
        modifier = modifier,
    )
}

// ============================================================================
// Badge Component (mirrors components/common/Badge/index.tsx)
// ============================================================================

enum class BadgeStatus {
    SUCCESS, WARNING, ERROR, INFO, NEUTRAL;
    companion object {
        val Success = SUCCESS
        val Warning = WARNING
        val Error = ERROR
        val Info = INFO
        val Neutral = NEUTRAL
    }
}

@Composable
fun Badge(
    status: BadgeStatus,
    label: String,
    modifier: Modifier = Modifier,
    dot: Boolean = true,
) {
    val isDark = isDarkMode()
    val (bg, fg, dotColor) = when (status) {
        BadgeStatus.SUCCESS -> if (isDark) {
            Triple(Color(0xFF14532D), Color(0xFFDCFCE7), Color(0xFF4ADE80))
        } else {
            Triple(Color(0xFFDCFCE7), Color(0xFF14532D), Color(0xFF22C55E))
        }
        BadgeStatus.WARNING -> if (isDark) {
            Triple(Color(0xFF78350F), Color(0xFFFEF3C7), Color(0xFFFBBF24))
        } else {
            Triple(Color(0xFFFEF3C7), Color(0xFF92400E), Color(0xFFF59E0B))
        }
        BadgeStatus.ERROR -> if (isDark) {
            Triple(Color(0xFFFFDAD6), Color(0xFF93000A), Color(0xFFBA1A1A))
        } else {
            Triple(Color(0xFFFEE2E2), Color(0xFF991B1B), Color(0xFFEF4444))
        }
        BadgeStatus.INFO -> if (isDark) {
            Triple(Color(0xFF0F2A5E), Color(0xFFDAE2FF), Color(0xFF5B8BFF))
        } else {
            Triple(Color(0xFFDCE1FF), Color(0xFF00164D), Color(0xFF0B50DA))
        }
        BadgeStatus.NEUTRAL -> Triple(
            MaterialTheme.colorScheme.surfaceContainerHigh,
            MaterialTheme.colorScheme.onSurfaceVariant,
            MaterialTheme.colorScheme.outline,
        )
    }

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(9999.dp),
        color = bg,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            if (dot) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(dotColor),
                )
            }
            Text(
                text = label.uppercase(),
                fontFamily = Inter,
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp,
                lineHeight = 16.sp,
                color = fg,
            )
        }
    }
}

@Composable
fun StatusBadge(status: String, modifier: Modifier = Modifier) {
    val badgeStatus = when (status.lowercase()) {
        "verified", "success", "completed", "paid", "active", "on_track" -> BadgeStatus.SUCCESS
        "pending", "repaying", "processing" -> BadgeStatus.WARNING
        "rejected", "failed", "overdue", "suspended" -> BadgeStatus.ERROR
        else -> BadgeStatus.NEUTRAL
    }
    Badge(
        status = badgeStatus,
        label = status.replace("_", " "),
        modifier = modifier,
        dot = true,
    )
}

// ============================================================================
// List Item Component (mirrors components/common/ListItem/index.tsx)
// ============================================================================

@Composable
fun ListItem(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    leadingIcon: ImageVector? = null,
    leadingColor: Color? = null,
    trailing: (@Composable () -> Unit)? = null,
    trailingText: String? = null,
    chevron: Boolean = false,
    onClick: (() -> Unit)? = null,
    destructive: Boolean = false,
) {
    val iconTint = if (destructive) MaterialTheme.colorScheme.error else (leadingColor ?: primaryBright())
    val showChevron = chevron || (onClick != null && trailing == null && trailingText == null)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        if (leadingIcon != null) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(iconTint.copy(alpha = 0.08f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = leadingIcon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(20.dp),
                )
            }
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontFamily = Inter,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                color = if (destructive) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            if (!subtitle.isNullOrBlank()) {
                Spacer(Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    fontFamily = Inter,
                    fontWeight = FontWeight.Normal,
                    fontSize = 12.sp,
                    lineHeight = 18.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }

        if (trailingText != null) {
            Text(
                text = trailingText,
                fontFamily = Inter,
                fontWeight = FontWeight.Normal,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        trailing?.invoke()

        if (showChevron) {
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.outline,
                modifier = Modifier.size(20.dp),
            )
        }
    }
}

// ============================================================================
// DomiCard Component (mirrors components/common/Card/index.tsx)
// ============================================================================

enum class DomiCardElevation {
    FLAT, RAISED, GLOW;
    companion object {
        val Flat = FLAT
        val Raised = RAISED
        val Glow = GLOW
    }
}

@Composable
fun DomiCard(
    modifier: Modifier = Modifier,
    elevation: DomiCardElevation = DomiCardElevation.FLAT,
    padded: Boolean = true,
    onClick: (() -> Unit)? = null,
    shape: androidx.compose.ui.graphics.Shape = RoundedCornerShape(20.dp),
    content: @Composable ColumnScope.() -> Unit,
) {
    val isDark = isDarkMode()
    val bgColor = if (isDark) MaterialTheme.colorScheme.surfaceContainer else MaterialTheme.colorScheme.surface
    val borderStroke = if (elevation == DomiCardElevation.FLAT) {
        androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    } else null
    val shadowElevation = when (elevation) {
        DomiCardElevation.FLAT -> 0.dp
        DomiCardElevation.RAISED -> 3.dp
        DomiCardElevation.GLOW -> 10.dp
    }
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .then(
                if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier
            ),
        shape = shape,
        color = bgColor,
        border = borderStroke,
        shadowElevation = shadowElevation,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .then(if (padded) Modifier.padding(16.dp) else Modifier),
            content = content,
        )
    }
}

// ============================================================================
// Allocation Breakdown (mirrors components/savings/AllocationBreakdown/index.tsx)
// ============================================================================

@Composable
fun AllocationBreakdown(
    allocation: com.nexahub.domicoop.core.ContributionAllocation,
    totalSavings: Double = allocation.shares + allocation.social + allocation.savings + allocation.deposit,
    modifier: Modifier = Modifier,
) {
    AllocationBreakdown(
        shares = allocation.shares,
        social = allocation.social,
        savings = allocation.savings,
        deposit = allocation.deposit,
        total = totalSavings,
        modifier = modifier,
    )
}

@Composable
fun AllocationBreakdown(
    shares: Double,
    social: Double,
    savings: Double,
    deposit: Double,
    total: Double = shares + social + savings + deposit,
    modifier: Modifier = Modifier,
) {
    val effectiveTotal = if (total > 0) total else 1.0
    val sharesColor = primaryBright()
    val socialColor = MaterialTheme.colorScheme.tertiary
    val savingsColor = Color(0xFF22C55E)
    val depositColor = Color(0xFFF59E0B)

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.20f)),
        modifier = modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            // Header
            Text(
                text = "CONTRIBUTION ALLOCATION",
                fontFamily = Inter,
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp,
                letterSpacing = 0.5.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            // Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.20f)),
            ) {
                if (shares > 0) Box(Modifier.weight(shares.toFloat()).fillMaxSize().background(sharesColor))
                if (social > 0) Box(Modifier.weight(social.toFloat()).fillMaxSize().background(socialColor))
                if (savings > 0) Box(Modifier.weight(savings.toFloat()).fillMaxSize().background(savingsColor))
                if (deposit > 0) Box(Modifier.weight(deposit.toFloat()).fillMaxSize().background(depositColor))
            }

            // Allocation Rows
            AllocationBreakdownRow(
                dotColor = sharesColor,
                label = "Shares",
                desc = "Fixed monthly",
                amount = shares,
                percent = (shares / effectiveTotal) * 100,
            )
            AllocationBreakdownRow(
                dotColor = socialColor,
                label = "Social",
                desc = "Fixed monthly",
                amount = social,
                percent = (social / effectiveTotal) * 100,
            )
            AllocationBreakdownRow(
                dotColor = savingsColor,
                label = "Savings",
                desc = "Flexible (capped)",
                amount = savings,
                percent = (savings / effectiveTotal) * 100,
            )
            if (deposit > 0) {
                AllocationBreakdownRow(
                    dotColor = depositColor,
                    label = "Deposit",
                    desc = "Overflow only",
                    amount = deposit,
                    percent = (deposit / effectiveTotal) * 100,
                )
            }

            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant,
                thickness = 1.dp,
                modifier = Modifier.padding(top = 4.dp),
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Total Credited",
                    fontFamily = Inter,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Money(
                    amount = shares + social + savings + deposit,
                    size = MoneySize.MD,
                    tone = MoneyTone.DEFAULT,
                )
            }
        }
    }
}

@Composable
private fun AllocationBreakdownRow(
    dotColor: Color,
    label: String,
    desc: String,
    amount: Double,
    percent: Double,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f),
        ) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(dotColor),
            )
            Spacer(Modifier.width(12.dp))
            Column {
                Text(
                    text = label,
                    fontFamily = Inter,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = desc,
                    fontFamily = Inter,
                    fontWeight = FontWeight.Normal,
                    fontSize = 9.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        Column(horizontalAlignment = Alignment.End) {
            if (amount > 0) {
                Text(
                    text = "₦${com.nexahub.domicoop.core.Format.currency(amount)}",
                    fontFamily = Inter,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = String.format(java.util.Locale.US, "%.1f%%", percent),
                    fontFamily = Inter,
                    fontWeight = FontWeight.Normal,
                    fontSize = 9.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            } else {
                Text(
                    text = "₦0",
                    fontFamily = Inter,
                    fontWeight = FontWeight.Normal,
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

// ============================================================================
// Empty, Loading, Error states
// ============================================================================

@Composable
fun EmptyState(
    title: String,
    message: String? = null,
    body: String = message ?: "",
    icon: ImageVector = Icons.Default.ReceiptLong,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 40.dp, horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceContainerHigh),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(28.dp),
            )
        }
        Spacer(Modifier.height(16.dp))
        Text(
            text = title,
            fontFamily = PlusJakartaSans,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
        )
        if (body.isNotEmpty()) {
            Spacer(Modifier.height(8.dp))
            Text(
                text = body,
                fontFamily = Inter,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp),
            )
        }
        if (actionLabel != null && onAction != null) {
            Spacer(Modifier.height(24.dp))
            DomiButton(
                text = actionLabel,
                onClick = onAction,
                tonal = true,
                size = ButtonSize.SM,
                modifier = Modifier.fillMaxWidth(0.6f),
            )
        }
    }
}

@Composable
fun LoadingState(
    message: String? = null,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(40.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(
                modifier = Modifier.size(32.dp),
                strokeWidth = 3.dp,
                color = MaterialTheme.colorScheme.primary,
            )
            if (message != null) {
                Spacer(Modifier.height(12.dp))
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
fun ErrorState(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(
            imageVector = Icons.Default.ErrorOutline,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.error,
            modifier = Modifier.size(40.dp),
        )
        Spacer(Modifier.height(12.dp))
        Text(
            text = message,
            fontFamily = Inter,
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.error,
        )
        Spacer(Modifier.height(16.dp))
        DomiButton(text = "Try again", onClick = onRetry, tonal = true, size = ButtonSize.SM)
    }
}

// ============================================================================
// Cards, Detail Rows, Labels
// ============================================================================

@Composable
fun BrandHeroCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        shadowElevation = 8.dp,
        color = Color.Transparent,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(listOf(BrandGradientStart, BrandGradientEnd)),
                    RoundedCornerShape(20.dp),
                )
                .padding(20.dp),
        ) {
            Column(content = content)
        }
    }
}

@Composable
fun SectionLabel(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text.uppercase(),
        fontFamily = Inter,
        fontWeight = FontWeight.SemiBold,
        fontSize = 12.sp,
        letterSpacing = 1.sp,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = modifier,
    )
}

@Composable
fun InfoRow(label: String, value: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            fontFamily = Inter,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.width(12.dp))
        Text(
            text = value,
            fontFamily = Inter,
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.End,
        )
    }
}

@Composable
fun TransactionDetailCard(
    icon: ImageVector,
    label: String,
    value: String,
    showCopy: Boolean = false,
    modifier: Modifier = Modifier,
) {
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.surfaceContainer),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(20.dp))
                }
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(
                        text = label.uppercase(),
                        fontFamily = Inter,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp,
                        letterSpacing = 0.5.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = value,
                        fontFamily = Inter,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                }
            }
            if (showCopy) {
                IconButton(
                    onClick = {
                        clipboardManager.setText(AnnotatedString(value))
                        Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.size(36.dp),
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Copy",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp),
                    )
                }
            }
        }
    }
}

// ============================================================================
// Modals & Dialogs (DialogShell, ConfirmationModal, InfoModal, SuccessModal, GuidelinesGate)
// ============================================================================

@Composable
fun DialogShell(
    visible: Boolean,
    onDismissRequest: () -> Unit,
    title: String,
    message: String? = null,
    icon: ImageVector = Icons.Default.Info,
    iconTint: Color = primaryBright(),
    bodyContent: (@Composable ColumnScope.() -> Unit)? = null,
    buttons: @Composable ColumnScope.() -> Unit,
) {
    if (visible) {
        Dialog(
            onDismissRequest = onDismissRequest,
            properties = DialogProperties(usePlatformDefaultWidth = false),
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .padding(vertical = 24.dp),
                shape = RoundedCornerShape(24.dp),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 10.dp,
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(iconTint.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(32.dp))
                    }
                    Spacer(Modifier.height(16.dp))
                    Text(
                        text = title,
                        fontFamily = PlusJakartaSans,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center,
                    )
                    if (!message.isNullOrBlank()) {
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = message,
                            fontFamily = Inter,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            lineHeight = 20.sp,
                        )
                    }

                    if (bodyContent != null) {
                        Spacer(Modifier.height(16.dp))
                        bodyContent()
                    }

                    Spacer(Modifier.height(24.dp))
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        content = buttons,
                    )
                }
            }
        }
    }
}

@Composable
fun ConfirmationModal(
    visible: Boolean,
    title: String,
    message: String,
    confirmText: String = "Confirm",
    cancelText: String = "Cancel",
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
    isDestructive: Boolean = false,
) {
    DialogShell(
        visible = visible,
        onDismissRequest = onCancel,
        title = title,
        message = message,
        icon = if (isDestructive) Icons.Default.Logout else Icons.Default.Info,
        iconTint = if (isDestructive) MaterialTheme.colorScheme.error else primaryBright(),
        buttons = {
            DomiButton(
                text = confirmText,
                onClick = onConfirm,
                variant = if (isDestructive) ButtonVariant.TONAL else ButtonVariant.PRIMARY,
            )
            DomiButton(
                text = cancelText,
                onClick = onCancel,
                tonal = true,
            )
        },
    )
}

@Composable
fun InfoModal(
    visible: Boolean,
    title: String,
    message: String,
    primaryButtonText: String = "OK",
    onPrimaryClick: () -> Unit = {},
    onDismissRequest: () -> Unit = onPrimaryClick,
    onDismiss: () -> Unit = onDismissRequest,
    icon: ImageVector = Icons.Default.Info,
) {
    val handleDismiss: () -> Unit = {
        onPrimaryClick()
        if (onDismiss != onDismissRequest) {
            onDismiss()
        }
    }
    DialogShell(
        visible = visible,
        onDismissRequest = handleDismiss,
        title = title,
        message = message,
        icon = icon,
        buttons = {
            DomiButton(text = primaryButtonText, onClick = handleDismiss)
        },
    )
}

@Composable
fun SuccessModal(
    visible: Boolean,
    title: String = "Success",
    message: String = "Action completed successfully.",
    buttonText: String = "Continue",
    onDone: () -> Unit = {},
    onDismiss: () -> Unit = onDone,
) {
    val handleDone: () -> Unit = {
        onDone()
        if (onDismiss != onDone) {
            onDismiss()
        }
    }
    DialogShell(
        visible = visible,
        onDismissRequest = handleDone,
        title = title,
        message = message,
        icon = Icons.Default.Check,
        iconTint = Color(0xFF22C55E),
        buttons = {
            DomiButton(text = buttonText, onClick = handleDone)
        },
    )
}

@Composable
fun GuidelinesGate(
    visible: Boolean,
    onAcknowledge: () -> Unit,
    onViewFull: () -> Unit,
) {
    val highlights = listOf(
        "Registration fee is ₦20,000, compulsory for new intake (non-refundable).",
        "If you want to exit the cooperative, you must give 6 months' notice with a written letter.",
        "Consistent payment for 6 months qualifies you for a loan of 3 times what you have, at an interest rate of 10%, repayable within one year.",
        "If you guarantee someone and the person defaults, your money will be held until that person pays.",
        "There is a late payment fee of ₦500.",
    )

    DialogShell(
        visible = visible,
        onDismissRequest = {},
        title = "Cooperative Guidelines",
        message = "Before you continue, a quick reminder of a few key rules:",
        icon = Icons.Default.Gavel,
        iconTint = primaryBright(),
        bodyContent = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                highlights.forEach { text ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.Top,
                    ) {
                        Box(
                            modifier = Modifier
                                .padding(top = 6.dp)
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary),
                        )
                        Text(
                            text = text,
                            fontFamily = Inter,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            lineHeight = 18.sp,
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            }
        },
        buttons = {
            DomiButton(text = "I Acknowledge", onClick = onAcknowledge)
            DomiButton(text = "View Full Guidelines", onClick = onViewFull, variant = ButtonVariant.GHOST)
        },
    )
}
