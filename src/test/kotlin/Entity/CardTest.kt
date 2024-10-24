package Entity

import entity.Card
import entity.CardSuit
import entity.CardValue
import entity.DiveGame
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow

/**
 * Test für Card, verknüpft mit suit und Value zu CardSuit und CardValue
 * @param testCard welche gespielt werden soll, und zeigt uns ob es abgedeckt oder nicht
 * @throws IllegalStateException wenn Karte ungültig ist
 */

class CardTest {
    @Test
    fun testCard() {
        val card = Card(
            isHidden = false,
            suit = CardSuit.DIAMONDS,
            cValue = CardValue.SEVEN,
            diveGame = null
        )
        assertFalse(card.isHidden)
        assertEquals(CardSuit.DIAMONDS, card.suit)
        assertEquals(CardValue.SEVEN, card.cValue)
        assertDoesNotThrow {
            card.isHidden;
            card.suit = CardSuit.DIAMONDS;
            card.cValue = CardValue.SEVEN;
        }
    }
}