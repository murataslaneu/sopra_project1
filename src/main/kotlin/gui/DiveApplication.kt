package gui

import service.RootService
import tools.aqua.bgw.core.BoardGameApplication

/**
 * Implementation of the BGW [BoardGameApplication] for the example card game "DiveGame"
 */
class DiveApplication: BoardGameApplication("DiveGame"), Refreshables {

    // Central service from which all others are created/accessed
    // also holds the currently active game
    private val rootService = RootService()

    // All Scenes used in the game:

    // This is where the actual game takes place
    private val gameScene = GameScene(rootService)

    // This menu scene is shown after application start and if the "new game" button
    //is clicked, starts the new game.
    //After that came the nextPlayerOverlay with button "Start Turn"
    private val mainMenuScene = MainMenuScene(rootService)

    // This menu scene is shown after finished game (i.e. draw pile is empty)
    private val winningScreen = WinningScreen(rootService)

    //Overlay, that hide the cards from each other (players)
    private val nextPlayerOverlay = NextPlayerOverlay(rootService)

    // all scenes and the application itself need too
    // reacted to changes done in the service layer
    init {
        rootService.addRefreshables(
            this,
            mainMenuScene,
            gameScene,
            winningScreen,
            nextPlayerOverlay
        )

        // Set the initial scene to the main menu
        this.showGameScene(gameScene)
        this.showMenuScene(mainMenuScene, 0)
    }

    /**
     * The refreshAfterGameStart method is called by the service layer after a game has started.
     * It hides the menu scene after a short delay. And that came the Overlay for current player.
     */
    override fun refreshAfterGameStart(){
        hideMenuScene(500)
        showMenuScene(nextPlayerOverlay)
        nextPlayerOverlay.currentPlayerNameText()
    }

    /**
     * The refreshAfterTurnStart method refreshes the gui when a player's turn starts.
     * Hides the overlay and displays the main game scene for gameplay.
     */
    override fun refreshAfterTurnStart() {
        hideMenuScene()
        showGameScene(gameScene)
    }

    /**
     * The refreshAfterTurnEnds method refreshes the gui when a player's turn ends.
     * Hides the overlay and displays the overlay for next player.
     */
    override fun refreshAfterTurnEnds() {
        hideMenuScene()
        showMenuScene(nextPlayerOverlay)
        nextPlayerOverlay.currentPlayerNameText()
    }

    /**
     * The refreshAfterGameEnd method is called by the service layer after a game has ended.
     * It shows the winning Screen with player names and scores.
     */
    override fun refreshAfterGameEnds(){
        showMenuScene(winningScreen)
    }
}