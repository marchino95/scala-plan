package model.plan

import model.exception.MalformedPlanException
import model.{Action, Context}
import net.sf.tweety.logics.pl.parser.PlParser
import net.sf.tweety.logics.pl.sat.Sat4jSolver
import net.sf.tweety.logics.pl.sat.SatSolver
import net.sf.tweety.logics.pl.syntax.PropositionalFormula

/**
  * The SAT-plan solver.
  */
trait SATPlanSolver {
  @throws[MalformedPlanException]
  def buildPlan: Option[List[Action]]
}

/**
  * The SATPlanSolver companion object.
  */
object SATPlanSolver {

  /**
    * Build a Sat4J-based solver.
    * @param context
    * @param satFormulation the k-bounded plan
    * @return
    */
  def SAT4JStringSolver(context: Context, satFormulation: String): SATPlanSolver = new SAT4JStringSolverImpl(context, satFormulation)

  private class SAT4JStringSolverImpl(val context: Context, val satFormulation: String) extends SATPlanSolver {

    @throws[MalformedPlanException]
    override def buildPlan: Option[List[Action]] = {
      val parser: PlParser = new PlParser()
      SatSolver.setDefaultSolver(new Sat4jSolver)
      try {
        val formula: PropositionalFormula = parser.parseFormula(this.satFormulation)
        val positiveClauses = Option(SatSolver.getDefaultSolver.getWitness(formula))
        if (!positiveClauses.isDefined) return Option.empty
        // Obtain a sorted action list from the positive clauses
        val actionList: List[Action] = positiveClauses.get.toString
          .split(", ")
          .map((clause: String) => clause.replace("[", ""))
          .map((clause: String) => clause.replace("]", ""))
          .map{(clause: String) =>
            val splittedClause: Array[String] = clause.split("_")
            val time: Int = splittedClause(0).toInt
            val propositionName: String = splittedClause(1)
            (time, propositionName)
          }
          .filter{ case (time, propositionName) => this.context.containActionName(propositionName) }
          .sortBy(_._1)
          .map{ case (time, actionName) => this.context.getActionByName(actionName).get }
          .toList
        Option(actionList)
      } catch {
        case e: Exception =>
          e.printStackTrace()
          throw new MalformedPlanException
      }
    }
  }
}
