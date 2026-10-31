package com.sid.tapedeck.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import com.sid.tapedeck.ui.theme.TapeType
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.sid.tapedeck.ui.theme.TapeTheme
import com.sid.tapedeck.ui.theme.chunkyBorder
import com.sid.tapedeck.ui.theme.hardShadow

enum class TapeNavTab(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    Home("home", "HOME", TapeIcons.Home),
    Library("library", "LIBRARY", TapeIcons.Library),
    Search("search", "SEARCH", TapeIcons.Search),
    You("you", "YOU", TapeIcons.User)
}

/**
 * BottomNav: DOCKED to screen edge, full width, 84dp high, bg nav, 3dp top border.
 * 4 tabs: Home, Library, Search, You.
 * Active tab = pink pill (52dp high, icon + label, onPink);
 * inactive = icon above label in onNav, 12sp. NOT floating, no rounded container.
 */
@Composable
fun BottomNav(
    currentRoute: String,
    onTabSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = TapeTheme.colors

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(colors.nav)
            .drawBehind {
                // 3dp top border only
                drawLine(
                    color = colors.line,
                    start = Offset(0f, 0f),
                    end = Offset(size.width, 0f),
                    strokeWidth = 3.dp.toPx()
                )
            }
            .navigationBarsPadding()
            .height(84.dp)
            .padding(horizontal = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TapeNavTab.entries.forEach { tab ->
                val isActive = currentRoute == tab.route

                if (isActive) {
                    // Active tab: pink pill (52dp high, icon + label, onPink)
                    val pillShape = RoundedCornerShape(26.dp)
                    Box(
                        modifier = Modifier
                            .height(52.dp)
                            .hardShadow(color = colors.line, offsetY = 3.dp, shape = pillShape)
                            .background(colors.pink, pillShape)
                            .chunkyBorder(colors.line, width = 3.dp, shape = pillShape)
                            .padding(horizontal = 14.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = tab.icon,
                                contentDescription = tab.title,
                                tint = colors.onPink,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            TapeText(
                                text = tab.title,
                                style = TapeType.mono,
                                color = colors.onPink
                            )
                        }
                    }
                } else {
                    // Inactive tab: icon above label in onNav, 12sp
                    Column(
                        modifier = Modifier
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                onTabSelected(tab.route)
                            }
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = tab.icon,
                            contentDescription = tab.title,
                            tint = colors.onNav,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.height(3.dp))
                        TapeText(
                            text = tab.title,
                            style = TapeType.subtitle,
                            color = colors.onNav
                        )
                    }
                }
            }
        }
    }
}

