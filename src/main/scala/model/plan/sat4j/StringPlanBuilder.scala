package model.plan.sat4j

import model.{Action, Context, Literal, State}
import ModelToString._

import scala.collection.mutable.ListBuffer

/**
  * A builder that encode a State as String.
  */
object StateBuilder {

  def apply(context: Context, state: State, time: Int, includeNegativeLiterals: Boolean = true): String = if(includeNegativeLiterals) {
    context.literals
      .map((literal) => if(state.literals.contains(literal)) timeLiteral(literal, time) else not(timeLiteral(literal, time)))
      .reduce((first, second) => and(first, second))
  } else {
    state.literals.map(literal => timeLiteral(literal, time)).reduce((first, second) => and(first, second))
  }
}

/**
  * A builder that encode an Action as String.
  * The creation of axioms follows the strategy inside the book:
  * "Artificial Intelligence: A Modern Approach, Stuart Russell & Peter Norvig".
  */
object ActionBuilder {

  def apply(context: Context, startTime: Int, endTime: Int): String = {

    val actions: List[Action] = context.actions
    val fluents: Set[Literal] = context.literals
    val successorAxiomList: ListBuffer[String] = ListBuffer()
    val preconditionAxiomList: ListBuffer[String] = ListBuffer()
    val exclusiveActionAxiomList: ListBuffer[String] = ListBuffer()

    val addFluentsActionMap: Map[Literal, List[Action]] = fluents.map(literal => (literal, context.getActionsThatAdd(literal))).toMap
    val deleteFluentsActionMap: Map[Literal, List[Action]] = fluents.map(literal => (literal, context.getActionsThatDelete(literal))).toMap

    for(i <- startTime until endTime) {

      val currentTime: Int = i
      val nextTime: Int = i + 1

      // Successor Axioms

      for(fluent <- fluents) {

        val actionsAddFluent: List[Action] = addFluentsActionMap(fluent)
        val actionsDelFluent: List[Action] = deleteFluentsActionMap(fluent)

        val antecedent: String = timeLiteral(fluent, nextTime)
        var consequent: String = ""

        if(actionsAddFluent.isEmpty && actionsDelFluent.isEmpty) {
          consequent = timeLiteral(fluent, currentTime)
        } else if(actionsAddFluent.isEmpty) {
          consequent = close(and(timeLiteral(fluent, currentTime),
            not(or(actionsDelFluent.map(action => timeAction(action, currentTime))))))
        } else if(actionsDelFluent.isEmpty) {
          consequent = close(or(close(or(actionsAddFluent.map(action => timeAction(action, currentTime)))), timeLiteral(fluent, currentTime)))
        } else {
          consequent = or(or(actionsAddFluent.map(action => timeAction(action, currentTime))), close(and(timeLiteral(fluent, currentTime),
            not(or(actionsDelFluent.map(action => timeAction(action, currentTime)))))))
        }

        val successorAxiom: String = doubleImply(antecedent, consequent)
        successorAxiomList += successorAxiom
      }

      // Precondition Axioms

      val preconditionActionAxiomsAtCurrentTime: Option[String] =
        actions.filter(action => action.hasPreconditions).map{action => imply(timeAction(action, currentTime),
          and(action.preconditions.map(precondition => timeLiteral(precondition, currentTime))))}
        .reduceOption((first, second) => and(first, second))

      if(preconditionActionAxiomsAtCurrentTime.isDefined)
        preconditionAxiomList += preconditionActionAxiomsAtCurrentTime.get

      // Exclusive Axioms

      if(actions.size > 1) {
        val exclusiveActionsAtCurrentTime: List[String] = actions.map(action => not(timeAction(action, currentTime)))

        // Add axiom for each pair of actions
        var exclusiveActionPair: ListBuffer[String] = ListBuffer()
        for(actionOne <- exclusiveActionsAtCurrentTime; actionTwo <- exclusiveActionsAtCurrentTime) {
          if(actionOne != actionTwo) exclusiveActionPair += close(or(actionOne, actionTwo))
        }

        exclusiveActionAxiomList += and(exclusiveActionPair.toSet)
      }

    }

    and(preconditionAxiomList ++= successorAxiomList ++= exclusiveActionAxiomList)

  }
}


