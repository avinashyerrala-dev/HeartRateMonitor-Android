package com.health.heartratemonitor.presentation.history.details

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.health.heartratemonitor.presentation.utils.DateUtils.formatDate
import com.health.heartratemonitor.presentation.utils.DateUtils.formatDuration
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json.Default.decodeFromString
import java.util.Date
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.unit.Dp
import com.health.heartratemonitor.domain.model.ActivitySession
import com.health.heartratemonitor.presentation.components.MetricChip
import com.health.heartratemonitor.presentation.theme.Zone1Color
import com.health.heartratemonitor.presentation.theme.Zone2Color
import com.health.heartratemonitor.presentation.theme.Zone3Color
import com.health.heartratemonitor.presentation.theme.Zone4Color
import com.health.heartratemonitor.presentation.theme.Zone5Color
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun ActivityDetailsScreen(
    vm: ActivityDetailsViewModel = hiltViewModel()
) {
    val state by vm.uiState.collectAsState()
    state.session?.let { session ->

        // ---------- derived values ----------
        val zones = listOf(
            "Z1" to session.zone1TimeMillis,
            "Z2" to session.zone2TimeMillis,
            "Z3" to session.zone3TimeMillis,
            "Z4" to session.zone4TimeMillis,
            "Z5" to session.zone5TimeMillis
        )

        val hrPoints: List<Pair<Long, Int>> = remember(session.heartRateGraphJson) {
            decodeFromString(
                ListSerializer(HRPoint.serializer()),
                session.heartRateGraphJson
            ).map { it.timestamp to it.bpm }
        }

        // ---------- UI ----------
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {

            /* --- header -------------------------------------------------- */
            Text(
                text = session.activityType,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Text(formatDate(session.startTimestamp))

            /* --- summary chips ------------------------------------------ */
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
//                InfoChip("Duration", formatDuration(session.startTimestamp, session.endTimestamp))
//                InfoChip("Calories", "${session.caloriesBurned.toInt()} kcal")
//                InfoChip("HR", "${session.minHeartRate}-${session.maxHeartRate}")
                SummaryChipsRow(session)
            }

            /* --- zone bar chart ----------------------------------------- */
            Text("Time spent in HR zones", style = MaterialTheme.typography.titleMedium)
            ZoneBarChart(zones = zones)

            /* --- line chart --------------------------------------------- */
            Text("Heart‑rate progression", style = MaterialTheme.typography.titleMedium)
            HRLineChart(points = hrPoints)
        }
    }
}

/* ---------- helper composables -------------------------------------- */
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SummaryChipsRow(session: ActivitySession) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement   = Arrangement.spacedBy(12.dp)
    ) {
        MetricChip(
            icon       = Icons.Filled.AccessTime,
            valueText  = formatDuration(session.startTimestamp, session.endTimestamp)
        )
        MetricChip(
            icon       = Icons.Filled.LocalFireDepartment,
            valueText  = "${session.caloriesBurned.toInt()} kcal"
        )
        MetricChip(
            icon       = Icons.Filled.Favorite,
            valueText  = "${session.minHeartRate}-${session.maxHeartRate}"
        )
    }
}
//
//@Composable
//fun InfoChip(
//    label: String,
//    value: String,
//    modifier: Modifier = Modifier,
//    minWidth: Dp = 112.dp          // ensures the chip never collapses
//) {
//    Surface(
//        color  = MaterialTheme.colorScheme.primaryContainer,
//        shape  = RoundedCornerShape(32),
//        shadowElevation = 1.dp,
//        modifier = modifier
//            .height(40.dp)
//            .defaultMinSize(minWidth = minWidth)
//    ) {
//        Row(
//            modifier = Modifier
//                .padding(horizontal = 16.dp)
//                .fillMaxHeight(),
//            verticalAlignment = Alignment.CenterVertically,
//            horizontalArrangement = Arrangement.spacedBy(6.dp)
//        ) {
//            Text(
//                text  = label,
//                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Medium)
//            )
//            Text(
//                text  = value,
//                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
//            )
//        }
//    }
//}

/* --- very light‑weight bar chart ------------------------------------ */
/* zone‑colours from Colors.kt */
val zoneColors = listOf(Zone1Color, Zone2Color, Zone3Color, Zone4Color, Zone5Color)

/**
 * @param zones list in order Z1..Z5 (label, millis)
 * Shows each bar as (time / totalDuration) * fullWidth
 */
