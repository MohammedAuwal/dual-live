package com.duallive.app.ucl2026.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.duallive.app.ucl2026.logic.Ucl26BracketTie

@Composable
fun Ucl26BracketScreen(r16Ties: List<Ucl26BracketTie>) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NavyBackground)
            .padding(top = 16.dp)
    ) {
        Text(
            "KNOCKOUT BRACKET",
            color = GoldAccent,
            modifier = Modifier.padding(horizontal = 16.dp),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Row(
            modifier = Modifier
                .fillMaxSize()
                .horizontalScroll(scrollState)
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(32.dp)
        ) {
            BracketColumn("ROUND OF 16", r16Ties)
            BracketColumn("QUARTER-FINALS", List(4) { Ucl26BracketTie("QF", null, null) })
            BracketColumn("SEMI-FINALS", List(2) { Ucl26BracketTie("SF", null, null) })
            BracketColumn("FINAL", List(1) { Ucl26BracketTie("F", null, null) })
        }
    }
}

@Composable
fun BracketColumn(title: String, ties: List<Ucl26BracketTie>) {
    Column(
        verticalArrangement = Arrangement.SpaceAround,
        modifier = Modifier.fillMaxHeight().width(200.dp)
    ) {
        Text(title, color = GoldAccent, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        ties.forEach { tie ->
            BracketMatchCard(tie)
        }
    }
}

@Composable
fun BracketMatchCard(tie: Ucl26BracketTie) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(GlassWhite, RoundedCornerShape(8.dp))
            .padding(8.dp)
    ) {
        TeamLine(tie.teamAId?.toString() ?: "TBD")
        Divider(color = Color.White.copy(alpha = 0.1f), thickness = 1.dp)
        TeamLine(tie.teamBId?.toString() ?: "TBD")
    }
}

@Composable
fun TeamLine(name: String) {
    Row(
        modifier = Modifier.padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(Modifier.size(8.dp).background(GoldAccent, RoundedCornerShape(2.dp)))
        Spacer(Modifier.width(8.dp))
        Text(name, color = Color.White, fontSize = 14.sp)
    }
}
