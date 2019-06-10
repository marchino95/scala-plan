package model

import model.exception.InvalidActionException

/**
  * A STRIPS-based action.
  */
trait Action {

  def name: String
  def preconditions: List[Literal]
  def addList: List[Literal]
  def deleteList: List[Literal]
  def hasPreconditions: Boolean
  /**
    * Obtain all the literals involved in the action.
    * @return
    */
  def literals: Set[Literal]
  def isInAddList(literal: Literal): Boolean
  def isInDeleteList(literal: Literal): Boolean
}

/**
  * Action companion object.
  */
object Action {

  def apply(name: String, preconditions: List[Literal] = List(), addList: List[Literal], deleteList: List[Literal]): Action =
    new ActionImpl(name.replaceAll("[^A-Za-z]+", ""), preconditions, addList, deleteList)

  private class ActionImpl(override val name: String, override val preconditions: List[Literal],
                           override val addList: List[Literal], override val deleteList: List[Literal]) extends Action {

    if(name.isEmpty || (addList.isEmpty && deleteList.isEmpty)) throw new InvalidActionException()

    override def hasPreconditions: Boolean = !this.preconditions.isEmpty

    override def equals(obj: Any): Boolean = {
      if (this == obj) return true
      if (obj == null) return false
      if (getClass ne obj.getClass) return false
      val other = obj.asInstanceOf[ActionImpl]
      if (!(name == other.name)) return false
      true
    }

    override def hashCode(): Int = {
      val state = Seq(name, preconditions, addList, deleteList)
      state.map(_.hashCode()).foldLeft(0)((a, b) => 31 * a + b)
    }

    override def isInAddList(literal: Literal): Boolean = this.addList.contains(literal)

    override def isInDeleteList(literal: Literal): Boolean = this.deleteList.contains(literal)

    override def literals: Set[Literal] = this.preconditions.toSet ++ this.addList.toSet ++ this.deleteList.toSet

    override def toString: String = this.name + "\nPRE: " + this.preconditions.map(literal => literal.name).mkString(", ") +
      "\nADD: " + this.addList.map(literal => literal.name).mkString(", ") + "\nDEL: " + this.deleteList.map(literal => literal.name).mkString(", ")

  }
}

