package com.example.poke

import androidx.activity.ComponentActivity
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.testing.TestNavHostController
import com.example.poke.data.*
import com.example.poke.ui.common.UiState
import com.example.poke.ui.navigation.Screen
import com.example.poke.ui.theme.PokeTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock

internal class MainActivityKtTest{
    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()
    private lateinit var navController: TestNavHostController

    @Mock
    private lateinit var repository: Repository

    fun setup(
        uiStatePokemons: StateFlow<UiState<GetPokemonsResponse>> = MutableStateFlow(UiState.Loading),
        uiStateFavoritePokemons: StateFlow<UiState<Set<FavoritePokemon>>> = MutableStateFlow(UiState.Loading),
        uiStateDetailPokemon: StateFlow<UiState<DetailPokemonResponse>> = MutableStateFlow(UiState.Loading),
        getAllPokemon: () -> Unit = {},
        getFavoritePokemon: () -> Unit = {},
        getDetail: (Int) -> Unit = {},
        addFavorite: (FavoritePokemon) -> Unit = {},
        removeFavorite: (FavoritePokemon) -> Unit = {},
        isFavoritePokemon: (FavoritePokemon) -> Boolean = {false},
    ){
        composeTestRule.setContent {
            PokeTheme {
                navController = TestNavHostController(LocalContext.current)
                navController.navigatorProvider.addNavigator(ComposeNavigator())
                PokeApp(
                    navController = navController,
                    uiStatePokemons = uiStatePokemons,
                    uiStateFavoritePokemons = uiStateFavoritePokemons,
                    uiStateDetailPokemon = uiStateDetailPokemon,
                    getAllPokemon = getAllPokemon,
                    getFavoritePokemon = getFavoritePokemon,
                    getDetail = getDetail,
                    addFavorite = addFavorite,
                    removeFavorite = removeFavorite,
                    isFavoritePokemon = isFavoritePokemon,
                )
            }
        }
    }

    @Test
    fun verify_startDestination(){
        setup()
        val currentRoute = navController.currentBackStackEntry?.destination?.route
        assertEquals(Screen.Home.route, currentRoute)
    }

    @Test
    fun verify_home_isEmpty() = runTest{
        val expected = DummyData.getEmpty()

        setup(
            uiStatePokemons = MutableStateFlow(UiState.Success(expected))
        )

        composeTestRule.onAllNodesWithTag("PokemonCard").assertCountEquals(0)
    }

    @Test
    fun verify_home_isNotEmpty(){
        val expected = DummyData.getAll()

        setup(
            uiStatePokemons = MutableStateFlow(UiState.Success(expected))
        )

        composeTestRule.onNodeWithTag("CardList").performScrollToIndex(DummyData.listSize - 1)
        composeTestRule.onAllNodesWithTag("PokemonCard").assertCountEquals(DummyData.listSize)
    }

    @Test
    fun verify_favorite_isEmpty(){
        val expectedRoute = Screen.Favorite.route
        setup()
        composeTestRule.onNodeWithContentDescription("favorite-Nav").performClick()

        val currentRoute = navController.currentBackStackEntry?.destination?.route
        assertEquals(expectedRoute, currentRoute)
        composeTestRule.onAllNodesWithTag("PokemonCard").assertCountEquals(0)
    }

    @Test
    fun verify_favorite_isNotEmpty(){
        val homeData = DummyData.getAll()
        val detailData = DummyData.getDetail()
        val initFavoriteData = mutableSetOf<FavoritePokemon>()
        val initFavoriteUiState = MutableStateFlow(UiState.Success(initFavoriteData))

        setup(
            uiStatePokemons = MutableStateFlow(UiState.Success(homeData)),
            uiStateDetailPokemon = MutableStateFlow(UiState.Success(detailData)),
            uiStateFavoritePokemons = initFavoriteUiState,
            addFavorite = {pokemon -> initFavoriteData.add(pokemon)},
            getFavoritePokemon = { initFavoriteUiState.value = UiState.Success(initFavoriteData) }
        )

        composeTestRule.onNodeWithText(DummyData.data[0].name).performClick()
        //check if current route is detail
        var currentRoute = navController.currentBackStackEntry?.destination?.route
        assertEquals(Screen.Detail.route, currentRoute)
        //add favorite
        composeTestRule.onNodeWithContentDescription("favorite-button").performClick()
        composeTestRule.onNodeWithContentDescription("nav-back").performClick()


        composeTestRule.onNodeWithContentDescription("favorite-Nav").performClick()
        currentRoute = navController.currentBackStackEntry?.destination?.route
        assertEquals(Screen.Favorite.route, currentRoute)
        composeTestRule.onAllNodesWithTag("PokemonCard").assertCountEquals(1)
    }

}