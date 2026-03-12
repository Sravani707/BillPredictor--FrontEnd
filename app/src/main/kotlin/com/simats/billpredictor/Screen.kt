package com.simats.billpredictor

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Home : Screen("home")
    object History : Screen("history")
    object Prediction : Screen("prediction")
    object Profile : Screen("profile")
    object Trends : Screen("trends")
    object MonthSummary : Screen("month_summary")
    
    object AddExpense : Screen("add_expense?expenseId={expenseId}") {
        fun createRoute(expenseId: Int? = null) = if (expenseId != null) "add_expense?expenseId=$expenseId" else "add_expense"
    }
    
    object AddNotes : Screen("add_notes")
    object Register : Screen("register")
    object Login : Screen("login")
    object ForgotPassword : Screen("forgot_password")
    
    object Otp : Screen("otp_screen/{email}") {
        fun createRoute(email: String) = "otp_screen/$email"
    }
    
    object ResetPassword : Screen("reset_password/{email}") {
        fun createRoute(email: String) = "reset_password/$email"
    }
    
    object AddIncome : Screen("add_income")
    object NewEvent : Screen("new_event")
    object EventSaved : Screen("event_saved")
    object SpecialEvents : Screen("special_events")
    
    object EventSavingsPlanner : Screen("event_savings_planner/{userId}/{eventId}") {
        fun createRoute(userId: Int, eventId: Int): String {
            return "event_savings_planner/$userId/$eventId"
        }
    }
    
    object CalculationLogic : Screen("calculation_logic")
    object TrendingUp : Screen("trending_up")
    object TrendingDown : Screen("trending_down")
    object HelpAndFaq : Screen("help_and_faq")
    object About : Screen("about")
    object PrivacyPolicy : Screen("privacy_policy")
    object ContactSupport : Screen("contact_support")
}
