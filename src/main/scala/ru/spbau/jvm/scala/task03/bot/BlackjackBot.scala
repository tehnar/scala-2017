package ru.spbau.jvm.scala.task03.bot

import info.mukel.telegrambot4s.api.declarative.{Callbacks, Commands}
import info.mukel.telegrambot4s.api.{Polling, TelegramBot}
import info.mukel.telegrambot4s.models.{InlineKeyboardButton, InlineKeyboardMarkup, Message}
import ru.spbau.jvm.scala.task03.game.{ActiveDeal, BlackjackGame, GameNotStarted}

import scala.collection.mutable
import scala.concurrent.Await
import scala.concurrent.duration._


class BlackjackBot(val token: String) extends TelegramBot with Polling with Commands with Callbacks {

  val games: mutable.Map[Long, BlackjackGame] = mutable.HashMap.empty

  onCommand('start) {
    implicit message =>
      if (!games.contains(message.chat.id)) {
        Await.result(reply("This is a simple blackjack bot"), 1 second)
        val game = newGame()
        games.put(message.chat.id, game)
        makeButtons(game)
      } else {
        reply("The game is already running!")
      }
  }

  onCallbackWithTag("new") {
    cbq =>
      implicit val message: Message = cbq.message.get
      val game: BlackjackGame = games.getOrElseUpdate(message.chat.id, newGame())
      game.newGame()
      makeButtons(game)
  }

  onCallbackWithTag("deal") {
    cbq =>
      implicit val message: Message = cbq.message.get
      val maybeGame = games.get(message.chat.id)
      maybeGame match {
        case Some(game) =>
          game.deal()
          makeButtons(game)
        case None => reply(help())
      }
  }

  onCallbackWithTag("stop") {
    cbq =>
      implicit val message: Message = cbq.message.get
      val maybeGame = games.get(message.chat.id)
      maybeGame match {
        case Some(game) =>
          game.stop()
          makeButtons(game)
        case None => reply(help())
      }
  }

  def makeButtons(game: BlackjackGame)(implicit message: Message): Unit = {
    game.gameState match {
      case GameNotStarted =>
        reply("Start new game", replyMarkup = Some(
          InlineKeyboardMarkup.singleColumn(Seq(InlineKeyboardButton.callbackData("new game", "new")))
        ))
      case ActiveDeal(_, _) =>
        reply("Your turn", replyMarkup = Some(
          InlineKeyboardMarkup.singleColumn(Seq(
            InlineKeyboardButton.callbackData("deal", "deal"),
            InlineKeyboardButton.callbackData("stop", "stop")
          ))
        ))
    }
  }

  private def newGame()(implicit message: Message): BlackjackGame =
    new BlackjackGame(gameMessage => Await.result(reply(gameMessage), 1 second))

  private def help(): String = {
    """
This is a simple blackjack bot. The commands are:

/new - start a new game
/deal - draw a card
/stop - stop drawing cards
    """.stripMargin
  }
}


