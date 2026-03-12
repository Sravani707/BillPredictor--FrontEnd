package com.simats.billpredictor

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.billpredictor.ui.theme.BillpredictorTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    userName: String,
    onBackClicked: () -> Unit,
    onHelpClicked: () -> Unit,
    onAboutClicked: () -> Unit,
    onPrivacyPolicyClicked: () -> Unit,
    onContactSupportClicked: () -> Unit,
    onLogoutClicked: () -> Unit,
    currentScreen: Screen,
    onNavigate: (Screen) -> Unit
) {
    var showLogoutConfirmDialog by remember { mutableStateOf(false) }
    var showLogoutSuccessDialog by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Profile", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClicked) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            BottomNavigationBar(
                currentScreen = currentScreen,
                onNavigate = onNavigate
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(padding)
                .verticalScroll(scrollState)
        ) {
            UserProfileHeader(userName = userName)
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                "Settings",
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Gray
            )

            ProfileRowItem(
                icon = Icons.Outlined.HelpOutline,
                text = "Help & FAQ",
                onClick = onHelpClicked
            )
            ProfileRowItem(
                icon = Icons.Outlined.Info,
                text = "About App",
                onClick = onAboutClicked
            )
            ProfileRowItem(
                icon = Icons.Outlined.Shield,
                text = "Privacy Policy",
                onClick = onPrivacyPolicyClicked
            )
            ProfileRowItem(
                icon = Icons.Outlined.Email,
                text = "Contact Support",
                onClick = onContactSupportClicked
            )
            
            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp),
                thickness = 0.5.dp,
                color = Color.LightGray.copy(alpha = 0.5f)
            )

            ProfileRowItem(
                icon = Icons.AutoMirrored.Filled.Logout,
                text = "Logout",
                textColor = Color.Red,
                iconColor = Color.Red,
                onClick = { showLogoutConfirmDialog = true }
            )
            
            Spacer(modifier = Modifier.height(32.dp))
        }

        if (showLogoutConfirmDialog) {
            AlertDialog(
                onDismissRequest = { showLogoutConfirmDialog = false },
                title = { Text("Logout") },
                text = { Text("Are you sure you want to logout?") },
                confirmButton = {
                    TextButton(onClick = {
                        showLogoutConfirmDialog = false
                        showLogoutSuccessDialog = true
                    }) {
                        Text("Logout", color = Color.Red)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showLogoutConfirmDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }

        if (showLogoutSuccessDialog) {
            AlertDialog(
                onDismissRequest = { },
                title = { Text("Logged Out") },
                text = { Text("Logged out successfully!") },
                confirmButton = {
                    TextButton(onClick = {
                        showLogoutSuccessDialog = false
                        onLogoutClicked()
                    }) {
                        Text("OK")
                    }
                }
            )
        }
    }
}

@Composable
fun UserProfileHeader(userName: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(Color(0xFFE3F2FD)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                if (userName.isNotEmpty()) userName.first().uppercase() else "U",
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF4285F4)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(userName, fontWeight = FontWeight.Bold, fontSize = 24.sp)
        Text("Account Settings", color = Color.Gray, fontSize = 14.sp)
    }
}

@Composable
fun ProfileRowItem(
    icon: ImageVector,
    text: String,
    textColor: Color = Color.Black,
    iconColor: Color = Color(0xFF4285F4),
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(iconColor.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                icon,
                contentDescription = text,
                tint = iconColor,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = text,
            modifier = Modifier.weight(1f),
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = textColor
        )
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = Color.LightGray,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    BillpredictorTheme {
        ProfileScreen(
            userName = "Sravs5",
            onBackClicked = {},
            onHelpClicked = {},
            onAboutClicked = {},
            onPrivacyPolicyClicked = {},
            onContactSupportClicked = {},
            onLogoutClicked = {},
            currentScreen = Screen.Profile,
            onNavigate = {}
        )
    }
}
