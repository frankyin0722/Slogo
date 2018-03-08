package view.vis_elements;

import buttons.TabButton;
import javafx.scene.Scene;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import view.IVisualConstants;

public class TabView implements IVisualConstants {

	private TabPane tabs;
	private AnchorPane myAnchor;
	private Scene myScene;

	public TabView() {
		initializePanes();
	}
	
	private void initializePanes() {
		tabs = new TabPane();
		myAnchor = new AnchorPane();
		TabButton newTab = new TabButton(tabs);
		AnchorPane.setRightAnchor(newTab, 10.0);
		newTab.createTab();
		myAnchor.getChildren().addAll(tabs, newTab);
		myScene = new Scene(myAnchor,INITIAL_SCENE_WIDTH,INITIAL_SCENE_HEIGHT);
		myAnchor.setBackground(new Background(new BackgroundFill(INITIAL_COLOR, null, null)));
	}

	
	public Scene getScene() {
		return myScene;
	}
}
