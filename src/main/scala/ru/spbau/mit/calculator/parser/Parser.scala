package ru.spbau.mit.calculator.parser
import java.util

import ru.spbau.mit.calculator.parser.AST.ASTNode
import ru.spbau.mit.calculator.tokenizer.Tokens.Token

trait Parser {
  def parse(tokens: util.List[Token]): ASTNode
}
