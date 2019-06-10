package view

import controller.Controller
import javafx.application.Application
import javafx.collections.FXCollections
import javafx.event.ActionEvent
import javafx.fxml.{FXML, FXMLLoader}
import javafx.scene.control.Alert.AlertType
import javafx.scene.{Parent, Scene}
import javafx.scene.control._
import javafx.stage.{Modality, Stage}

class LaunchDashboard extends Application {

  override def start(primaryStage: Stage): Unit = {
    val loaderDashboard: FXMLLoader = new FXMLLoader(getClass.getResource("/GuiScalaPlan.fxml"))
    val sceneDashboard = new Scene(loaderDashboard.load())
    primaryStage.setTitle("ScalaPlan")
    primaryStage.setScene(sceneDashboard)
    primaryStage.show()
  }
  
  override def stop(): Unit = System.exit(0)
}

/**
  * The view controller for the main scene.
  */
class DashboardController {

  @FXML
  var initialStateTextArea: TextArea = _

  @FXML
  var goalTextArea: TextArea = _

  @FXML
  var solveButton: Button = _

  @FXML
  var addActionButton: Button = _

  @FXML
  var deleteActionButton: Button = _

  @FXML
  var actionListView: ListView[String] = _

  @FXML
  var maxLengthSlider: Slider = _

  @FXML
  var lengthLabel: Label = _

  @FXML 
  def solveButtonClick(event: ActionEvent): Unit = if(goalTextArea.getText.isEmpty
    || initialStateTextArea.getText.isEmpty
    || actionListView.getItems.isEmpty()) {
      showAlert("Error", "You must enter a goal, an initial state and a list of actions!")
  } else {
    try {
      val plan: Option[List[String]] = Controller.solve(initialStateTextArea.getText.split(",").toList, goalTextArea.getText.split(",").toList, maxLengthSlider.getValue.toInt)
      if(plan.isEmpty) {
        showAlert("Result", "A valid plan does not exist...")
      } else {
        showPlan(plan.get)
      }
    } catch {
      case e:Exception => showAlert("Error", e.getMessage)
    }
  }

  @FXML 
  def addActionButtonClick(event: ActionEvent): Unit = {
    try {
      val fxmlLoader = new FXMLLoader(getClass.getResource("/GuiAddActionDialog.fxml"))
      val root1 = fxmlLoader.load.asInstanceOf[Parent]
      val stage = new Stage
      stage.initModality(Modality.APPLICATION_MODAL)
      stage.setScene(new Scene(root1))
      stage.setTitle("Create a new Action")
      stage.showAndWait()
      refreshActionView()
    } catch { case e: Exception => }
  }

  def refreshActionView(): Unit = {
    val list = FXCollections.observableArrayList[String]()
    actionListView.refresh()
    Controller.getActions.foreach(action => list.add(action))
    actionListView.setItems(list)
  }

  @FXML 
  def deleteActionButtonClick(event: ActionEvent): Unit = if (actionListView.getSelectionModel.getSelectedItem != null) {
    val actionSelectedName: String = actionListView.getSelectionModel.getSelectedItem.split("\nPRE")(0)
    Controller.deleteAction(actionSelectedName)
    refreshActionView()
  }

  def showAlert(title: String, message: String): Unit = {
    val alert = new Alert(AlertType.INFORMATION)
    alert.setTitle(title)
    alert.setHeaderText(message)
    alert.showAndWait()
  }

  def showPlan(actions: List[String]): Unit = {
    val plan: String = actions.mkString("\n")
    val alert = new Alert(AlertType.INFORMATION)
    alert.setTitle("Result")
    alert.setHeaderText("Perfect! There is a plan!")
    alert.setContentText(plan)
    alert.showAndWait()
  }

  @FXML
  def sliderValueChanged(): Unit = lengthLabel.setText(maxLengthSlider.getValue.toInt.toString)

}

