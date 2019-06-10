package model.exception

class InvalidActionListException extends Exception {

  override def getMessage: String = "The action list is ambiguous!"
}
