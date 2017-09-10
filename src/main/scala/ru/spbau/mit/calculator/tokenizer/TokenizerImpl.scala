package ru.spbau.mit.calculator.tokenizer
import java.util

import ru.spbau.mit.calculator.tokenizer.Tokens.{LeftParenthesis, RightParenthesis, Token}

class TokenizerImpl extends Tokenizer {
  val VALID_OPERATOR_CHARS = Set('+', '-', '*', '/', '%')

  override def tokenize(expr: String): util.List[Token] = {
    var curPos = 0
    var tokens = new util.ArrayList[Token]()

    while (curPos < expr.length) {
      val (token, newPos) = expr.charAt(curPos) match {
        case ' ' => (null, curPos + 1)
        case '(' => (new LeftParenthesis, curPos + 1)
        case ')' => (new RightParenthesis, curPos + 1)
        case c if VALID_OPERATOR_CHARS.contains(c) => parseOperator(expr, curPos)
        case c if c.isDigit => parseNumber(expr, curPos)
        case c if c.isLetter => parseIdentifier(expr, curPos)
        case c => throw new TokenizerException(s"Unexpected character $c at $curPos")
      }
      curPos = newPos
      if (token != null) {
        tokens.add(token)
      }
    }

    tokens
  }

  private def parseNumber(expr: String, from: Int): (Token, Int) = {
    var curPos = from
    var wasDot = false
    while (curPos < expr.length && (expr.charAt(curPos).isDigit || expr.charAt(curPos) == '.')) {
      if (expr.charAt(curPos) == '.') {
        if (wasDot) {
          throw new TokenizerException(s"Unexpected extra dot while parsing number at $curPos")
        } else {
          wasDot = true
        }
      }
      curPos += 1
    }
    (Tokens.Number(expr.substring(from, curPos)), curPos)
  }

  private def parseIdentifier(expr: String, from: Int): (Token, Int) = {
    var curPos = from
    while (curPos < expr.length && expr.charAt(curPos).isLetterOrDigit) {
      curPos += 1
    }
    (Tokens.Identifier(expr.substring(from, curPos)), curPos)
  }

  private def parseOperator(expr: String, from: Int): (Token, Int) = {
    var curPos = from
    while (curPos < expr.length && VALID_OPERATOR_CHARS.contains(expr.charAt(curPos))) {
      curPos += 1
    }
    (Tokens.Operator(expr.substring(from, curPos)), curPos)
  }
}