@Composable
fun ZoneBarChart(
    zones: List<Pair<String, Long>>,
    modifier: Modifier = Modifier
) {
    val totalDuration = zones.sumOf { it.second }.coerceAtLeast(1L)

    Column(modifier) {
        zones.forEachIndexed { idx, (label, millis) ->

            val fraction = millis / totalDuration.toFloat()
            val minutes  = (millis / 1000 / 60).toInt()

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                /* label column */
                Text(
                    text = label,
                    modifier = Modifier.width(28.dp),
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(Modifier.width(4.dp))

                /* bar column */
                Box(
                    Modifier
                        .weight(1f)
                        .height(12.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(
                            if (millis == 0L)
                                MaterialTheme.colorScheme.surfaceVariant
                            else
                                zoneColors[idx % zoneColors.size]
                        )
                ) {
                    if (millis != 0L) {
                        Box(
                            Modifier
                                .fillMaxWidth(fraction)
                                .matchParentSize()
                                .background(zoneColors[idx % zoneColors.size])
                        )
                    }
                }
                Spacer(Modifier.width(8.dp))

                /* time label */
                Text(
                    text  = "${minutes}m",
                    style = MaterialTheme.typography.bodySmall,
                )
            }
            Spacer(Modifier.height(8.dp))
        }
    }
}


/* --- simple line chart --------------------------------------------- */
@Composable
fun HRLineChart(
    points: List<Pair<Long, Int>>,
    modifier: Modifier = Modifier
        .fillMaxWidth()
        .height(220.dp)
) {
    if (points.size < 2) {
        Text("No HR data", modifier = modifier, textAlign = TextAlign.Center)
        return
    }
    val lineColor = MaterialTheme.colorScheme.primary
    val gridColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
    Canvas(modifier) {
        /* ---- math --------------------------------------------- */
        val minHr = points.minOf { it.second }
        val maxHr = points.maxOf { it.second }
        val hrRange = (maxHr - minHr).coerceAtLeast(1)

        val time0 = points.first().first
        val time1 = points.last().first
        val timeSpan = (time1 - time0).coerceAtLeast(1L)

        fun x(t: Long) = (t - time0) / timeSpan.toFloat() * size.width
        fun y(hr: Int) = size.height - (hr - minHr) / hrRange.toFloat() * size.height

        /* ---- axes & grid -------------------------------------- */
        val axisPaint = Paint().apply {
            color = gridColor
            strokeWidth = 1.dp.toPx()
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(6.dp.toPx(), 6.dp.toPx()))
        }
        val stepCount = 4
        for (i in 0..stepCount) {
            val yPos = size.height * i / stepCount
            // horizontal grid
            drawLine(
                color        = axisPaint.color,
                start        = Offset(0f, yPos),
                end          = Offset(size.width, yPos),
                strokeWidth  = 1.dp.toPx(),
                pathEffect   = PathEffect.dashPathEffect(floatArrayOf(6.dp.toPx(), 6.dp.toPx()))
            )
            // y‑axis labels (min‑mid‑max)
            val hrValue = minHr + (hrRange * (stepCount - i) / stepCount)
            drawContext.canvas.nativeCanvas.drawText(
                hrValue.toString(),
                0f,
                yPos - 4.dp.toPx(),
                android.graphics.Paint().apply {
                    color = axisPaint.color.toArgb()
                    textSize = 10.sp.toPx()
                }
            )
        }
        // x‑axis start & end labels
        val timeFormatter = SimpleDateFormat("mm:ss", Locale.getDefault())
        val labelPaint = android.graphics.Paint().apply {
            color = axisPaint.color.toArgb()
            textSize = 10.sp.toPx()
        }
        drawContext.canvas.nativeCanvas.drawText(
            timeFormatter.format(Date(time0)),
            0f,
            size.height + 14.dp.toPx(),
            labelPaint
        )
        drawContext.canvas.nativeCanvas.drawText(
            timeFormatter.format(Date(time1)),
            size.width - labelPaint.measureText(timeFormatter.format(Date(time1))),
            size.height + 14.dp.toPx(),
            labelPaint
        )

        /* ---- HR poly‑line ------------------------------------- */
        val path = Path()
        points.forEachIndexed { index, (t, hr) ->
            val pt = Offset(x(t), y(hr))
            if (index == 0) path.moveTo(pt.x, pt.y) else path.lineTo(pt.x, pt.y)
        }
        drawPath(
            path,
            color = lineColor,
            style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
        )
    }
}


@Serializable
data class HRPoint(val timestamp: Long, val bpm: Int)
