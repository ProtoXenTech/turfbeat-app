package turfbeat.com.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import org.koin.compose.viewmodel.koinViewModel
import turfbeat.com.ui.screens.auth.ForgotPasswordScreen
import turfbeat.com.ui.screens.auth.RegistrationScreen
import turfbeat.com.ui.screens.auth.SignInScreen
import turfbeat.com.ui.screens.clubs.ClubDetailScreen
import turfbeat.com.ui.screens.clubs.ClubDirectoryScreen
import turfbeat.com.ui.screens.home.HomeScreen
import turfbeat.com.ui.screens.matches.MatchBoardScreen
import turfbeat.com.ui.screens.matches.MatchDetailScreen
import turfbeat.com.ui.screens.players.PlayerDetailScreen
import turfbeat.com.ui.screens.players.PlayerDirectoryScreen
import turfbeat.com.ui.screens.splash.SplashScreen
import turfbeat.com.ui.screens.venues.VenueDetailScreen
import turfbeat.com.ui.screens.venues.VenueDirectoryScreen
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
                    navController.navigate(Routes.SIGN_IN) { popUpTo(Routes.SPLASH) { inclusive = true } }
                },
                onNavigateToHome = {
                    navController.navigate(Routes.HOME) { popUpTo(Routes.SPLASH) { inclusive = true } }
                }
            )
        }

        composable(Routes.SIGN_IN) {
            val authViewModel: AuthViewModel = koinViewModel()
            SignInScreen(
                viewModel = authViewModel,
                onNavigateToHome = { navController.navigate(Routes.HOME) { popUpTo(Routes.SIGN_IN) { inclusive = true } } },
                onNavigateToSignUp = { navController.navigate(Routes.SIGN_UP) },
                onNavigateToForgotPassword = { navController.navigate(Routes.FORGOT_PASSWORD) }
            )
        }

        composable(Routes.SIGN_UP) {
            RegistrationScreen(
                onNavigateBack = { navController.popBackStack() },
                onRegistrationComplete = { navController.navigate(Routes.HOME) { popUpTo(Routes.SIGN_UP) { inclusive = true } } }
            )
        }

        composable(Routes.FORGOT_PASSWORD) {
            ForgotPasswordScreen(
                onNavigateBack = { navController.popBackStack() },
                onPasswordReset = { navController.navigate(Routes.SIGN_IN) { popUpTo(Routes.FORGOT_PASSWORD) { inclusive = true } } }
            )
        }

        composable(Routes.HOME) {
            val authViewModel: AuthViewModel = koinViewModel()
            val uiState by authViewModel.uiState.collectAsState()
            HomeScreen(
                userName = uiState.userName,
                onLogout = {
                    authViewModel.logout()
                    navController.navigate(Routes.SIGN_IN) { popUpTo(0) { inclusive = true } }
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

        composable(Routes.CLUB_DIRECTORY) {
            ClubDirectoryScreen(
                onNavigateBack = { navController.popBackStack() },
                onClubClick = { navController.navigate(Routes.clubDetail(it)) }
            )
        }

        composable(Routes.CLUB_DETAIL, arguments = listOf(navArgument("clubId") { type = NavType.IntType })) {
            val clubId = it.arguments?.getInt("clubId") ?: return@composable
            ClubDetailScreen(clubId = clubId, onNavigateBack = { navController.popBackStack() })
        }

        composable(Routes.VENUE_DIRECTORY) {
            VenueDirectoryScreen(
                onNavigateBack = { navController.popBackStack() },
                onVenueClick = { navController.navigate(Routes.venueDetail(it)) }
            )
        }

        composable(Routes.VENUE_DETAIL, arguments = listOf(navArgument("venueId") { type = NavType.IntType })) {
            val venueId = it.arguments?.getInt("venueId") ?: return@composable
            VenueDetailScreen(venueId = venueId, onNavigateBack = { navController.popBackStack() })
        }

        composable(Routes.PLAYER_DIRECTORY) {
            PlayerDirectoryScreen(
                onNavigateBack = { navController.popBackStack() },
                onPlayerClick = { navController.navigate(Routes.playerDetail(it)) }
            )
        }

        composable(Routes.PLAYER_DETAIL, arguments = listOf(navArgument("playerId") { type = NavType.IntType })) {
            val playerId = it.arguments?.getInt("playerId") ?: return@composable
            PlayerDetailScreen(playerId = playerId, onNavigateBack = { navController.popBackStack() })
        }

        composable(Routes.MATCH_BOARD) {
            MatchBoardScreen(
                onNavigateBack = { navController.popBackStack() },
                onMatchClick = { navController.navigate(Routes.matchDetail(it)) }
            )
        }

        composable(Routes.MATCH_DETAIL, arguments = listOf(navArgument("matchId") { type = NavType.IntType })) {
            val matchId = it.arguments?.getInt("matchId") ?: return@composable
            MatchDetailScreen(matchId = matchId, onNavigateBack = { navController.popBackStack() })
        }
    }
}
