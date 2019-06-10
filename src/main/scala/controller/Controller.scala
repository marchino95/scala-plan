package controller

import model.plan.{KBoundedPlan, SATPlanSolver}
import model.{Action, Context, Literal, State}

import scala.collection.mutable

/**
  * The App controller that connects View and Model (MVC pattern).
  */
object Controller {

  private val actions: mutable.HashMap[String, Action] = mutable.HashMap()

  def addAction(name: String, preconditions: List[String], addList: List[String], deleteList: List[String]): Unit =
    actions += (name -> Action(name, preconditions.map(Literal(_)), addList.map(Literal(_)), deleteList.map(Literal(_))))

  def deleteAction(name: String): Unit = actions.remove(name)

  def getActions: List[String] = actions.map(_._2).map(_.toString).toList

  def solve(initialStateList: List[String], goalList: List[String], maxLength: Int): Option[List[String]] = {
    val initialState: State = State(initialStateList.map(Literal(_)))
    val goal: State = State(goalList.map(Literal(_)))
    val context = Context(initialState, goal, actions.values.toList)
    val planner = KBoundedPlan.createStringPlan(1, context, goal)
    for(length <- 1 to maxLength) {
      planner.extendsTo(length)
      val solver = SATPlanSolver.SAT4JStringSolver(context, planner.turnsIntoSatProblem)
      val solution: Option[List[Action]] = solver.buildPlan
      if(solution.isDefined) return Option(solution.get.map(action => action.name))
    }
    Option.empty
  }

}
