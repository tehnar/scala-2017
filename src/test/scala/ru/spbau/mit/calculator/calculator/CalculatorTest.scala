package ru.spbau.mit.calculator.calculator

import java.util

import org.scalamock.scalatest.MockFactory
import org.scalatest.FunSuite
import ru.spbau.mit.calculator.Calculator
import ru.spbau.mit.calculator.operations.functions.FunctionExpression
import ru.spbau.mit.calculator.operations.operators.BinaryOperatorExpression
import ru.spbau.mit.calculator.parser.{AST, Parser}
import ru.spbau.mit.calculator.tokenizer.{Tokenizer, Tokens}

class CalculatorTest extends FunSuite with MockFactory {
  val plusOperator = new BinaryOperatorExpression {
    override def priority(): Int = 1
    override def name(): String = "+"
    override def evaluate(lhs: Double, rhs: Double): Double = lhs + rhs
  }
  val minusOperator = new BinaryOperatorExpression {
    override def priority(): Int = 1
    override def name(): String = "-"
    override def evaluate(lhs: Double, rhs: Double): Double = lhs - rhs
  }
  val multiplyOperator = new BinaryOperatorExpression {
    override def priority(): Int = 2
    override def name(): String = "*"
    override def evaluate(lhs: Double, rhs: Double): Double = lhs * rhs
  }
  val divideOperator = new BinaryOperatorExpression {
    override def priority(): Int = 2
    override def name(): String = "/"
    override def evaluate(lhs: Double, rhs: Double): Double = lhs / rhs
  }

  val operators = List(plusOperator, minusOperator, multiplyOperator, divideOperator)
  val functions = List(new FunctionExpression {
    override def name(): String = "sqrt"
    override def evaluate(arg: Double): Double = Math.sqrt(arg)
  })

  test("Test") {
    val parser = mock[Parser]
    val tokenizer = mock[Tokenizer]
    val calculator = new Calculator(parser, tokenizer, operators, functions)
    val expr = "1 + 2*3 / 6 * sqrt(( 2  *  3 -  2))"
    val tokens = util.Arrays.asList[Tokens.Token](
      Tokens.Number("1"), Tokens.Operator("+"), Tokens.Number("2"), Tokens.Operator("*"),
      Tokens.Number("3"), Tokens.Operator("/"), Tokens.Number("6"), Tokens.Operator("*"),
      Tokens.Identifier("sqrt"), Tokens.LeftParenthesis(), Tokens.LeftParenthesis(),
      Tokens.Number("2"), Tokens.Operator("*"), Tokens.Number("3"), Tokens.Operator("-"),
      Tokens.Number("2"), Tokens.RightParenthesis(), Tokens.RightParenthesis()
    )
    val tree =
      AST.BinaryOperator("+",
        AST.Number(1),
        AST.BinaryOperator("*",
          AST.BinaryOperator("/",
            AST.BinaryOperator("*", AST.Number(2), AST.Number(3)),
            AST.Number(6)
          ),
          AST.FunctionCall("sqrt",
            AST.BinaryOperator("-",
              AST.BinaryOperator("*", AST.Number(2), AST.Number(3)),
              AST.Number(2)
            )
          )
        )
      )
    (tokenizer.tokenize _).expects(expr).returns(tokens)
    (parser.parse _).expects(tokens).returns(tree)
    val expected = 3
    assert(calculator.calculate(expr) === expected)
  }
}
