package com.simats.billpredictor

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.billpredictor.ui.theme.BillpredictorTheme
import kotlinx.coroutines.delay

sealed class Screen {
    object Splash : Screen()
    object Onboarding : Screen()
    object FeatureOnboarding1 : Screen()
    object FeatureOnboarding2 : Screen()
    object FeatureOnboarding3 : Screen()
    object Register : Screen()
    object Login : Screen()
    object ForgotPassword : Screen()
    object Home : Screen()
    object MonthSummary : Screen()
    object Prediction : Screen()
    data class AddExpense(val selectedCategory: String? = null, val selectedDate: Long? = null, val note: String? = null) : Screen()
    object SelectCategory : Screen()
    object SelectDate : Screen()
    data class AddNote(val initialNote: String) : Screen()
    data class NewEvent(val selectedCategory: String? = null) : Screen()
    object SelectEventCategory : Screen()
    object EventSaved : Screen()
    object SpecialEvents : Screen()
    object TrendingUp : Screen()
    object TrendingDown : Screen()
    object ExpenseTrends : Screen()
    object MarchForecast : Screen()
    object CalculationLogic : Screen()
    object HelpAndFaq : Screen()
    object About : Screen()
    object PrivacyPolicy : Screen()
    object ContactSupport : Screen()
    object History : Screen()
    object Profile : Screen()
    object EditProfile : Screen()
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BillpredictorTheme {
                var currentScreen by remember { mutableStateOf<Screen>(Screen.Splash) }

                val navigateTo = { screen: Screen -> currentScreen = screen }

                when (val screen = currentScreen) {
                    is Screen.Splash -> SplashScreen { navigateTo(Screen.Onboarding) }
                    is Screen.Onboarding -> OnboardingScreen { navigateTo(Screen.FeatureOnboarding1) }
                    is Screen.FeatureOnboarding1 -> FeatureOnboardingScreen(
                        onNextClicked = { navigateTo(Screen.FeatureOnboarding2) },
                        onSkipClicked = { navigateTo(Screen.Register) }
                    )
                    is Screen.FeatureOnboarding2 -> PredictionOnboardingScreen(
                        onNextClicked = { navigateTo(Screen.FeatureOnboarding3) },
                        onSkipClicked = { navigateTo(Screen.Register) }
                    )
                    is Screen.FeatureOnboarding3 -> PlanningOnboardingScreen(
                        onGetStartedClicked = { navigateTo(Screen.Register) },
                        onSkipClicked = { navigateTo(Screen.Register) }
                    )
                    is Screen.Register -> RegisterScreen(
                        onBackClicked = { navigateTo(Screen.FeatureOnboarding3) },
                        onLoginClicked = { navigateTo(Screen.Login) },
                        onRegisterClicked = { navigateTo(Screen.Home) }
                    )
                    is Screen.Login -> LoginScreen(
                        onBackClicked = { navigateTo(Screen.Register) },
                        onRegisterClicked = { navigateTo(Screen.Register) },
                        onForgotPasswordClicked = { navigateTo(Screen.ForgotPassword) },
                        onLoginClicked = { navigateTo(Screen.Home) }
                    )
                    is Screen.ForgotPassword -> ForgotPasswordScreen { navigateTo(Screen.Login) }
                    is Screen.Home -> HomeScreen(
                        onSummaryClicked = { navigateTo(Screen.MonthSummary) },
                        onPredictClicked = { navigateTo(Screen.Prediction) },
                        onAddClicked = { navigateTo(Screen.AddExpense()) },
                        onTrendsClicked = { navigateTo(Screen.ExpenseTrends) },
                        onViewAllClicked = { navigateTo(Screen.History) },
                        currentScreen = screen,
                        onNavigate = navigateTo
                    )
                    is Screen.MonthSummary -> MonthSummaryScreen(
                        onBackClicked = { navigateTo(Screen.Home) },
                        onAddClicked = { navigateTo(Screen.AddExpense()) },
                        currentScreen = screen,
                        onNavigate = navigateTo
                    )
                    is Screen.Prediction -> PredictionScreen(
                        onBackClicked = { navigateTo(Screen.Home) },
                        onAddEventClicked = { navigateTo(Screen.NewEvent()) },
                        onTrendingUpClicked = { navigateTo(Screen.TrendingUp) },
                        onTrendingDownClicked = { navigateTo(Screen.TrendingDown) },
                        onMarchForecastClicked = { navigateTo(Screen.MarchForecast) },
                        onCalculationClicked = { navigateTo(Screen.CalculationLogic) },
                        onAddClicked = { navigateTo(Screen.AddExpense()) },
                        currentScreen = screen,
                        onNavigate = navigateTo
                    )
                    is Screen.AddExpense -> AddExpenseScreen(
                        onBackClicked = { navigateTo(Screen.Home) },
                        onCategoryClicked = { navigateTo(Screen.SelectCategory) },
                        onDateClicked = { navigateTo(Screen.SelectDate) },
                        onNotesClicked = { note -> navigateTo(Screen.AddNote(note)) },
                        currentScreen = screen,
                        onNavigate = navigateTo,
                        selectedCategory = screen.selectedCategory,
                        selectedDate = screen.selectedDate,
                        note = screen.note
                    )
                    is Screen.SelectCategory -> SelectCategoryScreen(
                        onBackClicked = { navigateTo(Screen.AddExpense()) },
                        onCategorySelected = { category -> navigateTo(Screen.AddExpense(category)) },
                        currentScreen = screen,
                        onNavigate = navigateTo
                    )
                    is Screen.SelectDate -> SelectDateScreen(
                        onBackClicked = { navigateTo(Screen.AddExpense()) },
                        onDateSelected = { date -> navigateTo(Screen.AddExpense(selectedDate = date)) },
                        currentScreen = screen,
                        onNavigate = navigateTo
                    )
                    is Screen.AddNote -> AddNotesScreen(
                        onBackClicked = { navigateTo(Screen.AddExpense()) },
                        onSaveNote = { note -> navigateTo(Screen.AddExpense(note = note)) },
                        initialNote = screen.initialNote,
                        currentScreen = screen,
                        onNavigate = navigateTo
                    )
                    is Screen.NewEvent -> NewEventScreen(
                        onBackClicked = { navigateTo(Screen.Prediction) },
                        onSaveEvent = { navigateTo(Screen.EventSaved) },
                        onCategoryClicked = { navigateTo(Screen.SelectEventCategory) },
                        selectedCategory = screen.selectedCategory,
                        currentScreen = screen,
                        onNavigate = navigateTo
                    )
                    is Screen.SelectEventCategory -> SelectEventCategoryScreen(
                        onBackClicked = { navigateTo(Screen.NewEvent()) },
                        onCategorySelected = { category -> navigateTo(Screen.NewEvent(category)) },
                        currentScreen = screen,
                        onNavigate = navigateTo
                    )
                    is Screen.EventSaved -> EventSavedScreen(
                        onAddAnotherEvent = { navigateTo(Screen.NewEvent()) },
                        onViewAllEvents = { navigateTo(Screen.SpecialEvents) },
                        onSeePrediction = { navigateTo(Screen.Prediction) }
                    )
                    is Screen.SpecialEvents -> SpecialEventsScreen(
                        onBackClicked = { navigateTo(Screen.EventSaved) },
                        onAddEventClicked = { navigateTo(Screen.NewEvent()) },
                        currentScreen = screen,
                        onNavigate = navigateTo
                    )
                    is Screen.TrendingUp -> TrendingUpScreen(
                        onBackClicked = { navigateTo(Screen.Prediction) },
                        currentScreen = screen,
                        onNavigate = navigateTo
                    )
                    is Screen.TrendingDown -> TrendingDownScreen(
                        onBackClicked = { navigateTo(Screen.Prediction) },
                        currentScreen = screen,
                        onNavigate = navigateTo
                    )
                    is Screen.ExpenseTrends -> ExpenseTrendsScreen(
                        onBackClicked = { navigateTo(Screen.Home) },
                        currentScreen = screen,
                        onNavigate = navigateTo
                    )
                    is Screen.MarchForecast -> MarchForecastScreen(
                        onBackClicked = { navigateTo(Screen.Prediction) },
                        currentScreen = screen,
                        onNavigate = navigateTo
                    )
                    is Screen.CalculationLogic -> CalculationLogicScreen(
                        onBackClicked = { navigateTo(Screen.Prediction) },
                        currentScreen = screen,
                        onNavigate = navigateTo
                    )
                    is Screen.History -> HistoryScreen(
                        onBackClicked = { navigateTo(Screen.Home) },
                        currentScreen = screen,
                        onNavigate = navigateTo
                    )
                    is Screen.Profile -> ProfileScreen(
                        onBackClicked = { navigateTo(Screen.Home) },
                        onEditProfileClicked = { navigateTo(Screen.EditProfile) },
                        onHelpClicked = { navigateTo(Screen.HelpAndFaq) },
                        onAboutClicked = { navigateTo(Screen.About) },
                        onPrivacyPolicyClicked = { navigateTo(Screen.PrivacyPolicy) },
                        onContactSupportClicked = { navigateTo(Screen.ContactSupport) },
                        onLogoutClicked = { navigateTo(Screen.Login) },
                        currentScreen = screen,
                        onNavigate = navigateTo
                    )
                    is Screen.EditProfile -> EditProfileScreen(
                        onBackClicked = { navigateTo(Screen.Profile) },
                        onSaveClicked = { navigateTo(Screen.Profile) },
                        currentScreen = screen,
                        onNavigate = navigateTo
                    )
                    is Screen.HelpAndFaq -> HelpAndFaqScreen(
                        onBackClicked = { navigateTo(Screen.Profile) },
                        currentScreen = screen,
                        onNavigate = navigateTo
                    )
                    is Screen.About -> AboutScreen(
                        onBackClicked = { navigateTo(Screen.Profile) },
                        currentScreen = screen,
                        onNavigate = navigateTo
                    )
                    is Screen.PrivacyPolicy -> PrivacyPolicyScreen(
                        onBackClicked = { navigateTo(Screen.Profile) },
                        currentScreen = screen,
                        onNavigate = navigateTo
                    )
                    is Screen.ContactSupport -> ContactSupportScreen(
                        onBackClicked = { navigateTo(Screen.Profile) },
                        currentScreen = screen,
                        onNavigate = navigateTo
                    )
                }
            }
        }
    }
}

@Composable
fun SplashScreen(onTimeout: () -> Unit) {
    var startAnimation by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0.8f,
        animationSpec = tween(durationMillis = 500)
    )
    val alpha by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = 500)
    )

    LaunchedEffect(Unit) {
        startAnimation = true
        delay(2000)
        onTimeout()
    }

    Surface(
        color = Color.White,
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.AccountBalanceWallet,
                contentDescription = "Wallet Icon",
                modifier = Modifier
                    .padding(bottom = 16.dp)
                    .scale(scale)
                    .alpha(alpha),
                tint = Color(0xFF4285F4)
            )
            Text(
                text = "ExpenseAI",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF4285F4),
                modifier = Modifier
                    .scale(scale)
                    .alpha(alpha)
            )
            Text(
                text = "Smart Expense Tracking",
                fontSize = 16.sp,
                modifier = Modifier
                    .scale(scale)
                    .alpha(alpha)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SplashScreenPreview() {
    BillpredictorTheme {
        SplashScreen {}
    }
}
