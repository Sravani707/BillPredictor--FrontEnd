package com.simats.billpredictor

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.simats.billpredictor.model.ExpenseItem
import com.simats.billpredictor.network.RetrofitClient
import com.simats.billpredictor.network.ExpenseApi
import com.simats.billpredictor.utils.NotificationHelper
import com.simats.billpredictor.utils.SmartReminderWorker
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {

    private val apiService: ExpenseApi by lazy { RetrofitClient.instance }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Request notification permission for Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) !=
                PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    101
                )
            }
        }

        // Schedule Daily Smart Check for Reminders
        val workRequest = PeriodicWorkRequestBuilder<SmartReminderWorker>(
            1, TimeUnit.DAYS
        ).build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "smart_reminder",
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )

        // TEMPORARY: Run worker immediately for testing
        val testWork = OneTimeWorkRequestBuilder<SmartReminderWorker>().build()
        WorkManager.getInstance(this).enqueue(testWork)

        // TEMPORARY: Test notification immediately
        NotificationHelper.showNotification(
            this,
            "AIMint Test",
            "If you see this, notifications work ✅"
        )

        setContent {
            val navController = rememberNavController()
            val userId = remember { mutableIntStateOf(-1) }
            val userName = remember { mutableStateOf("") }
            var currentScreen by remember { mutableStateOf<Screen>(Screen.Home) }

            val expenseViewModel: ExpenseViewModel = viewModel(
                factory = ExpenseViewModelFactory(apiService)
            )

            NavHost(
                navController = navController, 
                startDestination = "splash",
                enterTransition = { EnterTransition.None },
                exitTransition = { ExitTransition.None }
            ) {

                composable("splash") {
                    SplashScreen(onTimeout = {
                        navController.navigate("onboarding") {
                            popUpTo("splash") { inclusive = true }
                        }
                    })
                }

                composable("onboarding") {
                    OnboardingScreen(onContinueClicked = {
                        navController.navigate(Screen.Login.route) {
                            popUpTo("onboarding") { inclusive = true }
                        }
                    })
                }

                composable(Screen.Login.route) {
                    LoginScreen(
                        onBackClicked = { navController.popBackStack() },
                        onRegisterClicked = { navController.navigate(Screen.Register.route) },
                        onForgotPasswordClicked = { navController.navigate(Screen.ForgotPassword.route) },
                        onLoginSuccess = { name, id ->
                            userName.value = name
                            userId.intValue = id
                            navController.navigate(Screen.Home.route) {
                                popUpTo(Screen.Login.route) { inclusive = true }
                            }
                        }
                    )
                }

                composable(Screen.Register.route) {
                    RegisterScreen(
                        onLoginClicked = { navController.popBackStack() },
                        onRegisterSuccess = { navController.navigate(Screen.Login.route) }
                    )
                }

                composable(Screen.ForgotPassword.route) {
                    ForgotPasswordScreen(navController = navController)
                }

                composable(
                    route = Screen.Otp.route,
                    arguments = listOf(navArgument("email") { type = NavType.StringType })
                ) { backStackEntry ->
                    val email = backStackEntry.arguments?.getString("email") ?: ""
                    OtpScreen(navController = navController, email = email)
                }

                composable(
                    route = Screen.ResetPassword.route,
                    arguments = listOf(navArgument("email") { type = NavType.StringType })
                ) { backStackEntry ->
                    val email = backStackEntry.arguments?.getString("email") ?: ""
                    ResetPasswordScreen(navController = navController, email = email)
                }

                composable(Screen.Home.route) {
                    HomeScreen(
                        userName = userName.value,
                        userId = userId.intValue,
                        api = apiService,
                        viewModel = expenseViewModel,
                        onMonthSummaryClick = { navController.navigate(Screen.MonthSummary.route) },
                        onPredictClick = { },
                        onNavigate = { screen -> navController.navigate(screen.route) },
                        navController = navController
                    )
                }

                composable(Screen.History.route) {
                    HistoryScreen(
                        userId = userId.intValue,
                        api = apiService,
                        onBackClicked = { navController.popBackStack() },
                        currentScreen = Screen.History,
                        onNavigate = { screen -> navController.navigate(screen.route) }
                    )
                }

                composable(Screen.MonthSummary.route) {
                    MonthlySummaryScreen(
                        userId = userId.intValue,
                        api = apiService,
                        onBackClicked = { navController.popBackStack() },
                        currentScreen = Screen.MonthSummary,
                        onNavigate = { screen -> navController.navigate(screen.route) }
                    )
                }

                composable(Screen.AddExpense.route) {
                    val expenseItem = navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.get<ExpenseItem>("expenseItem")
                    AddExpenseScreen(
                        userId = userId.intValue,
                        expenseId = expenseItem?.realId,
                        initialAmount = expenseItem?.amount ?: "",
                        initialCategoryName = expenseItem?.categoryName ?: "",
                        viewModel = expenseViewModel,
                        onBackClicked = { navController.popBackStack() }
                    )
                }

                composable(Screen.AddIncome.route) {
                    AddIncomeScreen(
                        userId = userId.intValue,
                        api = apiService,
                        onBackClicked = { navController.popBackStack() }
                    )
                }

                composable(Screen.AddNotes.route) {
                    AddNotesScreen(
                        navController = navController,
                        expenseId = null,
                        viewModel = expenseViewModel
                    )
                }

                composable(Screen.Prediction.route) {
                    PredictionScreen(
                        userId = userId.intValue,
                        api = apiService,
                        viewModel = expenseViewModel,
                        onBackClicked = { navController.popBackStack() },
                        currentScreen = Screen.Prediction,
                        onNavigate = { screen -> navController.navigate(screen.route) }
                    )
                }

                composable(Screen.TrendingUp.route) {
                    TrendingUpScreen(
                        userId = userId.intValue,
                        viewModel = expenseViewModel,
                        onBackClicked = { navController.popBackStack() },
                        currentScreen = Screen.TrendingUp,
                        onNavigate = { screen -> navController.navigate(screen.route) }
                    )
                }

                composable(Screen.TrendingDown.route) {
                    TrendingDownScreen(
                        userId = userId.intValue,
                        viewModel = expenseViewModel,
                        onBackClicked = { navController.popBackStack() },
                        currentScreen = Screen.TrendingDown,
                        onNavigate = { screen -> navController.navigate(screen.route) }
                    )
                }

                composable(Screen.CalculationLogic.route) {
                    CalculationLogicScreen(
                        onBackClicked = { navController.popBackStack() },
                        currentScreen = currentScreen,
                        onNavigate = { screen ->
                            currentScreen = screen
                        }
                    )
                }

                composable(Screen.NewEvent.route) {
                    NewEventScreen(
                        userId = userId.intValue,
                        api = apiService,
                        viewModel = expenseViewModel,
                        onBackClicked = { navController.popBackStack() },
                        currentScreen = Screen.NewEvent,
                        onNavigate = { screen -> navController.navigate(screen.route) }
                    )
                }

                composable(Screen.EventSaved.route) {
                    EventSavedScreen(
                        onAddAnotherEvent = {
                            navController.navigate(Screen.NewEvent.route) {
                                popUpTo(Screen.EventSaved.route) { inclusive = true }
                            }
                        },
                        onViewAllEvents = { navController.navigate(Screen.SpecialEvents.route) },
                        onSeePrediction = { navController.navigate(Screen.Prediction.route) }
                    )
                }

                composable(Screen.SpecialEvents.route) {
                    SpecialEventsScreen(
                        navController = navController,
                        userId = userId.intValue,
                        api = apiService,
                        viewModel = expenseViewModel,
                        onBackClicked = { navController.popBackStack() },
                        onAddEventClicked = { navController.navigate(Screen.NewEvent.route) },
                        currentScreen = Screen.SpecialEvents,
                        onNavigate = { screen -> navController.navigate(screen.route) }
                    )
                }

                composable(
                    route = Screen.EventSavingsPlanner.route,
                    arguments = listOf(
                        navArgument("userId") { type = NavType.IntType },
                        navArgument("eventId") { type = NavType.IntType }
                    )
                ) { backStackEntry ->
                    val userIdArg = backStackEntry.arguments?.getInt("userId") ?: 0
                    val eventIdArg = backStackEntry.arguments?.getInt("eventId") ?: 0
                    EventSavingsPlannerScreen(
                        navController = navController,
                        userId = userIdArg,
                        eventId = eventIdArg,
                        viewModel = expenseViewModel
                    )
                }

                composable(Screen.Profile.route) {
                    ProfileScreen(
                        userName = userName.value,
                        onBackClicked = { navController.popBackStack() },
                        onHelpClicked = { navController.navigate(Screen.HelpAndFaq.route) },
                        onAboutClicked = { navController.navigate(Screen.About.route) },
                        onPrivacyPolicyClicked = { navController.navigate(Screen.PrivacyPolicy.route) },
                        onContactSupportClicked = { navController.navigate(Screen.ContactSupport.route) },
                        onLogoutClicked = {
                            userId.intValue = -1
                            userName.value = ""
                            navController.navigate(Screen.Login.route) {
                                popUpTo(Screen.Home.route) { inclusive = true }
                            }
                        },
                        currentScreen = Screen.Profile,
                        onNavigate = { screen -> navController.navigate(screen.route) }
                    )
                }

                composable(Screen.HelpAndFaq.route) {
                    HelpAndFaqScreen(onBackClicked = { navController.popBackStack() })
                }

                composable(Screen.About.route) {
                    AboutScreen(onBackClicked = { navController.popBackStack() })
                }

                composable(Screen.PrivacyPolicy.route) {
                    PrivacyPolicyScreen(onBackClicked = { navController.popBackStack() })
                }

                composable(Screen.ContactSupport.route) {
                    ContactSupportScreen(onBackClicked = { navController.popBackStack() })
                }
            }
        }
    }
}
