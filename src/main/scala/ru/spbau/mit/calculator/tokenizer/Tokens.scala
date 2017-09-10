package ru.spbau.mit.calculator.tokenizer

object Tokens {
  trait Token

  case class Number(value: String) extends Token
  case class Operator(op: String) extends Token
  case class LeftParenthesis() extends Token
  case class RightParenthesis() extends Token
  case class Identifier(name: String) extends Token
}
