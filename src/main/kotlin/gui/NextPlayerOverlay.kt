package gui

import service.RootService
import tools.aqua.bgw.components.uicomponents.Button
import tools.aqua.bgw.components.uicomponents.Label
import tools.aqua.bgw.core.Alignment
import tools.aqua.bgw.core.MenuScene
import tools.aqua.bgw.visual.ColorVisual
import tools.aqua.bgw.util.Font

/**
 * [NextPlayerOverlay] Represents the overlay scene displayed between turns in the "DiveGame"
 *
 * The purpose of this overlay is to hide the game screen and provide instructions
 * for handing the device to the next player. It ensures that players cannot see each other's cards.
 * The overlay contains a message for the current player and a button to proceed to the next turn.
 *
 * @property rootService The central [RootService] that provides access to game services and data.
 */
class NextPlayerOverlay(private val rootService: RootService) :
    MenuScene(1080, 600, ColorVisual(0,0,0,180)), Refreshables{

    //Label displaying instructions to the player.
    private val messageLabel = Label(
        text = "Please hand the device to current Player.",
        posX = 240,
        posY = 180,
        width = 600,
        height = 120,
        alignment = Alignment.CENTER,
        font = Font(12),
        visual = ColorVisual(222, 221, 222))

    //The button, that players click to start they turn.
    private val continueButton = Button(
        text = "Start Turn",
        posX = 415,
        posY = 390,
        width = 250,
        height = 100,
        font = Font(14),
        alignment = Alignment.CENTER,
        visual = ColorVisual(136, 221, 136)
    ).apply { onMouseClicked = { rootService.gameService.startTurn() } }

    //Initializes the overlay by setting its opacity and adding the label and button components.
    init {
        opacity = .5
        addComponents(messageLabel, continueButton)
    }

    /**
     * Updates the message displayed on the overlay to indicate whose turn is next.
     *
     * If no game is active, an exception os thrown.
     */
    fun currentPlayerNameText() {
        val game = rootService.currentGame
        checkNotNull(game) { "No started game found." }

        val currentPlayer = if(game.currentPlayer == game.playerList[0]) game.playerList[0].name else game.playerList[1].name

        val currentPlayerName = "Now it's $currentPlayer's turn. Please hand the device to $currentPlayer."
        messageLabel.text = currentPlayerName
    }
}