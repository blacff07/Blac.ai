package com.blac.ai.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalTextStyle  // ✅ ADD THIS IMPORT
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.blac.ai.data.Attachment
import com.blac.ai.data.ChatMessage
import com.blac.ai.utils.CodeHighlighter
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatBubble(
    message: ChatMessage,
    onCopy: (String) -> Unit,
    onShare: (String) -> Unit
) {
    val isUser = message.isUser
    var isExpanded by remember { mutableStateOf(false) }
    var showActions by remember { mutableStateOf(false) }

    val enterTransition = remember {
        slideInVertically(
            initialOffsetY = { it / 2 },
            animationSpec = tween(300, easing = FastOutSlowInEasing)
        ) + fadeIn(animationSpec = tween(300))
    }

    AnimatedVisibility(
        visible = true,
        enter = enterTransition
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            if (isExpanded) {
                Text(
                    text = SimpleDateFormat("HH:mm, MMM d", Locale.getDefault())
                        .format(Date(message.timestamp)),
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                    modifier = Modifier
                        .padding(
                            start = if (isUser) 0.dp else 16.dp,
                            end = if (isUser) 16.dp else 0.dp,
                            bottom = 4.dp
                        )
                        .align(if (isUser) Alignment.End else Alignment.Start)
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentWidth(if (isUser) Alignment.End else Alignment.Start)
            ) {
                Card(
                    onClick = {
                        isExpanded = !isExpanded
                        showActions = !showActions
                    },
                    colors = CardDefaults.cardColors(
                        containerColor = if (isUser)
                            MaterialTheme.colorScheme.primaryContainer
                        else
                            MaterialTheme.colorScheme.secondaryContainer
                    ),
                    shape = RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                        bottomStart = if (isUser) 16.dp else 4.dp,
                        bottomEnd = if (isUser) 4.dp else 16.dp
                    ),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = if (showActions) 8.dp else 2.dp
                    ),
                    modifier = Modifier
                        .animateContentSize()
                        .shadow(
                            elevation = if (showActions) 8.dp else 2.dp,
                            shape = RoundedCornerShape(16.dp),
                            clip = false
                        )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp)
                    ) {
                        if (message.isCode) {
                            CodeContent(
                                code = message.content,
                                language = message.language
                            )
                        } else {
                            Text(
                                text = message.content,
                                fontSize = 16.sp,
                                lineHeight = 22.sp,
                                color = if (isUser)
                                    MaterialTheme.colorScheme.onPrimaryContainer
                                else
                                    MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }

                        AnimatedVisibility(visible = showActions) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp),
                                horizontalArrangement = Arrangement.End
                            ) {
                                IconButton(
                                    onClick = { onCopy(message.content) },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        Icons.Default.ContentCopy,
                                        contentDescription = "Copy",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                                IconButton(
                                    onClick = { onShare(message.content) },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Share,
                                        contentDescription = "Share",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }

                        if (message.attachments.isNotEmpty()) {
                            AttachmentPreview(
                                attachments = message.attachments,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CodeContent(code: String, language: String) {
    val highlighter = remember { CodeHighlighter() }
    val highlightedText = remember(code, language) {
        highlighter.highlight(code, language)
    }

    Surface(
        color = Color(0xFF1E1E1E),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            if (language.isNotBlank()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF2D2D2D))
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = language.uppercase(),
                        fontSize = 12.sp,
                        color = Color(0xFFBBBBBB),
                        fontWeight = FontWeight.Bold
                    )
                    Icon(
                        Icons.Default.Code,
                        contentDescription = null,
                        tint = Color(0xFFBBBBBB),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            BasicText(
                text = highlightedText,
                style = LocalTextStyle.current.copy(
                    fontSize = 13.sp,
                    fontFamily = FontFamily.Monospace,
                    color = Color(0xFFD4D4D4)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            )
        }
    }
}

@Composable
private fun AttachmentPreview(
    attachments: List<Attachment>,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            Icons.Default.AttachFile,
            contentDescription = null,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = "${attachments.size} file(s) attached",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}