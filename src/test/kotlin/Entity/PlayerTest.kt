package Entity

import entity.Player
import org.junit.jupiter.api.Assertions.*
import kotlin.test.Test

class PlayerTest {
    @Test
    fun testPlayer() {

        val player = Player(
            name = "Tom",
            score = 30,
            swapped = true,
            hand = mutableListOf(),
            playerStack = mutableListOf()
        )
        assertEquals("Tom", player.name)
        assertEquals(30,player.score)
        assertTrue(player.swapped)
        assertTrue(player.hand.isEmpty())
        assertTrue(player.playerStack.isEmpty())

        assertDoesNotThrow {
            player.name = "Tom";
            player.score = 30;
            player.swapped = true;
            player.hand = mutableListOf();
            player.playerStack = mutableListOf();
        }
    }
}