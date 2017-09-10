package ru.spbau.mit.calculator.tokenizer

import java.util

import org.scalatest.FunSuite

class TokenizerTest extends FunSuite {
  test("Tokenize empty string") {
    val tokenizer = new TokenizerImpl()
    val expected = new util.ArrayList()
    assert(expected === tokenizer.tokenize(""))
  }

  test("Tokenize identifiers") {
    val tokenizer = new TokenizerImpl()
    val expected = util.Arrays.asList(
      Tokens.Identifier("a"), Tokens.Identifier("b"),
      Tokens.Identifier("cc"), Tokens.Identifier("def")
    )
    assert(expected === tokenizer.tokenize(" a b    cc     def   "))
  }

  test("Tokenize numbers") {
    val tokenizer = new TokenizerImpl()
    val expected = util.Arrays.asList(
      Tokens.Number("12345380423453454212343124"), Tokens.Number("0.00"),
      Tokens.Number("123."), Tokens.Number("1")
    )
    assert(expected === tokenizer.tokenize(" 12345380423453454212343124 0.00 123.  1   "))
  }

  test("Tokenize parenthesis") {
    val tokenizer = new TokenizerImpl()
    val expected = util.Arrays.asList(
      Tokens.LeftParenthesis(), Tokens.RightParenthesis(), Tokens.LeftParenthesis(),
      Tokens.LeftParenthesis(), Tokens.RightParenthesis()
    )
    assert(expected === tokenizer.tokenize(" ( ) ((    )   "))
  }

  test("Tokenize operators") {
    val tokenizer = new TokenizerImpl()
    val expected = util.Arrays.asList(
      Tokens.Operator("+"), Tokens.Operator("++"), Tokens.Operator("+-"),
      Tokens.Operator("//"), Tokens.Operator("*")
    )
    assert(expected === tokenizer.tokenize(" + ++      +- //   *"))
  }

  test("Tokenize all") {
    val tokenizer = new TokenizerImpl()
    val expected = util.Arrays.asList(
      Tokens.Number("1"), Tokens.Operator("+"), Tokens.Number("2"), Tokens.Operator("*"),
      Tokens.Number("3.3"), Tokens.Operator("+"), Tokens.Identifier("blah"),
      Tokens.LeftParenthesis(), Tokens.RightParenthesis(), Tokens.RightParenthesis(), Tokens.RightParenthesis()
    )
    assert(expected === tokenizer.tokenize(" 1+2*3.3+blah()))   "))
  }

  test("Tokenize number with multiple dots") {
    val tokenizer = new TokenizerImpl()
    intercept[TokenizerException] {
      tokenizer.tokenize("1.2.3")
    }
  }
}
