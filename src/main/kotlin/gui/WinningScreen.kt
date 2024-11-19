package gui

import service.RootService
import tools.aqua.bgw.core.MenuScene
import tools.aqua.bgw.components.uicomponents.*
import tools.aqua.bgw.util.Font
import java.awt.Color
import kotlin.system.exitProcess

class WinningScreen(private val rootService: RootService):
    MenuScene(1920, 1080), Refreshables {

    private val resultLabel = Label(
        width = 300,
        height = 50,
        posX = 810,
        posY = 100,
        text = "Score",
        font = Font(size = 30, color = Color.WHITE)
    )

    private val p1ScoreLabel = Label(
        width = 300,
        height = 50,
        posX = 810,
        posY = 200,
        text = "Player 1 Score: ",
        font = Font(size = 24, color = Color.YELLOW)
    )

    private val p2ScoreLabel = Label(
        width = 300,
        height = 50,
        posX = 810,
        posY = 270,
        text = "Player 2 Score: ",
        font = Font(size = 24, color = Color.YELLOW)
    )

    private val newGameButton = Button(
        width = 150,
        height = 50,
        posX = 810,
        posY = 400,
        text = "New Game"
    ).apply {
        onMouseClicked = {
            rootService.gameService.startNewGame(listOf("Player 1", "Player 2"))
        }
    }

    private val exitButton = Button(
        width = 200,
        height = 50,
        posX = 1020,
        posY = 400,
    ).apply {
        onMouseClicked = {
            exitProcess(0)
        }
    }

    init {
        addComponents(resultLabel, p1ScoreLabel, p2ScoreLabel,
            newGameButton, exitButton)
    }

    override fun refreshAfterGameEnds() {
        val game = rootService.currentGame
        if(game != null) {
            val players = game.playerList
            p1ScoreLabel.text = "${players[0].name} Score: ${players[0].score}"
            p2ScoreLabel.text = "${players[1].name} Score: ${players[1].score}"

            p1ScoreLabel.font = Font(size = 24, color = Color.BLACK)
            p2ScoreLabel.font = Font(size = 24, color = Color.BLACK)
        }
    }
}