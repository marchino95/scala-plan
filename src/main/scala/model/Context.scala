package model

import model.exception.InvalidActionListException

/**
  * A complete context for a planning operation.
  */
trait Context {

  def initialState: State
  def goal: State
  def actions: List[Action]
  /**
    * Get all the literals involved in the context.
    * @return
    */
  def literals: Set[Literal]
  def containActionName(action: String): Boolean
  def getActionByName(name: String): Option[Action]
  def getActionsThatAdd(literal: Literal): List[Action]
  def getActionsThatDelete(literal: Literal): List[Action]
}

/**
  * Context companion object.
  */
object Context {

  def apply(initialState: State, goal: State, actions: List[Action]): Context = new ContextImpl(initialState, goal, actions)

  private class ContextImpl(override val initialState: State, override val goal: State, override val actions: List[Action]) extends Context {

    if(actions.distinct.size != actions.size) throw new InvalidActionListException()

    override def containActionName(actionName: String): Boolean = {
      for (action <- this.actions) {
        if (action.name == actionName) return true
      }
      false
    }

    override def getActionByName(name: String): Option[Action] = {
      for (action <- this.actions) {
        if (action.name == name) return Option(action)
      }
      Option.empty
    }

    override def literals: Set[Literal] = {
      var literalSet: Set[Literal] = Set()
      for (action <- this.actions) literalSet = literalSet ++ action.literals
      for (literal <- initialState.literals) literalSet = literalSet + literal
      for (literal <- goal.literals) literalSet = literalSet + literal
      literalSet
    }

    override def getActionsThatAdd(literal: Literal): List[Action] = actions.filter(action => action.isInAddList(literal))

    override def getActionsThatDelete(literal: Literal): List[Action] = actions.filter(action => action.isInDeleteList(literal))
  }
}


