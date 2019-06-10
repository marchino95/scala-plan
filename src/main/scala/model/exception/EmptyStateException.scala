package model.exception

class EmptyStateException extends Exception {

  override def getMessage: String = "The state cannot be empty!"
}
