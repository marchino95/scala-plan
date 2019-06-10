package model.exception

class InvalidActionException extends Exception {

  override def getMessage: String = "The action is not valid"
}
