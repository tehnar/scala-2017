package ru.spbau.mit.calculator.operations.operators


trait BinaryOperatorExpression {
  def priority(): Int
  def evaluate(lhs: Double, rhs: Double): Double
  def name(): String
}
