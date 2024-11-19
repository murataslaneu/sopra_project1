package gui

import service.RootService
import tools.aqua.bgw.components.uicomponents.*
import tools.aqua.bgw.core.MenuScene
import tools.aqua.bgw.util.Font
import tools.aqua.bgw.visual.ColorVisual
import java.awt.Color
import kotlin.system.exitProcess

/**
 * [MainMenuScene] Represents the main menu scene for the "DiveGame"
 *
 * This scene allows players to enter their names, start a new game or exit the game.
 * It validates the input to ensure both players have unique, non-empty names before enabling the start button.
 *
 * @property rootService The central [RootService] that provides access to game services and data.
 */
class MainMenuScene(private val rootService: RootService):
    MenuScene(400,1080), Refreshables {

    //Label displaying the title of the scene: "DIVE GAME"
    private val headLineLabel = Label(
        width = 300,
        height = 50,
        posX = 50,
        posY = 50,
        text = "DIVE GAME",
        font = Font(size = 30, color = Color.BLACK))

    //Label for player 1's name.
    private val p1Label = Label(
        width =  100,
        height = 35,
        posX = 50,
        posY = 125,
        text = "Player 1:"
    )

    //Text Field for player 1's name input.
    //Ensures that the "Start" button is disabled if the input is empty or matches Player 2's name.
    private val p1Input: TextField = TextField(
        width =  200,
        height = 35,
        posX = 150,
        posY = 125
    ).apply {
        onKeyTyped = {
            startButton.isDisabled = this.text.isBlank() || p2Input.text.isBlank() || this.text == p2Input.text
        }
    }

    //Label for player 2's name.
    private val p2Label = Label(
        width =  100,
        height = 35,
        posX = 50,
        posY = 170,
        text = "Player 2:"
    )

    //Text Field for player 2's name input.
    //Ensures that the "Start" button is disabled if the input is empty or matches Player 1's name.
    private val p2Input: TextField = TextField(
        width =  200,
        height = 35,
        posX = 150,
        posY = 170
    ).apply {
        onKeyTyped = {
            startButton.isDisabled = this.text.isBlank() || p1Input.text.isBlank() || this.text == p1Input.text
        }
    }

    //Button to start a new game.
    //The button is initially disabled and is enabled only when valid input is provided.
    private val startButton = Button(
        width = 140,
        height = 35,
        posX = 50,
        posY = 240,
        text = "Start"
    ).apply {
        visual = ColorVisual(136,221,136)
        onMouseClicked = {
            rootService.gameService.startNewGame(
                listOf(p1Input.text.trim(), p2Input.text.trim()))
        }
    }

    //Button to exit the Game
    private val exitButton = Button(
        width = 140,
        height = 35,
        posX = 210,
        posY = 240,
        text = "Exit"
    ).apply {
        visual = ColorVisual(221,136,136)
        onMouseClicked = {
            exitProcess(0)
        }
    }

    //Initializes the main menu scene.
    // Sets the background color and opacity, and adds all components to the scene.
    init {
        background = ColorVisual(108,168,59)
        opacity = .6
        addComponents(headLineLabel,
            p1Label, p2Label,
            p1Input, p2Input,
            startButton, exitButton)
    }
}