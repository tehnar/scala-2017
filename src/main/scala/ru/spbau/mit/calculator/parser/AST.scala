package ru.spbau.mit.calculator.parser

object AST {
  trait ASTNode
  case class BinaryOperator(op: String, lhs: ASTNode, rhs: ASTNode) extends ASTNode
  case class FunctionCall(functionName: String, arg: ASTNode) extends ASTNode
  case class Number(x: Double) extends ASTNode
}
