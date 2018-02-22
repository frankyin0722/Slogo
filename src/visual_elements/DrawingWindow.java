package visual_elements;

import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import turtle.Turtle;

public class DrawingWindow extends Pane {
	public static final int INITIAL_WIDTH = 600;
	public static final int INITIAL_HEIGHT = 400;
	private Turtle myTurtle;

	public DrawingWindow(Group root) {
		setupInitialCanvas();
		
		this.getChildren().addAll(testing());
		root.getChildren().addAll(this);
	}
	
	
	private void setupInitialCanvas() {
		this.setPrefSize(INITIAL_WIDTH, INITIAL_HEIGHT);
		this.setBackground(new Background(new BackgroundFill(Color.ALICEBLUE, CornerRadii.EMPTY, Insets.EMPTY)));
	}
	
	
	
	private Rectangle testing() {
		Rectangle rect = new Rectangle(20, 20, 100, 100);
		rect.setFill(Color.AQUAMARINE);
		return rect;
	}
	
//	private void setupTurtle(Group root) {
//		
//	}
	
}
