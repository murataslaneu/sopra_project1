package entity
import kotlin.test.*
/**
 * Diese Klasse enthält Testfälle für die Card Klasse, um sicherzustellen, dass die
 * Karteneigenschaften korrekt initialisiert und geprüft werden.
 */
class CardTest {
    @Test
    fun testCard() {
        val card = Card(
            isHidden = false,           //für isHidden false eingegeben.
            suit = CardSuit.DIAMONDS,   //für CardSuit ist Diamond eingegeben.
            value = CardValue.SEVEN     //für CardValue ist Seven eingegeben.
        )
        assertFalse(card.isHidden)                  //prüft ob isHidden false ist.
        assertEquals(CardSuit.DIAMONDS, card.suit)  //prüft ob CardSuit Diamond ist.
        assertEquals(CardValue.SEVEN, card.value)   //prüft ob CardValue Seven ist.
    }
}