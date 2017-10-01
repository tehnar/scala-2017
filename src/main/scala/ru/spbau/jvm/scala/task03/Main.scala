package ru.spbau.jvm.scala.task03

import ru.spbau.jvm.scala.task03.bot.BlackjackBot

object Main {
  def main(args: Array[String]): Unit = {
    val bot = new BlackjackBot("385530401:AAFrsWBMmfbh1W0CoG7J_gE1gRmteLLaBL0")
    bot.run()
  }
}
