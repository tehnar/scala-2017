package ru.spbau.mit.calculator.operations.operators

class MinusBinaryOperator extends BinaryOperatorExpression {
  override def priority(): Int = 1

  override def evaluate(lhs: Double, rhs: Double): Double = lhs - rhs

  override def name(): String = "-"
}