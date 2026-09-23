package com.app.uicomponents.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.app.uicomponents.theme.LumiformColors
//Todo Marina, declare all string res to prevent problems also on multilanguage in future
@Composable
fun LoadingView(modifier: Modifier = Modifier) {
    val colors = LumiformColors.current
    Column(modifier = modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        CircularProgressIndicator(color = colors.accent)
    }
}
//Todo Marina, declare all string res to prevent problems also on multilanguage in future

@Composable
fun ErrorView(message: String, modifier: Modifier = Modifier, onRetry: (() -> Unit)? = null) {
    val colors = LumiformColors.current
    Column(
        modifier = modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = message, color = colors.textSecondary)
        if (onRetry != null) {
            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(containerColor = colors.accent, contentColor = colors.onAccent),
                modifier = Modifier.padding(top = 12.dp)
            ) {
                Text("Retry")
            }
        }
    }
}
//Todo Marina, declare all string res to prevent problems also on multilanguage in future

@Composable
fun EmptyView(message: String = "Nothing here yet", modifier: Modifier = Modifier) {
    val colors = LumiformColors.current
    Column(modifier = modifier.fillMaxSize().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Text(text = message, color = colors.textSecondary)
    }
}

//Todo Marina, declare all string res to prevent problems also on multilanguage in future
@Composable
fun OfflineBanner(modifier: Modifier = Modifier) {
    val colors = LumiformColors.current
    Row(
        modifier = modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(imageVector = Icons.Filled.CloudOff, contentDescription = null, tint = colors.textMuted)
        Text(text = "You're offline - showing previously saved content", style = MaterialTheme.typography.bodyMedium, color = colors.textMuted)
    }
}
