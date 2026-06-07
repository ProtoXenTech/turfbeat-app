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
import turfbeat.com.ui.screens.chat.ChatScreen
import turfbeat.com.ui.screens.clubs.ClubDetailScreen
import turfbeat.com.ui.screens.clubs.ClubDirectoryScreen
import turfbeat.com.ui.screens.settings.AboutScreen
import turfbeat.com.ui.screens.settings.ContactScreen
import turfbeat.com.ui.screens.settings.HelpScreen
import turfbeat.com.ui.screens.settings.PrivacyScreen
import turfbeat.com.ui.screens.settings.SettingsScreen
import turfbeat.com.ui.screens.settings.TermsScreen
import turfbeat.com.ui.screens.dashboard.AdminDashboardScreen
import turfbeat.com.ui.screens.dashboard.ClubDashboardScreen
import turfbeat.com.ui.screens.dashboard.PlayerDashboardScreen
import turfbeat.com.ui.screens.dashboard.VenueDashboardScreen
import turfbeat.com.ui.screens.home.HomeScreen
import turfbeat.com.ui.screens.matches.MatchBoardScreen
import turfbeat.com.ui.screens.matches.MatchDetailScreen
import turfbeat.com.ui.screens.notifications.NotificationsScreen
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
                        "settings" -> navController.navigate(Routes.SETTINGS)
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

        composable(Routes.PLAYER_DASHBOARD) {
            PlayerDashboardScreen(
                onNavigateBack = { navController.popBackStack() },
                onClubClick = { navController.navigate(Routes.clubDetail(it)) },
                onMatchClick = { navController.navigate(Routes.matchDetail(it)) }
            )
        }

        composable(Routes.CLUB_DASHBOARD) {
            ClubDashboardScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(Routes.VENUE_DASHBOARD) {
            VenueDashboardScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(Routes.ADMIN_DASHBOARD) {
            AdminDashboardScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(Routes.MATCH_CHAT, arguments = listOf(navArgument("matchId") { type = NavType.IntType })) {
            val matchId = it.arguments?.getInt("matchId") ?: return@composable
            ChatScreen(matchId = matchId, onNavigateBack = { navController.popBackStack() })
        }

        composable(Routes.NOTIFICATIONS) {
            NotificationsScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(Routes.SETTINGS) {
            val authViewModel: AuthViewModel = koinViewModel()
            SettingsScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToAbout = { navController.navigate(Routes.ABOUT) },
                onNavigateToHelp = { navController.navigate(Routes.HELP) },
                onNavigateToContact = { navController.navigate(Routes.CONTACT) },
                onNavigateToTerms = { navController.navigate(Routes.TERMS) },
                onNavigateToPrivacy = { navController.navigate(Routes.PRIVACY) },
                onLogout = {
                    authViewModel.logout()
                    navController.navigate(Routes.SIGN_IN) { popUpTo(0) { inclusive = true } }
                }
            )
        }

        composable(Routes.ABOUT) { AboutScreen(onNavigateBack = { navController.popBackStack() }) }
        composable(Routes.HELP) { HelpScreen(onNavigateBack = { navController.popBackStack() }) }
        composable(Routes.CONTACT) { ContactScreen(onNavigateBack = { navController.popBackStack() }) }
        composable(Routes.TERMS) { TermsScreen(onNavigateBack = { navController.popBackStack() }) }
        composable(Routes.PRIVACY) { PrivacyScreen(onNavigateBack = { navController.popBackStack() }) }
    }
}
