package com.suitup.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.suitup.app.ui.icons.CheckIcon
import com.suitup.app.ui.icons.CloseIcon
import com.suitup.app.ui.theme.SuitColors
import com.suitup.app.ui.theme.SuitTextStyles

enum class TimelineItemStatus { Completed, Current, Pending, Rejected }

data class OrderTimelineItem(
    val label: String,
    val description: String? = null,
    val status: TimelineItemStatus,
)

@Composable
fun OrderTimeline(
    items: List<OrderTimelineItem>,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        items.forEachIndexed { index, item ->
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
                Column(
                    modifier = Modifier.width(28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    TimelineMarker(item.status)
                    if (index < items.lastIndex) {
                        Box(
                            modifier = Modifier
                                .width(2.dp)
                                .height(36.dp)
                                .background(
                                    if (item.status == TimelineItemStatus.Completed) {
                                        SuitColors.Success
                                    } else {
                                        SuitColors.Mist
                                    }
                                )
                        )
                    }
                }
                Spacer(Modifier.width(12.dp))
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(top = 2.dp, bottom = if (index < items.lastIndex) 16.dp else 0.dp),
                    verticalArrangement = Arrangement.spacedBy(3.dp),
                ) {
                    Text(item.label, style = SuitTextStyles.titleMedium, color = SuitColors.Pearl)
                    if (item.description != null) {
                        Text(item.description, style = SuitTextStyles.bodySmall, color = SuitColors.Slate)
                    }
                }
            }
        }
    }
}

@Composable
private fun TimelineMarker(status: TimelineItemStatus) {
    val background = when (status) {
        TimelineItemStatus.Completed -> SuitColors.Success
        TimelineItemStatus.Current -> SuitColors.GoldPrimary
        TimelineItemStatus.Pending -> SuitColors.WarmBlack
        TimelineItemStatus.Rejected -> SuitColors.Error
    }
    Box(
        modifier = Modifier
            .size(24.dp)
            .clip(CircleShape)
            .background(background)
            .border(1.dp, if (status == TimelineItemStatus.Pending) SuitColors.Mist else background, CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        when (status) {
            TimelineItemStatus.Completed -> CheckIcon(tint = SuitColors.InkBlack, size = 14.dp)
            TimelineItemStatus.Current -> Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(SuitColors.InkBlack)
            )
            TimelineItemStatus.Pending -> Unit
            TimelineItemStatus.Rejected -> CloseIcon(tint = SuitColors.Pearl, size = 13.dp)
        }
    }
}
