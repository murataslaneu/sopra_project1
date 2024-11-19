package gui

import service.RootService
import tools.aqua.bgw.core.MenuScene
import tools.aqua.bgw.components.uicomponents.*
import tools.aqua.bgw.core.Alignment
import tools.aqua.bgw.util.Font
import tools.aqua.bgw.visual.ImageVisual
import java.awt.Color
import kotlin.system.exitProcess

/**
 * [WinningScreen] represents the winning screen for the DiveGame
 * This scene is displayed at the end of the game, showing the players names and scores as a
 * winner or draw when scores are same, and an option to exit the Game.
 *
 * @property rootService The central [RootService] that provides access to game services and data.
 */
class WinningScreen(private val rootService: RootService):
    MenuScene(1920, 1080, background = ImageVisual("winner_screen.jpg")), Refreshables {

    //Label displaying the title "Score Table"
    private val resultLabel = Label(
        width = 300,
        height = 50,
        posX = 810,
        posY = 300,
        text = "Score Table:",
        font = Font(size = 30, color = Color.RED))

    //Label displaying the title "Player 1 Score"
    //Its updated at the end of the game
    private val p1ScoreLabel = Label(
        width = 300,
        height = 50,
        posX = 810,
        posY = 350,
        text = "Player 1 Score: ",
        font = Font(size = 24, color = Color.YELLOW)
    )

    //Label displaying the title "Player 2 Score"
    //Its updated at the end of the game
    private val p2ScoreLabel = Label(
        width = 300,
        height = 50,
        posX = 810,
        posY = 400,
        text = "Player 2 Score: ",
        font = Font(size = 24, color = Color.YELLOW)
    )

    //Label displaying the result of the game, showing the winner or indicating a draw.
    private val gameResult = Label(
        width = 400,
        height = 50,
        alignment = Alignment.CENTER,
        posX = 750,
        posY = 510,
        text = "",
        font = Font(size = 36, color = Color.BLUE)
    )

    //Button to exit the Game
    private val exitButton = Button(
        width = 200,
        height = 50,
        posX = 850,
        posY = 600,
        text = "EXIT GAME"
    ).apply {
        onMouseClicked = {
            exitProcess(0)
        }
    }

    //initializes the winning screen by adding all components.
    init {
        addComponents(resultLabel,
            p1ScoreLabel,
            p2ScoreLabel,
            gameResult,
            exitButton)
    }

    /**
     * Determines the result of the game.
     *
     * Compares the scores of the 2 players and returns string indicating the winner
     * or if the game resulted in a draw.
     *
     * @return String representing the game result
     */
    private fun winnerResult(): String {
        val game = rootService.currentGame
            val players = game!!.playerList
            return when {
                game.playerList[0].score - game.playerList[1].score > 0 -> "${players[0].name} wins the game."
                game.playerList[1].score - game.playerList[0].score > 0 -> "${players[1].name} wins the game."
                else -> "Draw. No winner!"
            }
    }

    /**
     * Updates the screen to display the scores and game result after the game ends.
     */
        override fun refreshAfterGameEnds() {
        val game = rootService.currentGame
        if(game != null) {
            val players = game.playerList
            p1ScoreLabel.text = "${players[0].name}'s Score: ${players[0].score}"
            p2ScoreLabel.text = "${players[1].name}'s Score: ${players[1].score}"

            gameResult.text = winnerResult()

            p1ScoreLabel.font = Font(size = 24, color = Color.BLACK)
            p2ScoreLabel.font = Font(size = 24, color = Color.BLACK)
        }
    }
}