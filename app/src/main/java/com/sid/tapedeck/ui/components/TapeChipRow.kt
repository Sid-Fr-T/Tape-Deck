package com.sid.tapedeck.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * TapeChipRow: Row with fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp),
 * and each chip uses Modifier.weight(1f) so all chips have equal width.
 * The first chip's left edge and the last chip's right edge sit exactly on the screen's 22dp content margins.
 * Leaves 5dp of bottom space under every chip row so the hard shadows are not clipped or touching the next element.
 */
@Composable
fun TapeChipRow(
    labels: List<String>,
    selected: String,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 22.dp)
            .padding(bottom = 5.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        labels.forEach { label ->
            TapeChip(
                text = label,
                isSelected = label.equals(selected, ignoreCase = true),
                onClick = { onSelect(label) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

