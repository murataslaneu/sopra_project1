package Entity

import entity.DiveGame
import entity.Player
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow

class DiveGameTest {
    @Test
    fun testDiveGame() {
        val game = DiveGame(
            currentPlayer = Player("Tom", score = 30, swapped = true ,hand = mutableListOf(), playerStack = mutableListOf()),
            playerList = mutableListOf(),
            discradCard = mutableListOf(),
            trio = mutableListOf(),
            drawStack = mutableListOf()
        )
        assertEquals(game.currentPlayer, game.currentPlayer)
        assertTrue(game.playerList.isEmpty())
        assertTrue(game.discradCard.isEmpty())
        assertTrue(game.trio.isEmpty())
        assertTrue(game.drawStack.isEmpty())

        assertDoesNotThrow{
            game.currentPlayer = Player("Tom", score = 30, swapped = true ,hand = mutableListOf(), playerStack = mutableListOf());
            game.playerList = mutableListOf();
            game.discradCard = mutableListOf();
            game.trio = mutableListOf();
            game.drawStack = mutableListOf();

        }

    }
}