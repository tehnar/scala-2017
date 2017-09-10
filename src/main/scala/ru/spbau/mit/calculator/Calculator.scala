package ru.spbau.mit.calculator

import ru.spbau.mit.calculator.operations.functions.FunctionExpression
import ru.spbau.mit.calculator.operations.operators.BinaryOperatorExpression
import ru.spbau.mit.calculator.parser.AST.{BinaryOperator, FunctionCall, Number}
import ru.spbau.mit.calculator.parser.{AST, Parser}
import ru.spbau.mit.calculator.tokenizer.Tokenizer

class Calculator(parser: Parser, tokenizer: Tokenizer,
                 binaryOperators: List[BinaryOperatorExpression], functions: List[FunctionExpression]) {
  def calculate(expression: String): Double = {
    val tree = parser.parse(tokenizer.tokenize(expression))
    evaluateTree(tree)
  }

  private def evaluateTree(root: AST.ASTNode): Double = {
    root match {
      case BinaryOperator(op, lhs, rhs) =>
        val operator = binaryOperators
          .find(operator => operator.name() == op)
          .getOrElse(throw new CalculatorException(s"Unknown operator $op"))
        operator.evaluate(evaluateTree(lhs), evaluateTree(rhs))
      case FunctionCall(functionName, arg) =>
        val function = functions
          .find(function => function.name() == functionName)
          .getOrElse(throw new CalculatorException(s"Unknown function$functionName"))
        function.evaluate(evaluateTree(arg))
      case Number(x) =>
        x
    }
  }
}
