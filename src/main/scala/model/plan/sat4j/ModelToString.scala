package model.plan.sat4j

import scala.language.implicitConversions
import model.Action
import model.Literal

/**
  * An utility object that contains useful function in order to encode the model into a SATPlan compliant description as String.
  */
object ModelToString {

  private val NOT = "!(%s)"
  private val AND = "%s && %s"
  private val OR = "%s || %s"
  private val TIME_CONCAT = "%d_%s"
  private val IMPLY = "(!(%s) || (%s))"

  /**
    * Logic AND.
    * @param elements
    * @return
    */
  def and(elements: String*): String = elements.reduce((one: String, two: String) => String.format(AND, one, two))

  /**
    * Logic AND.
    * @param elements
    * @return
    */
  def and(elements: Iterable[String]): String = elements.reduce((one: String, two: String) => String.format(AND, one, two))

  /**
    * Logic OR.
    * @param elements
    * @return
    */
  def or(elements: String*): String = elements.reduce((one: String, two: String) => String.format(OR, one, two))

  /**
    * Logic OR.
    * @param elements
    * @return
    */
  def or(elements: Iterable[String]): String = elements.reduce((one: String, two: String) => String.format(OR, one, two))

  /**
    * Encode the element with the time information.
    * @param time
    * @param element
    * @return
    */
  def timing(time: Int, element: String): String = String.format(TIME_CONCAT, java.lang.Integer.valueOf(time), element)

  /**
    * Log NOT.
    * @param element
    * @return
    */
  def not(element: String): String = String.format(NOT, element)

  /**
    * Close an element with two parenthesis.
    * @param element
    * @return
    */
  def close(element: String): String = "( " + element + " )"

  /**
    * Logic implication.
    * @param antecedent
    * @param consequent
    * @return
    */
  def imply(antecedent: String, consequent: String): String = String.format(IMPLY, antecedent, consequent)

  /**
    * Logic double implication.
    * @param antecedent
    * @param consequent
    * @return
    */
  def doubleImply(antecedent: String, consequent: String): String = close(String.format(AND, imply(antecedent, consequent), imply(consequent, antecedent)))

  /**
    * Encode an action.
    * @param action
    * @return
    */
  def action(action: Action): String = action.name

  /**
    * Encode an action with a specific time.
    * @param action
    * @param time
    * @return
    */
  def timeAction(action: Action, time: Int): String = timing(time, action.name)

  /**
    * Encode a literal.
    * @param literal
    * @return
    */
  def literal(literal: Literal): String = literal.name

  /**
    * Encode a literal with a specific time.
    * @param literal
    * @param time
    * @return
    */
  def timeLiteral(literal: Literal, time: Int): String = timing(time, literal.name)
}
