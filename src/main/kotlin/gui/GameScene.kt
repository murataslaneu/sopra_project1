package gui

import entity.*
import service.CardImageLoader
import service.RootService
import tools.aqua.bgw.components.container.Area
import tools.aqua.bgw.components.container.CardStack
import tools.aqua.bgw.components.container.LinearLayout
import tools.aqua.bgw.components.gamecomponentviews.CardView
import tools.aqua.bgw.components.uicomponents.Label
import tools.aqua.bgw.core.BoardGameScene
import java.awt.Color
import tools.aqua.bgw.util.Font
import tools.aqua.bgw.components.uicomponents.*
import tools.aqua.bgw.core.Alignment
import tools.aqua.bgw.util.BidirectionalMap
import tools.aqua.bgw.visual.ColorVisual
import java.util.*


class GameScene(private val rootService: RootService):
    BoardGameScene(1920, 1080), Refreshables {


    private val cardMap: BidirectionalMap<Card, CardView> = BidirectionalMap()

    //Zoom-Meeting empfehlung
    /*timeFieldButton.dropAcceptor = { dragEvent -> when (dragEvent.draggedComponent) {
        is Area <*> -> dragEvent.draggedComponent == FloatingArea else -> false } }*/

    private val discardPile = CardStack<CardView>(250, 300,
        width = 130,
        height = 200,
        alignment = Alignment.CENTER ).apply {
        visual = ColorVisual(255, 0, 0, 50)
        // A dropAcceptor function checks if the dragged card is valid to be played
        // and consumes the dragged element if true
        dropAcceptor = { dragEvent ->
            when (dragEvent.draggedComponent) {
                // If the dragged component is a CardView, the dropAcceptor checks if the card is valid
                is CardView -> {
                    // The card is looked up in the BidirectionalMap "cards" and checked
                    val card = cardMap.backward(dragEvent.draggedComponent as CardView)
                    // If the card is valid, the card can be dropped and played
                    // Otherwise, the card is not accepted and the dragEvent is consumed (dragged element is reset)
                    rootService.playerActionService.isValidTrio(card)
                }
                else -> false
            }
        }
        // The onDragDropped event handler plays the card if it is valid
        onDragDropped = { event ->
            // Get the CardView from the drag event and look up
            // the corresponding Card in the BidirectionalMap "cards"
            val cardView = event.draggedComponent as CardView
            val card = cardMap.backward(cardView)

            // Play the card
            rootService.playerActionService.discardCard(card)
        }
    }

    private val discardPileLabel = Label ( 250,270,130,30,
        text = "Discard Pile", font = Font(16, color = Color.BLACK)
    )
    
    private val drawPile = CardStack<CardView> (250,600,
        alignment = Alignment.CENTER  ).apply {
        visual = ColorVisual(255, 0, 255, 100)
        onMouseClicked = {
            if(!cardDrawnInCurrentTurn){
                rootService.playerActionService.drawCard()
            } else if (!startTurnButton.isDisabled) {
                throw IllegalArgumentException(" You didn't clicked on startTurn Button. ")
            }
            else {
                throw IllegalArgumentException(" You already drawn a 1 Card. ")
            }
        }
    }

    private val drawPileLabel = Label (250,570,130,30,
        text = "Draw Pile", font = Font(16, color = Color.BLACK)
    )


    private val dropArea = Area<CardView>(
        posX = 810,
        posY = 315,
        width = 400,
        height = 350,
    ).apply {
        // A dropAcceptor function checks if the dragged card is valid to be played
        // and consumes the dragged element if true
        dropAcceptor = { dragEvent ->
            when (dragEvent.draggedComponent) {
                // If the dragged component is a CardView, the dropAcceptor checks if the card is valid
                is CardView -> {
                    //val cardView = dragEvent.draggedComponent as CardView
                    //val card = cardMap.backward(cardView)

                    // The card is looked up in the BidirectionalMap "cards" and checked
                    val card = cardMap.backward(dragEvent.draggedComponent as CardView)

                    // If the card is valid, the card can be dropped and played
                    // Otherwise, the card is not accepted and the dragEvent is consumed (dragged element is reset)
                    rootService.playerActionService.isValidTrio(card)
                } else -> false
            }
        }

        // The onDragDropped event handler plays the card if it is valid
        onDragDropped = { event ->
            // Get the CardView from the drag event and look up
            // the corresponding Card in the BidirectionalMap "cards"
            val cardView = event.draggedComponent as CardView
            val card = cardMap.backward(cardView)

            // Play the card
            /*if(swappedCheckBox.isChecked && playStack.components.size in 1..2) {
                val playStackCard = cardMap.backward(playStack.components.last())
                checkSwap(card, playStackCard)
            } else { rootService.playerActionService.playCard(card) }*/

            rootService.playerActionService.playCard(card)
            // Überprüfung, ob Trio == 3 ist, damit wir es leeren können.
            trioCompleted()

        }
    }

    private val playStack = LinearLayout<CardView>(
        posX = 810,
        posY = 315,
        width = 400,
        height = 350,
        alignment = Alignment.CENTER
    )


    /** Player 1 **/
    private val player1Hand = LinearLayout<CardView>(
        width = 300,
        height = 50,
        posX = 895,
        posY = 800
    )

    private val player1DiscardPile = CardStack<CardView>(
        600, 830,
        width = 130,
        height = 200,
        alignment = Alignment.CENTER,
        visual = ColorVisual(255, 0, 0, 50)) // ok

    private val player1DiscardPileLabel = Label ( 600, 800, 162 ,30,
        alignment = Alignment.CENTER,
        text = "Player1 Discard Pile", font = Font(16, color = Color.BLACK) )

    /** Player 2 **/
    private val player2Hand = LinearLayout<CardView>(
        width = 300,
        height = 50,
        posX = 895,
        posY = 50
    )

    private val player2DiscardPile = CardStack<CardView>(600, 50,
        width = 130,
        height = 200,
        alignment = Alignment.CENTER,
        visual = ColorVisual(255, 0, 0, 50) )

    private val player2DiscardPileLabel = Label ( 600, 30, 162 ,30,
        alignment = Alignment.CENTER,
        text = "Player2 Discard Pile", font = Font(16, color = Color.BLACK) )

    /** rechte Seite (Namen & Score) **/
    private val currentPlayerLabel = Label(
        width = 300,
        height = 50,
        posX = 1500,
        posY = 100,
        font = Font(size = 24, color = Color.BLACK)
    )

    private val player1ScoreLabel = Label(
        width = 300,
        height = 50,
        posX = 1500,
        posY = 200,
        font = Font(size = 22, color = Color.RED)
    )

    private val player2ScoreLabel = Label(
        width = 300,
        height = 50,
        posX = 1500,
        posY = 300,
        font = Font(size = 22, color = Color.RED)
    )

    private val swappedCheckBox = CheckBox(
        width = 150,
        height = 50,
        posX = 1500,
        posY = 600,
        text = "Swapped"
    )

    private val endTurnButton = Button(
        width = 150,
        height = 50,
        posX = 1500,
        posY = 950,
        text = "End Turn"
    ).apply {
        onMouseClicked = {
            rootService.gameService.endTurn()
            startTurnButton.isDisabled = false
        }
    }

    private var turnStartClicked = false
    private val startTurnButton = Button(
        width = 150,
        height = 50,
        posX = 1500,
        posY = 850,
        text = "Start Turn"
    ).apply {
        onMouseClicked = {
            rootService.gameService.startTurn()
            cardDrawnInCurrentTurn = false
            turnStartClicked = true
            this.isDisabled = true

        }
    }

    //private val queue = LinkedBlockingQueue<BidirectionalMap<Card, CardView>>()
    init {
        background = ColorVisual(200,200,200)
        addComponents(player1Hand, player1DiscardPile,
            player2Hand, player2DiscardPile, discardPile, discardPileLabel, drawPile, drawPileLabel,
            currentPlayerLabel, player1ScoreLabel, player2ScoreLabel, player1DiscardPileLabel,
            player2DiscardPileLabel, swappedCheckBox, endTurnButton, startTurnButton,
            playStack, dropArea)

        swappedCheckBox.onMouseClicked = {
            if(swappedCheckBox.isChecked) {
                if(playStack.components.size in 1..2) {
                    playStack.components.forEach{ it.isDraggable = false }
                } else { throw IllegalArgumentException("Swap can be used only when there 1 or 2 cards") }
            } else { playStack.components.forEach{ it.isDraggable = true } }
        }
    }

    override fun refreshAfterGameStart() {
        val game = rootService.currentGame
        checkNotNull(game) {"No started game found."}

        val players = game.playerList
        if(players.size == 2) {
            player1ScoreLabel.text = "${players[0].name}'s Score: ${players[0].score}"
            player2ScoreLabel.text = "${players[1].name}'s Score: ${players[1].score}"
            currentPlayerLabel.text = "Current Player: ${game.currentPlayer.name}"
        }

        startTurnButton.isDisabled = true
        turnStartClicked = true

        //player1Hand.clear()
        //player2Hand.clear()

        val cardImageLoader = CardImageLoader()

        initializeStackView(game.drawStack, drawPile, cardImageLoader)

        initializeLinearView(game.playerList[0].hand, player1Hand,
            cardImageLoader, isCurrentPlayer(players[0]))
        initializeLinearView(game.playerList[1].hand, player2Hand,
            cardImageLoader, isCurrentPlayer(players[1]))

        player1DiscardPile.clear()
        player2DiscardPile.clear()
    }

    override fun refreshAfterTurnStart() {
        val game = rootService.currentGame
        checkNotNull(game) {"No started game found."}

        cardDrawnInCurrentTurn = false
        if(!turnStartClicked) { startTurnButton.isDisabled = false }
        currentPlayerLabel.text = game.currentPlayer.name

        //val players = game.playerList
        //val cardImageLoader = CardImageLoader()

        //initializeLinearView(players[0].hand, player1Hand,
        // cardImageLoader, isCurrentPlayer(players[0]))
        //initializeLinearView(players[1].hand, player2Hand,
        // cardImageLoader, isCurrentPlayer(players[1]))

        swapIsUsed = false // Swap used refresh
        swappedCheckBox.isDisabled = false
        swappedCheckBox.isChecked = false
    }

    private var cardDrawnInCurrentTurn = false
    override fun refreshAfterCardDrawn(card: Card) {
        val game = rootService.currentGame
        checkNotNull(game) {"No started game found."}

        val currentPlayer = game.currentPlayer
        val handView = if(currentPlayer == game.playerList[0]) player1Hand else player2Hand

        val cardView = (cardMap[card] as CardView).apply {
            applyHoverEffect(this)
        }
        handView.add(cardView)
        cardDrawnInCurrentTurn = true
        checkPlayerHandSize(currentPlayer)
    }

    private fun applyHoverEffect(cardView: CardView) {
        cardView.onMouseEntered = {
            cardView.posY -= 25
        }
        cardView.onMouseExited = {
            cardView.posY += 25
        }
        cardView.width = 130.0
        cardView.height = 200.0
        cardView.rotation = 0.0
        cardView.showFront()
        cardView.isDraggable = true
    }

    override fun refreshAfterTurnEnds() {
        val game = rootService.currentGame
        checkNotNull(game) {"No started game found."}

        if(startTurnButton.isDisabled) { startTurnButton.isDisabled = false }

    }

    override fun refreshAfterCardDiscarded(handCard: Card) {
        val game = rootService.currentGame
        checkNotNull(game) {"No started game found."}

        val cardView = (cardMap[handCard] as CardView).apply {
            onMouseEntered = null
            onMouseExited = null
            onMouseClicked = null
            isDraggable = false
            showBack()
        }

        val currentPlayer = game.currentPlayer
        val handView = if(currentPlayer == game.playerList[0]) player1Hand else player2Hand
        handView.remove(cardView)
        discardPile.push(cardView)

        checkPlayerHandSize(currentPlayer) // Unnötig?

    }

    override fun refreshAfterCardPlayed(playedCard: Card) {
        val game = rootService.currentGame
        checkNotNull(game) {"No started game found."}

        if(swappedCheckBox.isChecked) {
            throw IllegalArgumentException("Cant play a card while swap mode active")
        }

        val cardView = (cardMap[playedCard] as CardView).apply {
            onMouseEntered = null
            onMouseExited = null
            onMouseClicked = null
            isDraggable = false
            showFront()
        }
        // Remove the card from the playerHand and add it to the playStack
        val currentPlayer = game.currentPlayer
        val handView = if(currentPlayer == game.playerList[0]) player1Hand else player2Hand
        handView.remove(cardView)
        playStack.add(cardView)

        checkPlayerHandSize(currentPlayer)
    }

    private fun initializeStackView(stack: Stack<Card>, stackView: CardStack<CardView>, cardImageLoader: CardImageLoader) {
        stackView.clear()
        stack.forEach { card ->
            val cardView = CardView(
                height = 200,
                width = 130,
                front = cardImageLoader.frontImageFor(card!!.suit, card.value),
                back = cardImageLoader.backImage
            )
            stackView.add(cardView)
            cardMap.add(card to cardView)
        }
    }

    private fun isCurrentPlayer(player: Player): Boolean {
        val game = rootService.currentGame
        checkNotNull(game) {"No started game found."}
        return game.currentPlayer == player
    }

    private fun initializeLinearView(
        hand: MutableList<Card?>,
        handView: LinearLayout<CardView>,
        cardImageLoader: CardImageLoader,
        currentPlayer: Boolean) {
        handView.clear()
        hand.forEach { card ->
            card?.let {
            val cardView = CardView(
                height = 200,
                width = 130,
                front = cardImageLoader.frontImageFor(it.suit, it.value),
                back = cardImageLoader.backImage
            ).apply {
                isDraggable = currentPlayer
                if(currentPlayer) {
                    showFront()
                    onMouseEntered = { posY -= 25 }  // Hover effect
                    onMouseExited = { posY += 25 }
                }
            }
            handView.add(cardView)
            cardMap.add(card to cardView) }
        }
    }

    /*private fun moveCardView(cardView: CardView, toStack: CardStack<CardView>, flip: Boolean = false) {
        cardView.removeFromParent()
        if (flip) {
            when (cardView.currentSide) {
                CardView.CardSide.BACK -> cardView.showFront()
                CardView.CardSide.FRONT -> cardView.showBack()
            }
        }
       // cardView.removeFromParent()
        toStack.add(cardView)
    }*/

    private fun trioCompleted() {
        val game = rootService.currentGame
        checkNotNull(game) {"No started game found."}

        if(game.trio.size == 3){
            val currentPlayer = game.currentPlayer
            val playerDiscardPile = if(currentPlayer == game.playerList[0]) player1DiscardPile else player2DiscardPile

            playStack.components.forEach { cardView ->
                cardView.apply {
                    onMouseEntered = null
                    onMouseExited = null
                    onMouseClicked = null
                    isDraggable = false
                    showBack()
                }
                playerDiscardPile.push(cardView)
            }
            playStack.clear()
        }
    }

    private fun checkPlayerHandSize(currentPlayer: Player) {
        val game = rootService.currentGame
        val handSize = currentPlayer.hand.size
        endTurnButton.isVisible = handSize <= 8
        endTurnButton.isDisabled = handSize > 8

        val currentHand = if(game!!.currentPlayer == game.playerList[0]) player1Hand else player2Hand
        if(handSize > 8) {
            currentHand.forEach { cardView ->
                cardView.isDraggable = false //Falls es >8 Karten gibt, kann man nicht spielen
                cardView.onMouseClicked = {
                    val card = cardMap.backward(cardView)

                    discardPile.push(cardView.apply {
                        showBack()
                        isDraggable = false
                        onMouseEntered = null
                        onMouseExited = null
                    })
                    currentPlayer.hand.remove(card)
                    currentHand.remove(cardView)

                    checkPlayerHandSize(currentPlayer)
                }
            }
        }
    }


    private var swapIsUsed = false //Überprüfen, ob swap benutzt ist

    private fun checkSwap(playerCard: Card, playStackCard: Card) {
        val game = rootService.currentGame
        checkNotNull(game) {"No started game found."}

        val sameValue = playerCard.value == playStackCard.value
        val sameSuit = playerCard.suit == playStackCard.suit

        if(!sameSuit && !sameValue) {
            throw IllegalArgumentException("Swap only possible for cards with same value or suit")
        }

        val currentPlayer = game.currentPlayer
        val currentHand = if(currentPlayer == game.playerList[0]) player1Hand else player2Hand

        val playerCardView = cardMap[playerCard] as CardView
        val playStackCardView = cardMap[playStackCard] as CardView

        playStack.remove(playStackCardView)
        currentHand.remove(playerCardView)

        playStack.add(playerCardView.apply {
            isDraggable = false
            showFront()
        })
        currentHand.add(playerCardView.apply {
            isDraggable = true
            showFront()
            applyHoverEffect(this)
        })

        swappedCheckBox.isDisabled = true
        swapIsUsed = true
    }
}