package com.duallive.app.ucl2026.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.duallive.app.ucl2026.logic.Ucl26PlayoffTie

@Composable
fun Ucl26PlayoffScreen(ties: List<Ucl26PlayoffTie>) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NavyBackground)
            .padding(16.dp)
    ) {
        Text(
            "KNOCKOUT PLAY-OFFS",
            color = GoldAccent,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            "AGGREGATE SCORE",
            color = Color.White.copy(alpha = 0.5f),
            fontSize = 12.sp
        )
        
        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(ties) { tie ->
                PlayoffTieCard(tie)
            }
        }
    }
}

@Composable
fun PlayoffTieCard(tie: Ucl26PlayoffTie) {
    val aggregateSeeded = tie.awayScoreLeg1 + tie.homeScoreLeg2
    val aggregateUnseeded = tie.homeScoreLeg1 + tie.awayScoreLeg2

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(GlassWhite, RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text("Team ${tie.unseededTeamId}", color = Color.White)
                Text("Team ${tie.seededTeamId}", color = Color.White, fontWeight = FontWeight.Bold)
            }
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("$aggregateUnseeded", color = GoldAccent, fontSize = 20.sp, fontWeight = FontWeight.Black)
                    Text("AGG", color = Color.Gray, fontSize = 10.sp)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("$aggregateSeeded", color = GoldAccent, fontSize = 20.sp, fontWeight = FontWeight.Black)
                    Text("AGG", color = Color.Gray, fontSize = 10.sp)
                }
            }
        }
    }
}
