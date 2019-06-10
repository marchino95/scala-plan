package model.plan

import model.{Context, State}
import model.plan.sat4j.ModelToString._
import model.plan.sat4j.{ActionBuilder, StateBuilder}

/**
  * A plan limited to k max steps .
  * @tparam X the type of the plan description
  */
trait KBoundedPlan[X] {

  def extendsTo(bound: Int): Unit
  def turnsIntoSatProblem: X
}

/**
  * KBoundedPlan companion object.
  */
object KBoundedPlan {

  /**
    * Create a KBoundedPlan using the string formulation.
    * @param k step limit
    * @param context
    * @param goal
    * @return
    */
  def createStringPlan(k: Int, context: Context, goal: State): KBoundedPlan[String] = new KBoundedStringPlanImpl(k, context, goal)

  private class KBoundedStringPlanImpl(var k: Int, val context: Context, val goal: State) extends KBoundedPlan[String] {

    if (k <= 0) throw new IllegalStateException("Invalid value of k!")

    var cachedK: Int = 0
    var cache: String = StateBuilder(this.context, this.context.initialState, 0)

    override def extendsTo(bound: Int): Unit = if (bound > this.k) this.k = bound

    override def turnsIntoSatProblem: String = {
      if (this.cachedK == this.k) {
        and(this.cache, StateBuilder(this.context, this.goal, this.k, includeNegativeLiterals = false))
      } else {
        val actions: String = ActionBuilder(this.context, this.cachedK, this.k)
        this.cache = and(this.cache, actions)
        this.cachedK = k
        and(this.cache, StateBuilder(this.context, this.goal, this.k, includeNegativeLiterals = false))
      }
    }
  }
}

