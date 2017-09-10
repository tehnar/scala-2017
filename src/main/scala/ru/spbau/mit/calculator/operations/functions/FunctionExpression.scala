package ru.spbau.mit.calculator.operations.functions

trait FunctionExpression {
  def evaluate(arg: Double): Double
  def name(): String
}
