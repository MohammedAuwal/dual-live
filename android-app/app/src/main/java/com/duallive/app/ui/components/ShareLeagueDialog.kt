package com.duallive.app.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ShareLeagueDialog(
    leagueName: String,
    inviteCode: String,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val fullLink = "https://duallive.app/join/$inviteCode"

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(onClick = onDismiss) { Text("Done") }
        },
        title = { Text("Invite Others", fontWeight = FontWeight.Bold) },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Text("Players can join using this link or code:")
                
                Spacer(modifier = Modifier.height(20.dp))
                
                // QR Placeholder
                Box(
                    modifier = Modifier.size(120.dp).background(Color.White, RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Share, contentDescription = null, tint = Color.Black)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Clickable Invite Code
                Surface(
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.clickable {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        clipboard.setPrimaryClip(ClipData.newPlainText("League Code", inviteCode))
                        Toast.makeText(context, "Code Copied!", Toast.LENGTH_SHORT).show()
                    }
                ) {
                    Text(
                        text = inviteCode,
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black
                    )
                }

                TextButton(onClick = {
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    clipboard.setPrimaryClip(ClipData.newPlainText("League Link", fullLink))
                    Toast.makeText(context, "Link Copied!", Toast.LENGTH_SHORT).show()
                }) {
                    Text("Copy Invite Link", fontSize = 12.sp)
                }
            }
        }
    )
}
