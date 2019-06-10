package model

import model.exception.EmptyStateException

/**
  * A specific state of the world.
  */
trait State {
  def literals: List[Literal]
}

/**
  * State companion object.
  */
object State {

  def apply(literals: List[Literal]): State = new StateImpl(literals)

  private class StateImpl(override val literals: List[Literal]) extends State {

    if(literals.isEmpty) throw new EmptyStateException()

    override def hashCode(): Int = {
      val state = Seq(literals)
      state.map(_.hashCode()).foldLeft(0)((a, b) => 31 * a + b)
    }

    override def toString: String = literals.map(literal => literal.name).mkString(", ")
  }
}

