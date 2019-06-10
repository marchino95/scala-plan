package model.exception

class MalformedPlanException extends Exception {

  override def getMessage: String = "The plan is not valid!"
}
