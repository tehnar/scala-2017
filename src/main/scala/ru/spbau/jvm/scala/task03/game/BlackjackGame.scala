package ru.spbau.jvm.scala.task03.game

import scala.collection.mutable
import scala.util.Random

class BlackjackGame(val messageListener: (String) => Unit) {
  private var deck = new Deck()

  private var _gameState: GameState = GameNotStarted
  def gameState: GameState = _gameState

  def newGame(): Unit = {
    gameState match {
      case GameNotStarted =>
        deck = new Deck()
        val playerHand = new Hand(deck.deal(), deck.deal())
        val dealerHand = new Hand(deck.deal(), deck.deal())
        _gameState = ActiveDeal(playerHand, dealerHand)
        messageListener(s"Your hand: ${handToStr(playerHand)} with total sum ${playerHand.sum()}\n" +
          s"Dealer hand: ${handToStr(dealerHand, 1)}")

      case _ => messageListener("You should first finish current game")
    }
  }

  private def endDeal(playerHand: Hand, dealerHand: Hand): Unit = {
    val message = if (playerHand.sum() > 21) {
      "You lost, sum is greater than 21"
    } else if (dealerHand.sum() > 21) {
      "You won, dealer's sum is greater than 21"
    } else if (playerHand.sum() == 21) {
      "You won, blackjack!"
    } else if (dealerHand.sum() == 21) {
      "Dealer won, blackjack!"
    } else if (playerHand.sum() > dealerHand.sum()) {
      "You won"
    } else {
      "You lost"
    }
    _gameState = GameNotStarted
    messageListener(message)
  }

  def deal(): Unit = {
    gameState match {
      case ActiveDeal(playerHand, dealerHand) =>
        val card = deck.deal()
        playerHand.addCard(card)
        messageListener(s"You draw ${cardToStr(card)}")
        if (playerHand.sum() >= 21) {
          endDeal(playerHand, dealerHand)
        } else {
          messageListener(s"Your hand: ${handToStr(playerHand)} with total sum ${playerHand.sum()}")
        }
      case _ => messageListener("The game is finished or has not started")
    }
  }

  def stop(): Unit = {
    gameState match {
      case ActiveDeal(playerHand, dealerHand) =>
        while (dealerHand.sum() < 17) {
          val card = deck.deal()
          messageListener(s"Dealer draws ${cardToStr(card)}")
          dealerHand.addCard(card)
        }
        messageListener(s"Dealer hand is: ${handToStr(dealerHand)} with total sum ${dealerHand.sum()}")
        endDeal(playerHand, dealerHand)
      case _ => messageListener("The game is finished or has not started")
    }
  }

  private def cardToStr(card: Card) = s"${card.suitChar}${card.rankStr}"

  private def handToStr(hand: Hand): String = handToStr(hand, hand.cards.size)
  private def handToStr(hand: Hand, cardsToShow: Int): String = {
    (hand.cards.take(cardsToShow).map(card => s"${cardToStr(card)}").toList :::
      hand.cards.drop(cardsToShow).map(_ => "??").toList).mkString(" ")
  }
  private def deal(hand: Hand, count: Int): Unit = {
    (1 to count).foreach(_ => hand.addCard(deck.deal()))
  }
}

class Hand(private val card1: Card, private val card2: Card) {
  val cards: mutable.MutableList[Card] = mutable.MutableList(card1, card2)

  def sum(): Int = cards.map(card => if (card.rankId >= 10) 10 else card.rankId).sum

  def addCard(card: Card): Unit = {
    cards += card
  }

}

sealed trait GameState
case object GameNotStarted extends GameState
case class ActiveDeal(playerHand: Hand, dealerHand: Hand) extends GameState

case class Card(id: Int) {
  val rankId: Int = id % 13 + 1
  val suitId: Int = id % 4

  val rankStr: String =
    if (rankId == 1) "A"
    else if (rankId < 10) (rankId + '0').toChar.toString
    else if (rankId == 10) "10"
    else "JQK".charAt(rankId - 11).toString
  val suitChar: Char = "♦♣♥♠".charAt(suitId)
}

class Deck {
  private var deck: List[Card] = Random.shuffle(0 to 51).map(Card).toList

  def deal(): Card = {
    val card = deck.head
    deck = deck.tail
    card
  }

  def size(): Int = deck.size
}
