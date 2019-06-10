package model.exception

class InvalidLiteralException extends Exception {

  override def getMessage: String = "The literal is invalid!"
}
