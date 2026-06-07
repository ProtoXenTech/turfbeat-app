package turfbeat.com.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import org.koin.compose.viewmodel.koinViewModel
import turfbeat.com.ui.screens.auth.ForgotPasswordScreen
import turfbeat.com.ui.screens.auth.RegistrationScreen
import turfbeat.com.ui.screens.auth.SignInScreen
import turfbeat.com.ui.screens.home.HomeScreen
import turfbeat.com.ui.screens.splash.SplashScreen
import turfbeat.com.ui.viewmodel.AuthViewModel

@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String = Routes.SPLASH
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Routes.SPLASH) {
            val authViewModel: AuthViewModel = koinViewModel()
            val uiState by authViewModel.uiState.collectAsState()

            SplashScreen(
                isLoggedIn = uiState.isLoggedIn,
                onNavigateToSignIn = {
                    navController.navigate(Routes.SIGN_IN) {
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                },
                onNavigateToHome = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.SIGN_IN) {
            val authViewModel: AuthViewModel = koinViewModel()
            SignInScreen(
                viewModel = authViewModel,
                onNavigateToHome = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.SIGN_IN) { inclusive = true }
                    }
                },
                onNavigateToSignUp = {
                    navController.navigate(Routes.SIGN_UP)
                },
                onNavigateToForgotPassword = {
                    navController.navigate(Routes.FORGOT_PASSWORD)
                }
            )
        }

        composable(Routes.SIGN_UP) {
            RegistrationScreen(
                onNavigateBack = { navController.popBackStack() },
                onRegistrationComplete = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.SIGN_UP) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.FORGOT_PASSWORD) {
            ForgotPasswordScreen(
                onNavigateBack = { navController.popBackStack() },
                onPasswordReset = {
                    navController.navigate(Routes.SIGN_IN) {
                        popUpTo(Routes.FORGOT_PASSWORD) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.HOME) {
            val authViewModel: AuthViewModel = koinViewModel()
            val uiState by authViewModel.uiState.collectAsState()

            HomeScreen(
                userName = uiState.userName,
                onLogout = {
                    authViewModel.logout()
                    navController.navigate(Routes.SIGN_IN) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onNavigate = { route ->
                    when (route) {
                        "clubs" -> navController.navigate(Routes.CLUB_DIRECTORY)
                        "players" -> navController.navigate(Routes.PLAYER_DIRECTORY)
                        "matches" -> navController.navigate(Routes.MATCH_BOARD)
                        "venues" -> navController.navigate(Routes.VENUE_DIRECTORY)
                        "player_dashboard" -> navController.navigate(Routes.PLAYER_DASHBOARD)
                        "club_dashboard" -> navController.navigate(Routes.CLUB_DASHBOARD)
                    }
                }
            )
        }
    }
}
