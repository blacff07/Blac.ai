package com.blac.ai.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.blac.ai.data.ChatMessage

@Composable
fun ChatBubble(message: ChatMessage) {
    val isUser = message.isUser

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = if (isUser)
                    MaterialTheme.colorScheme.primaryContainer
                else
                    MaterialTheme.colorScheme.secondaryContainer
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = message.content,
                fontSize = if (message.isCode) 14.sp else 16.sp,
                fontFamily = if (message.isCode) FontFamily.Monospace else FontFamily.Default,
                modifier = Modifier.padding(12.dp)
            )
        }
    }
}