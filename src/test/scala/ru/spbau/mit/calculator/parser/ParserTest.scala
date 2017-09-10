package ru.spbau.mit.calculator.parser

import java.util

import org.scalatest.FunSuite
import ru.spbau.mit.calculator.operations.operators.BinaryOperatorExpression
import ru.spbau.mit.calculator.tokenizer.Tokens

class ParserTest extends FunSuite {
  val plusOperator = new BinaryOperatorExpression {
    override def priority(): Int = 1
    override def name(): String = "+"
    override def evaluate(lhs: Double, rhs: Double): Double = ???
  }
  val minusOperator = new BinaryOperatorExpression {
    override def priority(): Int = 1
    override def name(): String = "-"
    override def evaluate(lhs: Double, rhs: Double): Double = ???
  }
  val multiplyOperator = new BinaryOperatorExpression {
    override def priority(): Int = 2
    override def name(): String = "*"
    override def evaluate(lhs: Double, rhs: Double): Double = ???
  }
  val divideOperator = new BinaryOperatorExpression {
    override def priority(): Int = 2
    override def name(): String = "/"
    override def evaluate(lhs: Double, rhs: Double): Double = ???
  }

  val operators = List(plusOperator, minusOperator, multiplyOperator, divideOperator)
  val parser = new ParserImpl(operators)

  test("Test single number token") {
    val expected = AST.Number(1.23)
    assert(expected === parser.parse(util.Arrays.asList(Tokens.Number("1.23"))))
  }

  test("Test function call") {
    val expected = AST.FunctionCall("func", AST.Number(1.23))
    val tokens = util.Arrays.asList[Tokens.Token](
      Tokens.Identifier("func"), Tokens.LeftParenthesis(), Tokens.Number("1.23"), Tokens.RightParenthesis()
    )
    assert(expected === parser.parse(tokens))
  }

  test("Test same operator priority") {
    val expected =
      AST.BinaryOperator("-",
        AST.BinaryOperator("-",
          AST.BinaryOperator("+", AST.Number(1), AST.Number(2)),
          AST.Number(3)
        ),
        AST.Number(4)
      )
    // 1 + 2 - 3 - 4
    val tokens = util.Arrays.asList[Tokens.Token](
      Tokens.Number("1"), Tokens.Operator("+"), Tokens.Number("2"), Tokens.Operator("-"), Tokens.Number("3"),
      Tokens.Operator("-"), Tokens.Number("4")
    )
    assert(expected === parser.parse(tokens))
  }


  test("Test different operator priority") {
    val expected =
      AST.BinaryOperator("+",
        AST.BinaryOperator("+",
          AST.Number(1),
          AST.BinaryOperator("/",
            AST.BinaryOperator("/", AST.Number(2), AST.Number(3)),
            AST.Number(4)),
        ),
        AST.Number(5)
      )
    // 1 + 2 / 3 / 4 + 5
    val tokens = util.Arrays.asList[Tokens.Token](
      Tokens.Number("1"), Tokens.Operator("+"), Tokens.Number("2"), Tokens.Operator("/"), Tokens.Number("3"),
      Tokens.Operator("/"), Tokens.Number("4"), Tokens.Operator("+"), Tokens.Number("5")
    )
    assert(expected === parser.parse(tokens))
  }

  test("Test with parenthesis") {
    val expected =
      AST.BinaryOperator("/",
        AST.BinaryOperator("/",
          AST.BinaryOperator("+", AST.Number(1), AST.Number(2)),
          AST.Number(3)
        ),
        AST.BinaryOperator("+", AST.Number(4), AST.Number(5))
      )
    // ((1 + 2) / 3) / ((4 + 5))
    val tokens = util.Arrays.asList[Tokens.Token](
      Tokens.LeftParenthesis(), Tokens.LeftParenthesis(), Tokens.Number("1"), Tokens.Operator("+"),
      Tokens.Number("2"), Tokens.RightParenthesis(), Tokens.Operator("/"), Tokens.Number("3"), Tokens.RightParenthesis(),
      Tokens.Operator("/"), Tokens.LeftParenthesis(), Tokens.LeftParenthesis(),
      Tokens.Number("4"), Tokens.Operator("+"), Tokens.Number("5"), Tokens.RightParenthesis(), Tokens.RightParenthesis()
    )
    assert(expected === parser.parse(tokens))
  }

  test("Test imbalanced parenthesis") {
    val tokens = util.Arrays.asList[Tokens.Token](
      Tokens.LeftParenthesis(), Tokens.RightParenthesis(),
      Tokens.RightParenthesis(), Tokens.LeftParenthesis()
    )

    intercept[ParserException] {
      parser.parse(tokens)
    }
  }

  test("Test subsequent operators") {
    val tokens = util.Arrays.asList[Tokens.Token](
      Tokens.Number("1"), Tokens.Operator("+"),
      Tokens.Operator("-"), Tokens.Operator("2")
    )

    intercept[ParserException] {
      parser.parse(tokens)
    }
  }
}
