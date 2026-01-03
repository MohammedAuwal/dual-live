package com.duallive.app.ucl2026.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.duallive.app.ucl2026.logic.UclPhase

@Composable
fun Ucl26AdminScreen(currentPhase: UclPhase, onProceed: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NavyBackground)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Glass Status Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(GlassWhite, RoundedCornerShape(16.dp))
                .border(1.dp, GoldAccent.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("CURRENT PHASE", color = Color.White.copy(alpha = 0.6f), fontSize = 12.sp)
                Text(
                    text = currentPhase.name.replace("_", " "),
                    color = GoldAccent,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Black
                )
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        // Proceed Button
        Button(
            onClick = onProceed,
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .border(2.dp, GoldAccent, RoundedCornerShape(30.dp)),
            shape = RoundedCornerShape(30.dp)
        ) {
            Text(
                "PROCEED TO ${getNextPhaseName(currentPhase)}",
                color = GoldAccent,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        }
    }
}

fun getNextPhaseName(current: UclPhase): String {
    return when(current) {
        UclPhase.LEAGUE -> "PLAY-OFFS"
        UclPhase.PLAYOFFS -> "ROUND OF 16"
        UclPhase.ROUND_OF_16 -> "QUARTER-FINALS"
        UclPhase.QUARTER_FINALS -> "SEMI-FINALS"
        UclPhase.SEMI_FINALS -> "FINAL"
        else -> "ARCHIVE"
    }
}
