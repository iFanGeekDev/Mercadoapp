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
    Box(modifier = modifier, contentAlignment = Alignment.TopEnd) {
        Icon(
            imageVector = icon,
            contentDescription = "Cart",
            tint = iconTint,
            modifier = Modifier.padding(end = 4.dp, top = 4.dp)
        )
        
        AnimatedVisibility(
            visible = count > 0,
            enter = scaleIn() + fadeIn(),
            exit = scaleOut() + fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .size(18.dp)
                    .background(badgeColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (count > 99) "99+" else count.toString(),
                    color = Color.White,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
