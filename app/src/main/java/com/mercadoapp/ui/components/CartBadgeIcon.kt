package com.mercadoapp.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mercadoapp.ui.theme.Brand500

@Composable
fun CartBadgeIcon(
    icon: ImageVector,
    count: Int,
    modifier: Modifier = Modifier,
    badgeColor: Color = Color.Red,
    iconTint: Color = Color.White
) {
    Box(modifier = modifier.size(32.dp), contentAlignment = Alignment.Center) {
        Icon(
            imageVector = icon,
            contentDescription = "Cart",
            tint = iconTint,
            modifier = Modifier.size(22.dp)
        )
        
        AnimatedVisibility(
            visible = count > 0,
            enter = scaleIn() + fadeIn(),
            exit = scaleOut() + fadeOut(),
            modifier = Modifier.align(Alignment.TopEnd)
        ) {
            Box(
                modifier = Modifier
                    .offset(x = (-2).dp, y = 2.dp)
                    .size(16.dp)
                    .background(badgeColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (count > 99) "99+" else count.toString(),
                    color = Color.White,
                    fontSize = 9.sp,
                    lineHeight = 9.sp,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }
    }
}
