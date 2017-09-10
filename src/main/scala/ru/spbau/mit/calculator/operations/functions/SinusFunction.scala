package ru.spbau.mit.calculator.operations.functions

class SinusFunction() extends FunctionExpression {
  override def evaluate(arg: Double): Double = Math.sin(arg)
  override def name() = "sin"
}
