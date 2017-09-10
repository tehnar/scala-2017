package ru.spbau.mit.calculator.parser
import java.util

import ru.spbau.mit.calculator.operations.operators.BinaryOperatorExpression
import ru.spbau.mit.calculator.tokenizer.Tokens
import ru.spbau.mit.calculator.tokenizer.Tokens._

class ParserImpl(operators: List[BinaryOperatorExpression]) extends Parser {
  val operatorPriorities: Map[String, Int] = operators.map(op => (op.name(), op.priority())).toMap

  override def parse(tokens: util.List[Tokens.Token]): AST.ASTNode = {
    var i = 0
    val nodesAndOperators = new util.ArrayList[Either[Tokens.Operator, AST.ASTNode]]()
    while (i < tokens.size()) {
      val x: Either[Tokens.Operator, AST.ASTNode] = tokens.get(i) match {
        case Number(value) =>
          i += 1
          Right(AST.Number(value.toDouble))
        case LeftParenthesis() =>
          val rightParenthesisId = findMatchingParenthesisId(tokens, i)
          val expr = parse(tokens.subList(i + 1, rightParenthesisId))
          i = rightParenthesisId + 1
          Right(expr)
        case Identifier(name) =>
          val (expr, r) = parseFunctionCall(tokens.subList(i, tokens.size()))
          i = r + i + 1
          Right(expr)
        case Operator(op) =>
          i += 1
          Left(Operator(op))
        case token => throw new ParserException(s"Unexpected token $token")
      }
      nodesAndOperators.add(x)
    }

    getTreeFromNodesAndOperators(nodesAndOperators)
  }

  private def getTreeFromNodesAndOperators(nodesAndOperators: util.List[Either[Tokens.Operator, AST.ASTNode]]): AST.ASTNode = {
    var minOperatorPriorityIndex = -1
    var operator = ""
    var minOperatorPriority = Int.MaxValue
    for (i <- 0 until nodesAndOperators.size()) {
      if (nodesAndOperators.get(i).isLeft) {
        val op = nodesAndOperators.get(i).left.get.op
        val priority = operatorPriorities.getOrElse(op, throw new ParserException(s"Unknown operator $op"))
        if (minOperatorPriorityIndex == -1 || (minOperatorPriority >= priority)) {
          operator = op
          minOperatorPriorityIndex = i
          minOperatorPriority = priority
        }
      }
    }
    if (minOperatorPriorityIndex == -1) {
      if (nodesAndOperators.size() == 1) {
        return nodesAndOperators.get(0).right.get
      } else {
        throw new ParserException("Binary operator expected")
      }
    }

    AST.BinaryOperator(operator,
      getTreeFromNodesAndOperators(nodesAndOperators.subList(0, minOperatorPriorityIndex)),
      getTreeFromNodesAndOperators(nodesAndOperators.subList(minOperatorPriorityIndex + 1, nodesAndOperators.size()))
    )
  }


  private def findMatchingParenthesisId(tokens: util.List[Tokens.Token], fromIndex: Int): Int = {
    var curPos = fromIndex
    var curBalance = 0
    while (curPos < tokens.size()) {
      tokens.get(curPos) match {
        case LeftParenthesis() => curBalance += 1
        case RightParenthesis() => curBalance -= 1
        case _ =>
      }
      if (curBalance == 0) {
        return curPos
      }
      curPos += 1
    }
      throw new ParserException("Cannot find matching parenthesis")
  }

  private def parseFunctionCall(tokens: util.List[Tokens.Token]): (AST.ASTNode, Int) = {
    if (tokens.size() < 3) {
      throw new ParserException("Cannot parse function call")
    }

    val functionName = tokens.get(0) match {
      case Identifier(name) => name
      case _ => throw new ParserException("Cannot parse function call")
    }

    tokens.get(1) match {
      case LeftParenthesis() =>
      case _ => throw new ParserException("Cannot parse function call")
    }

    val rightParenthesisId = findMatchingParenthesisId(tokens, 1)
    (AST.FunctionCall(functionName, parse(tokens.subList(2, rightParenthesisId))), rightParenthesisId)
  }

}
