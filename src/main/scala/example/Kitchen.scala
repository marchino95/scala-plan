package example

import model.plan.{KBoundedPlan, SATPlanSolver}
import model.{Action, Context, Literal, State}

/**
  * The Kitchen example used to test the application.
  */
object Kitchen extends App {

  val initialState = State(List(Literal("meat"), Literal("chips"), Literal("oil"), Literal("clean")))

  val goal = State(List(Literal("hamburger"), Literal("friedChips"), Literal("clean")))

  // Cook Hamburgher
  val cookHamburgerPre = List(Literal("meat"), Literal("clean"))
  val cookHamburgerAdd = List(Literal("hamburger"), Literal("dirty"))
  val cookHamburgerDel = List(Literal("meat"), Literal("clean"))

  val cookHamburger = Action("cookHamburger", cookHamburgerPre, cookHamburgerAdd, cookHamburgerDel)

  // Fry Chips
  val fryChipsPre = List(Literal("chips"), Literal("oil"), Literal("clean"))
  val fryChipsAdd = List(Literal("friedChips"), Literal("dirty"))
  val fryChipsDel = List(Literal("chips"), Literal("oil"), Literal("clean"))

  val fryChips = Action("fryChips", fryChipsPre, fryChipsAdd, fryChipsDel)

  // Clean
  val cleanPre = List(Literal("dirty"))
  val cleanAdd = List(Literal("clean"))
  val cleanDel = List(Literal("dirty"))

  val clean = Action("doClean", cleanPre, cleanAdd, cleanDel)

  val context = Context(initialState, goal, List(clean, fryChips, cookHamburger))

  val planner = KBoundedPlan.createStringPlan(4, context, goal)

  val solver = SATPlanSolver.SAT4JStringSolver(context, planner.turnsIntoSatProblem)

  println(planner.turnsIntoSatProblem)
  val plan = solver.buildPlan
  println(if (plan.isDefined) { plan.get.map(action => action.name) } else Option.empty)

}
