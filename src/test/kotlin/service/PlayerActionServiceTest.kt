package service

import entity.Card
import entity.CardSuit
import entity.CardValue
import kotlin.test.*

class PlayerActionServiceTest {
    @Test
    fun testPlayCard(){
        val card = Card(
            false,
            CardSuit.DIAMONDS,
            CardValue.SEVEN
            )
    }
}