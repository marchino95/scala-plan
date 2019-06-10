package view

import controller.Controller
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.Node
import javafx.scene.control.Alert.AlertType
import javafx.scene.control.{Alert, TextArea, TextField}
import javafx.stage.Stage

/**
  * The view controller for adding an action.
  */
class AddActionDialogController {

  @FXML
  var actionNameTextField: TextField = _

  @FXML
  var preconditionsTextArea: TextArea = _

  @FXML
  var addListTextArea: TextArea = _

  @FXML
  var deleteListTextArea: TextArea = _

  @FXML 
  def addActionButtonClick(event: ActionEvent): Unit = {
    try {
      Controller.addAction(actionNameTextField.getText,
        preconditionsTextArea.getText.split(",").filterNot(_.isEmpty).toList
          .map(atom => atom.replace(" ", "")),
        addListTextArea.getText.split(",").filterNot(_.isEmpty).toList
          .map(atom => atom.replace(" ", "")),
        deleteListTextArea.getText.split(",").filterNot(_.isEmpty).toList
          .map(atom => atom.replace(" ", "")))
    } catch {
      case e:Exception => showAlert("Error", e.getMessage)
    } finally {
      val source = event.getSource.asInstanceOf[Node]
      val stage = source.getScene.getWindow.asInstanceOf[Stage]
      stage.close()
    }
  }

  def showAlert(title: String, message: String): Unit = {
    val alert = new Alert(AlertType.INFORMATION)
    alert.setTitle(title)
    alert.setHeaderText(message)
    alert.showAndWait()
  }

}

