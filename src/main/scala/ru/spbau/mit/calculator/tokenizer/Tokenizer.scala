package ru.spbau.mit.calculator.tokenizer
import java.util

trait Tokenizer {
  def tokenize(expr: String): util.List[Tokens.Token]
}
