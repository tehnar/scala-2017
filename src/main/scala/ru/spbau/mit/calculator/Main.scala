package ru.spbau.mit.calculator

import ru.spbau.mit.calculator.operations.functions.SinusFunction
import ru.spbau.mit.calculator.operations.operators._
import ru.spbau.mit.calculator.parser.ParserImpl
import ru.spbau.mit.calculator.tokenizer.TokenizerImpl


object Main {
  def main(args: Array[String]): Unit = {
    val tokenizer = new TokenizerImpl()
    val operators = List(new PlusBinaryOperator(), new MinusBinaryOperator(),
      new MultiplyBinaryOperator(), new DivideBinaryOperator())
    val functions = List(new SinusFunction())
    val parser = new ParserImpl(operators)
    val calculator = new Calculator(parser, tokenizer, operators, functions)
    println(calculator.calculate("1 + sin((2 * 3 * (4 + 5) / 6 * 2))"))
  }
}
