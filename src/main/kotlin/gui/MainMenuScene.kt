package gui

import service.RootService
import tools.aqua.bgw.components.uicomponents.*
import tools.aqua.bgw.core.MenuScene
import tools.aqua.bgw.util.Font
import tools.aqua.bgw.visual.ColorVisual
import java.awt.Color
import kotlin.system.exitProcess

class MainMenuScene(private val rootService: RootService):
    MenuScene(400,1080), Refreshables {

    private val headLineLabel = Label(
        width = 300,
        height = 50,
        posX = 50,
        posY = 50,
        text = "ENTER NAMES",
        font = Font(size = 30, color = Color.BLACK)
    )

    private val p1Label = Label(
        width =  100,
        height = 35,
        posX = 50,
        posY = 125,
        text = "Player 1:"
    )

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

    private val p2Label = Label(
        width =  100,
        height = 35,
        posX = 50,
        posY = 170,
        text = "Player 2:"
    )

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

    init {
        background = ColorVisual(108,168,59)
        opacity = .6
        addComponents(headLineLabel,
            p1Label, p2Label,
            p1Input, p2Input,
            startButton, exitButton)
    }
}