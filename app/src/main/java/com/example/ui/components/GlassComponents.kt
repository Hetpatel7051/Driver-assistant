package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun GlassmorphicBackground(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    val darkTheme = isSystemInDarkTheme()
    
    // Smooth infinite breathing state for drifting gradient lights
    val infiniteTransition = rememberInfiniteTransition(label = "drift_glow")
    val driftAnimX by infiniteTransition.animateFloat(
        initialValue = 0.15f,
        targetValue = 0.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "drift_x"
    )
    val driftAnimY by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.45f,
        animationSpec = infiniteRepeatable(
            animation = tween(10000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "drift_y"
    )

    val bgGradient = if (darkTheme) {
        Brush.verticalGradient(
            colors = listOf(
                Color(0xFF0F111A), // Deep interstellar blue/black
                Color(0xFF141724), 
                Color(0xFF0D0E14)
            )
        )
    } else {
        Brush.verticalGradient(
            colors = listOf(
                Color(0xFFF0F4FA), // Ultra-clean premium slate tint
                Color(0xFFE5ECF6), 
                Color(0xFFFAFAFC)
            )
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(bgGradient)
    ) {
        // Decorative glowing ambient fluid circles drifting in the back.
        // They shine through the GlassCards!
        val circleColor1 = if (darkTheme) Color(0xFF005FB8).copy(alpha = 0.15f) else Color(0xFFD3E3FD).copy(alpha = 0.6f)
        val circleColor2 = if (darkTheme) Color(0xFF6750A4).copy(alpha = 0.12f) else Color(0xFFE8DEF8).copy(alpha = 0.5f)
        
        Canvas(modifier = Modifier.fillMaxSize()) {
            // First glow spot (drifting slightly)
            drawCircle(
                color = circleColor1,
                radius = size.width * 0.48f,
                center = Offset(size.width * driftAnimX, size.height * driftAnimY)
            )
            // Second opposite glow spot
            drawCircle(
                color = circleColor2,
                radius = size.width * 0.52f,
                center = Offset(size.width * (1f - driftAnimX), size.height * (1.1f - driftAnimY))
            )
        }
        
        this.content()
    }
}

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    borderStroke: BorderStroke? = null,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val dark = isSystemInDarkTheme()
    
    // 15% opacity white on dark mode, 65% white on light theme
    val bgColor = if (dark) {
        Color(0x1F2230D9) // Dynamic translucent surface representation
    } else {
        Color(0xB3FFFFFF) // 70% light premium white frosted translucent glass sheet
    }
    
    // Crisp highlight borders for refraction index
    val borderBrush = if (dark) {
        Brush.linearGradient(
            colors = listOf(
                Color.White.copy(alpha = 0.22f),
                Color.White.copy(alpha = 0.05f)
            )
        )
    } else {
        Brush.linearGradient(
            colors = listOf(
                Color.White.copy(alpha = 0.65f),
                Color.White.copy(alpha = 0.15f)
            )
        )
    }

    val finalModifier = modifier
        .clip(RoundedCornerShape(24.dp))
        .background(bgColor)
        .border(
            borderStroke ?: BorderStroke(1.2.dp, borderBrush),
            shape = RoundedCornerShape(24.dp)
        )

    if (onClick != null) {
        Box(
            modifier = finalModifier.clickable(onClick = onClick)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                content = content
            )
        }
    } else {
        Box(
            modifier = finalModifier
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                content = content
            )
        }
    }
}

@Composable
fun AnimatedSafarLogo(
    modifier: Modifier = Modifier
) {
    val darkTheme = isSystemInDarkTheme()
    val infiniteTransition = rememberInfiniteTransition(label = "logo_anim")
    
    // Scale pulse for deep breathing rhythm
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.94f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "logo_scale"
    )

    // Secondary pulse for outer wave
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.06f,
        targetValue = 0.35f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "logo_glow"
    )

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(100.dp)
        ) {
            // Ripple wave 1: wide glowing circle
            Box(
                modifier = Modifier
                    .size(88.dp)
                    .scale(scale * 1.15f)
                    .alpha(glowAlpha)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                Color(0xFF005FB8).copy(alpha = 0.5f),
                                Color.Transparent
                            )
                        )
                    )
            )

            // Ripple wave 2: core icon container with glowing border
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(68.dp)
                    .scale(scale)
                    .clip(CircleShape)
                    .background(
                        if (darkTheme) Color(0xFF1E2230) else Color(0xFFE0EBFB)
                    )
                    .border(
                        BorderStroke(
                            2.dp,
                            Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFF005FB8),
                                    Color(0xFF6750A4)
                                )
                            )
                        ),
                        CircleShape
                    )
            ) {
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = "Safar Logo",
                    tint = if (darkTheme) Color(0xFFD1E4FF) else Color(0xFF005FB8),
                    modifier = Modifier.size(32.dp)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        Text(
            text = "SAFAR TRACKER",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.ExtraBold,
            color = if (darkTheme) Color(0xFFE2E8F0) else Color(0xFF475569),
            letterSpacing = 2.5.sp,
            textAlign = TextAlign.Center
        )
        Text(
            text = "Connected Logistics Strategy",
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium,
            color = if (darkTheme) Color(0xFF94A3B8) else Color(0xFF64748B),
            textAlign = TextAlign.Center
        )
    }
}
