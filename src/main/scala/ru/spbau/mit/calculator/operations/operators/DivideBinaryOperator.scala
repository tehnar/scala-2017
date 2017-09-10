package ru.spbau.mit.calculator.operations.operators

class DivideBinaryOperator extends BinaryOperatorExpression {
  override def priority(): Int = 2

  override def evaluate(lhs: Double, rhs: Double): Double = lhs / rhs

  override def name() = "/"
}
