package gui

import service.RootService
import tools.aqua.bgw.core.BoardGameApplication

class DiveApplication: BoardGameApplication("DiveGame"), Refreshables {

    private val rootService = RootService()

    private val gameScene = GameScene(rootService)
    private val mainMenuScene = MainMenuScene(rootService)
    private val winningScreen = WinningScreen(rootService)

    init {
        rootService.addRefreshables(
            this,
            mainMenuScene,
            gameScene,
            winningScreen
        )
        rootService.gameService.startNewGame(listOf("Bob","Alice"))

        this.showGameScene(gameScene)
        this.showMenuScene(mainMenuScene, 0)
    }

    override fun refreshAfterGameStart(){
        hideMenuScene(500)
    }

    override fun refreshAfterGameEnds(){
        showMenuScene(winningScreen)
    }
}