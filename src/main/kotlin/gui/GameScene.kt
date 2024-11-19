package gui

import entity.*
import service.CardImageLoader
import service.RootService
import tools.aqua.bgw.components.container.CardStack
import tools.aqua.bgw.components.container.GameComponentContainer
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
import tools.aqua.bgw.visual.ImageVisual

/**
 * The [GameScene] class is a BoardGameScene that displays the game board and all game components.
 *
 * @property rootService The associated [RootService]
 */
class GameScene(private val rootService: RootService):
    BoardGameScene(1920, 1080, background = ImageVisual("casino_back.jpg")), Refreshables {

                                        /** All UI Components **/

    private val endGame = Button(50, 75,250,50,
        text = "Don't wanna Play ? -> END GAME",).apply {
            onMouseClicked = { rootService.gameService.isGameEnded() } }

    /** Player 1 Components **/
    private val player1Hand = LinearLayout<CardView>(895,800,300,50)

    private val player1DiscardPile = CardStack<CardView>(600, 830, 130, 200,
        Alignment.CENTER, ColorVisual(255, 255, 255, 50))

    private val player1DiscardPileLabel = Label ( 600, 800, 162 ,30,
        alignment = Alignment.CENTER, text = "Player's Discard Pile",
        font = Font(16, color = Color.BLACK) )

    /** Player 2 Components **/
    private val player2Hand = LinearLayout<CardView>(895,50,300,50)

    private val player2DiscardPile = CardStack<CardView>(600, 60, 130, 200,
        alignment = Alignment.CENTER,
        visual = ColorVisual(255, 255, 255, 50))

    private val player2DiscardPileLabel = Label ( 600, 30, 162 ,30,
        alignment = Alignment.CENTER,
        text = "Player's Discard Pile", font = Font(16, color = Color.BLACK) )

    /** right side (Player Names & Score & Buttons) **/
    private val currentPlayerLabel = Label(1500,100,300,50, font = Font(size = 30, color = Color.BLACK))

    private val player1ScoreLabel = Label(1500,200,300,50, font = Font(size = 26, color = Color.RED))

    private val player2ScoreLabel = Label(1500,300,300,50, font = Font(size = 26, color = Color.RED))

    //Player can change the cards one from the hand and one from play area with clicking on swap Button.
    private val swapButton = Button(1500, 600,150,50, text = "Swap").apply {
        onMouseClicked = {
            val playerCard = cardMap.backward(clickedPlayerCard!!)
            val playAreaCard = cardMap.backward(clickedPlayAreaCard!!)
            rootService.playerActionService.swapCard(playerCard, playAreaCard)
            endTurnButton.isDisabled = false
            playCardButton.isDisabled = true
            swappedCheckBox.isChecked = true }  }

    private val swappedCheckBox = CheckBox(1700,600,50,50, text = "Swapped Action",
        font = Font(size = 24, color = Color.BLACK))

    //Turn is ended when player clicked on end Turn button.
    private val endTurnButton = Button(1500, 950,150,50, text = "End Turn").apply {
        onMouseClicked = { rootService.gameService.endTurn() } }

    //Card is played (from hand to play Area) when player clicked on play Card button.
    private val playCardButton = Button(1500, 890,150,50, text = "Play Card").apply {
        onMouseClicked = { val playerCard = cardMap.backward(clickedPlayerCard!!)
        rootService.playerActionService.playCard(playerCard) }    }

    //Player can discard one Card only when hand size is more than 8 and end Turn button is disabled.
    private val discardCardButton = Button(1500, 540,150,50, text = "Discard Card").apply {
        onMouseClicked = { rootService.gameService.endTurn() } }.apply { onMouseClicked = {
        if(rootService.currentGame!!.currentPlayer.hand.size > 8) {
            val playerCard = cardMap.backward(clickedPlayerCard!!)
            rootService.playerActionService.discardCard(playerCard)
            endTurnButton.isDisabled = false
        } else {throw IllegalArgumentException("You can discard Card only when you have more than 8 Cards in Hand")} } }

    /** Play Area as Linear layout **/
    private val playArea = LinearLayout<CardView>(810,315,400,350, alignment = Alignment.CENTER)

    /** Draw Stack Stack **/
    private val drawPileLabel = Label (250,570,130,30,
        text = "Draw Pile", font = Font(16, color = Color.BLACK))

    //Player can click on the Button and take one card from draw Pile
    private val drawPile = CardStack<CardView> (250,600, alignment = Alignment.CENTER  ).apply {
        visual = ColorVisual(255, 255, 255, 50)
        onMouseClicked = {
            if(!cardDrawnInCurrentTurn){
                rootService.playerActionService.drawCard()
            } else { throw IllegalArgumentException(" You already drawn a 1 Card. ") }
        }
    }

    /** Discard Stack **/
    private val discardPile = CardStack<CardView>(250, 300,130,200,
        visual = ColorVisual(255, 255, 255, 50), alignment = Alignment.CENTER)

    private val discardPileLabel = Label ( 250,270,130,30,
        text = "Discard Pile", font = Font(16, color = Color.BLACK))

    // BidirectionalMap is a data structure that allows to map keys to values in both directions
    // In this case, it is used to map Card objects to CardView objects
    private val cardMap: BidirectionalMap<Card, CardView> = BidirectionalMap()

    private var clickedPlayerCard: CardView? = null

    private var clickedPlayAreaCard: CardView? = null

    //Initializes the game scene.
    // Adds all components to the scene.
    init {
        addComponents(
            player1Hand,
            player1DiscardPile,
            player1DiscardPileLabel,
            player2Hand,
            player2DiscardPile,
            player2DiscardPileLabel,
            currentPlayerLabel,
            player1ScoreLabel,
            player2ScoreLabel,
            discardCardButton,
            swapButton,
            swappedCheckBox,
            playCardButton,
            endTurnButton,
            playArea,
            drawPileLabel,
            drawPile,
            discardPile,
            discardPileLabel,
            endGame)
    }

    /**
     * Initializes the complete GUI, i.e. the draw stack with cards, discard stack as empty,
     * player hands with 5 cards.
     *
     * Activated with button start Game on [MainMenuScene]
     *
     * @throws IllegalStateException if no game has started.
     */
    override fun refreshAfterGameStart() {
        val game = rootService.currentGame
        checkNotNull(game) {"No started game found."}

        cardMap.clear()
        val cardImageLoader = CardImageLoader()

        //Initialize stack view for draw Stack, discard Stack
        initializeStackView(game.drawStack, drawPile, cardImageLoader)
        initializeStackView(game.discardStack, discardPile, cardImageLoader)

        //initialize Linear view for trio (play area)
        initializeLinearView(game.trio, playArea, cardImageLoader, true)

        val players = game.playerList
        val secondPlayer = if(game.currentPlayer == players[0]) players[1] else players[0]

        //initialize Linear view for player Hands. For current player cards are visible
        //for second player cards are hidden.
        initializeLinearView(game.currentPlayer.hand, player1Hand, cardImageLoader, true)
        initializeLinearView(secondPlayer.hand, player2Hand, cardImageLoader, false)

        //Make the cards clickable to each card in the players hands.
        //For both player hand. this function iterates through all card views and make them clickable.
        //When a card is clicked, it's stored in the "clickedPlayerCard" variable.
        for (cardView in player1Hand) { cardView.onMouseClicked = { clickedPlayerCard = cardView } }
        for (cardView in player2Hand) { cardView.onMouseClicked = { clickedPlayerCard = cardView } }

        endTurnButton.isDisabled = true
        discardCardButton.isVisible = false
    }

    /**
     * The refreshAfterTurnStart method is called by the service layer after a new turn has started.
     * It set the player name every turn for current player, and scores for players. Updated every round.
     *
     * Otherwise, [refreshView] refreshed the Views for players hand, discard Pile, play Area, player's Stack.
     *
     * Activated with button start Turn on [NextPlayerOverlay].
     */
    override fun refreshAfterTurnStart() {
        val game = rootService.currentGame
        checkNotNull(game) {"No started game found."}

        val players = game.playerList
        player1ScoreLabel.text = "${players[0].name}'s Score: ${players[0].score}"
        player2ScoreLabel.text = "${players[1].name}'s Score: ${players[1].score}"
        currentPlayerLabel.text = "Current Player: ${game.currentPlayer.name}"

        //The same process for play Area, make all cards clickable that player can swap them
        // or another actions.
        for(cardView in playArea) { cardView.onMouseClicked = null
        cardView.onMouseClicked = { clickedPlayAreaCard = cardView }}

        //Make the cards clickable to each card in the players hands.
        //For both player hand. this function iterates through all card views and make them clickable.
        //When a card is clicked, it's stored in the "clickedPlayerCard" variable. For every round.
        for(cardView in player1Hand) { cardView.onMouseClicked = { clickedPlayerCard = cardView } }
        for(cardView in player2Hand) { cardView.onMouseClicked = { clickedPlayerCard = cardView } }

        //Set the second player that we can change players.
        val secondPlayer = if(game.currentPlayer == players[0]) players[1] else players[0]

        //refresh the View for each Player and for the game that
        // for the game to be playable.
        refreshView(game.currentPlayer.hand, player1Hand, true)
        refreshView(secondPlayer.hand, player2Hand, false)
        refreshView(game.trio, playArea, true)
        refreshView(game.discardStack, discardPile, true)
        refreshView(game.currentPlayer.playerStack, player1DiscardPile, true)
        refreshView(secondPlayer.playerStack, player2DiscardPile, true)

        //make the buttons disabled
        endTurnButton.isDisabled = true

        //reset the clicked cards after new round started.
        clickedPlayerCard = null
        clickedPlayAreaCard = null
    }

    /**
     * The refreshAfterTurnEnds method change the players order when round endet, that the other player
     * can play. And make the buttons visible or disabled, that next player can play the game.
     * Activated with button [endTurnButton].
     */
    override fun refreshAfterTurnEnds() {
        val game = rootService.currentGame
        checkNotNull(game) {"No started game found."}

        cardDrawnInCurrentTurn = false
        clickedPlayerCard = null
        clickedPlayAreaCard = null
        playCardButton.isDisabled = false
        swapButton.isDisabled = false
        drawPile.isDisabled = false
        swappedCheckBox.isChecked = false
    }

    /**
     * The refreshAfterTurnEnds method updated the scores if player completed the trio.
     * When player played a card, make it visible in play Area, moves the [CardView] of the
     * card currently on the [playArea].
     *
     * Otherwise, [refreshView] refreshed the Views for players hand, discard Pile, play Area, player's Stack.
     *
     * Player can play card when [clickedPlayerCard] is selected and clicked on [playCardButton].
     */
    override fun refreshAfterCardPlayed(playedCard: Card) {
        val game = rootService.currentGame
        checkNotNull(game) {"No started game found."}

        clickedPlayerCard = null
        val players = game.playerList

        player1ScoreLabel.text = "${players[0].name}'s Score: ${players[0].score}"
        player2ScoreLabel.text = "${players[1].name}'s Score: ${players[1].score}"

        val secondPlayer = if(game.currentPlayer == players[0]) players[1] else players[0]

        // refresh the View for each Player and for the game that
        // for the game to be playable. When player played a card
        // the card is directly goes to play Area, and it is visible.
        refreshView(game.currentPlayer.hand, player1Hand, true)
        refreshView(secondPlayer.hand, player2Hand, false)
        refreshView(game.trio, playArea, true)
        refreshView(game.discardStack, discardPile, true)
        refreshView(game.currentPlayer.playerStack, player1DiscardPile, true)
        refreshView(secondPlayer.playerStack, player2DiscardPile, true)

        playCardButton.isDisabled = true
        swapButton.isDisabled = true
        endTurnButton.isDisabled = false
        drawPile.isDisabled = true
        discardCardButton.isVisible = false
    }

    /**
     * The refreshAfterCardDrawn method is called by the service layer after a card has been drawn.
     * It adds the drawn card to the playerHand and updates the all Stacks and Linear Labels
     * with [refreshView]. And used the variable [cardDrawnInCurrentTurn], that don't let the
     * player draw more than one card per Round. And disable or change visibility for buttons
     * that make the game playable.
     */
    private var cardDrawnInCurrentTurn = false
    override fun refreshAfterCardDrawn(card: Card) {
        val game = rootService.currentGame
        checkNotNull(game) {"No started game found."}

        // clear UI (clicked card)
        clickedPlayerCard = null
        clickedPlayAreaCard = null

        // refresh the View for each Player and for the game that
        // for the game to be playable. When the Player press on draw Card
        // the first card from draw Pile comes to player Hand.
        //And cardDrawnInCurrentTurn = true, that player cant draw another card (max 1).
        val players = game.playerList
        val secondPlayer = if(game.currentPlayer == players[0]) players[1] else players[0]
        refreshView(game.currentPlayer.hand, player1Hand, true)
        refreshView(secondPlayer.hand, player2Hand, false)
        refreshView(game.trio, playArea, true)
        refreshView(game.discardStack, discardPile, true)
        refreshView(game.currentPlayer.playerStack, player1DiscardPile, true)
        refreshView(secondPlayer.playerStack, player2DiscardPile, true)

        playCardButton.isDisabled = false
        cardDrawnInCurrentTurn = true

        // lock the end turn button that player cant end the turn with >8 cards.
        // else player can play the game just with last drawn card or end the turn
        val currentPlayer = if(game.currentPlayer == game.playerList[0]) player1Hand else player2Hand
        if(game.currentPlayer.hand.size > 8) {
            for (cardView in currentPlayer) { cardView.onMouseClicked = { clickedPlayerCard = cardView } }
            discardCardButton.isVisible = true
            endTurnButton.isDisabled = true
            swapButton.isDisabled = true
            clickedPlayerCard = null
        } else {
            //remove the clickable action, that player can just contact with last card. (play or endTurn)
            for (cards in game.currentPlayer.hand) {
                val cardView = cardMap.forward(cards)
                cardView.onMouseClicked = null
            }

            //Only the drawn card can be selected, that player can only play this card. (or end Turn)
            val drawnCard = cardMap.forward(card)
            drawnCard.onMouseClicked = { clickedPlayerCard = drawnCard }

            endTurnButton.isDisabled = false
            swapButton.isDisabled = true
            endTurnButton.isDisabled = false
        }
    }

    /**
     * The refreshAfterCardDiscarded method is called by the service layer after a card has been discarded.
     * It's take the card from the playerHand, put it in Discard Pile and
     * updates the all Stacks and Linear Labels with [refreshView].
     * And disable or change visibility for buttons that make the game playable.
     */
    override fun refreshAfterCardDiscarded(handCard: Card) {
        val game = rootService.currentGame
        checkNotNull(game) {"No started game found."}

        clickedPlayerCard = null

        val players = game.playerList
        val secondPlayer = if(game.currentPlayer == players[0]) players[1] else players[0]
        refreshView(game.currentPlayer.hand, player1Hand, true)
        refreshView(secondPlayer.hand, player2Hand, false)
        refreshView(game.trio, playArea, true)
        refreshView(game.discardStack, discardPile, true)
        refreshView(game.currentPlayer.playerStack, player1DiscardPile, true)
        refreshView(secondPlayer.playerStack, player2DiscardPile, true)

        discardCardButton.isVisible = false
        playCardButton.isDisabled = true
    }

    /**
     * The refreshAfterCardSwap method is called by the service layer after 2 cards been swapped.
     * It's swap 2 cards, one from player hand and one from play Area and updates the all Stacks and Linear Labels
     * with [refreshView]. And disable or change visibility for buttons that make the game playable.
     */
    override fun refreshAfterCardSwap(trioCard: Card, handCard: Card) {
        val game = rootService.currentGame
        checkNotNull(game) {"No started game found."}

        clickedPlayerCard = null
        clickedPlayAreaCard = null

        val players = game.playerList
        val secondPlayer = if(game.currentPlayer == players[0]) players[1] else players[0]
        refreshView(game.currentPlayer.hand, player1Hand, true)
        refreshView(secondPlayer.hand, player2Hand, false)
        refreshView(game.trio, playArea, true)
        refreshView(game.discardStack, discardPile, true)
        refreshView(game.currentPlayer.playerStack, player1DiscardPile, true)
        refreshView(secondPlayer.playerStack, player2DiscardPile, true)

        swapButton.isDisabled = true
        drawPile.isDisabled = true
    }

    /**
     * The refreshAfterCardSwap method is called by the service layer after game ended.
     * It goes to [WinningScreen] and show us who's the winner or if the is drawn.
     */
    override fun refreshAfterGameEnds() {
        val game = rootService.currentGame
        checkNotNull(game) {"No started game found."}
    }


    /**
     * clears [stackView], adds a new [CardView] for each
     * element of [stack] onto it, and adds the newly created view/card pair
     * to the global [cardMap].
     *
     * @param stack, The list of [Card] objects to be displayed in the stack view.
     * @param stackView, The UI component where the cards will be displayed.
     * @param cardImageLoader, A helper object (class from service) to load front and back Images for each card.
     */
    private fun initializeStackView(stack: List<Card>, stackView: CardStack<CardView>, cardImageLoader: CardImageLoader) {
        stackView.clear()
        stack.reversed().forEach { card -> // Iterate through the list of cards
            val cardView = CardView(
                height = 200,
                width = 130,
                front = cardImageLoader.frontImageFor(card.suit, card.value), // Load the front image
                back = cardImageLoader.backImage // Load the back image
            )
            stackView.add(cardView)// Add the card view to the stack view
            cardMap.add(card to cardView)// Map the card to its corresponding view
        }
    }

    /**
     * Initializes a linear view by clearing its current content, creating visual representation for each
     * card in [cardList], and adding these card views to the [linearView]. Also updates the global [cardMap]
     * to maintain a mapping between each card and its corresponding [CardView]. And make visible
     * front or back Image for each card with [changeSide].
     *
     * @param cardList, mutable List of [Card] objects to be displayed in the linear view.
     * @param linearView, The UI component of type [LinearLayout] where cards will be displayed. (mostly for hand)
     * @param cardImageLoader, helper object to load the front and back images for each card.
     * @param changeSide, boolean to determine whether the front or back of the cards should be displayed.
     */
    private fun initializeLinearView(cardList: MutableList<Card>, linearView: LinearLayout<CardView>, cardImageLoader: CardImageLoader, changeSide: Boolean) {
        linearView.clear()
        cardList.forEach { card -> // Iterate through the list of cards
            val cardView = CardView(
                height = 200,
                width = 130,
                front = cardImageLoader.frontImageFor(card.suit, card.value),
                back = cardImageLoader.backImage
            )
            if(changeSide) {cardView.showCardSide(CardView.CardSide.FRONT)} // Show the front side if true
            else {cardView.showCardSide(CardView.CardSide.BACK)} // Show the back side otherwise
            linearView.add(cardView) // Add the card view to the stack view
            cardMap.add(card to cardView) // Map the card to its corresponding view
        }
    }


    /**
     * Updates a given UI container to display the current state of a players hand or other
     * collection of the cards, that make the game playable. The method clears the [cardViewContainer],
     * retrieves the corresponding [CardView] for each card in the [hand] from global [cardMap], and
     * re-adds the views to the container, that helps us not to become the container failures. It also
     * adjusts the visibility of the front or back of the cards based in the [changeSide] parameter.
     *
     * @param hand The list of [Card] objects to be visualized in the [cardViewContainer].
     * @param cardViewContainer The UI component [GameComponentContainer], where the card views will be displayed.
     * @param changeSide, boolean determines which side of the cards to display.
     *
     * @throws IllegalStateException if there is no currently active game in the [rootService].
     */
    private fun refreshView(hand: MutableList<Card>,
                            cardViewContainer: GameComponentContainer<CardView>, changeSide: Boolean) {
        //To solve Container Problem for Cards, GameComponentContainer has been added

        val game = rootService.currentGame
        checkNotNull(game) {"No started game found."} // Ensure a game exists

        cardViewContainer.clear() // Clear the container for updated views
        for(cards in hand) { // Iterate through the list of cards
            val cardView = cardMap.forward(cards) // Retrieve the associated CardView from the cardMap

            // Remove the view from its current parent if needed!
            if(cardView.parent != null) cardView.removeFromParent()

            // Show the front of the card if changeSide is true
            // show the back of the card otherwise
            if(changeSide) { cardView.showCardSide(CardView.CardSide.FRONT) }
             else { cardView.showCardSide(CardView.CardSide.BACK) }
            cardViewContainer.add(cardView) // Add the updated view to the container
        }
    }
}

