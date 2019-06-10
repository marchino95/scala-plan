package model

import model.exception.InvalidLiteralException

/**
  * A simple fluent that represent an entity.
  */
trait Literal {

  def name: String
}

/**
  * Literal companion object.
  */
object Literal {

  def apply(name: String): Literal = new LiteralImpl(name.replaceAll("[^A-Za-z]+", ""))

  private class LiteralImpl(override val name: String) extends Literal {

    if (name.isEmpty) throw new InvalidLiteralException()

    def canEqual(other: Any): Boolean = other.isInstanceOf[LiteralImpl]

    /**
      * Two literals are equal if they have the same name.
      * @param other
      * @return
      */
    override def equals(other: Any): Boolean = other match {
      case that: LiteralImpl => that.name == this.name
      case _ => false
    }

    override def hashCode(): Int = {
      val state = Seq(name)
      state.map(_.hashCode()).foldLeft(0)((a, b) => 31 * a + b)
    }

    override def toString: String = this.name
  }
}


