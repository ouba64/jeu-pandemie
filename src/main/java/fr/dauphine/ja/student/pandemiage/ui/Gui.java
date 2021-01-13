package fr.dauphine.ja.student.pandemiage.ui;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import fr.dauphine.ja.student.pandemiage.gameengine.GameEngine;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;

/**
 * Stupid empty scene in JavaFX
 * 
 *
 */
public class Gui extends Application {
	public static final String DEFAULT_AIJAR = "./target/pandemiage-1.0-SNAPSHOT-ai.jar";
	public static final String DEFAULT_CITYGRAPH_FILE = "pandemic.graphml";
	public static final int DEFAULT_TURN_DURATION = 1; // in seconds
	public static final int DEFAULT_DIFFICULTY = 0; // Normal
	public static final int DEFAULT_HAND_SIZE = 9;
	Pane gBoard;

	@Override
	public void start(Stage stage) {

		gBoard = null;
		try {
			gBoard = FXMLLoader.load(getClass().getResource("pandemic.fxml"));
		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		URL url1 = getClass().getResource("board.jpg");
		URL url2 = getClass().getClassLoader().getResource("board.jpg");
		InputStream is = getClass().getClassLoader().getResourceAsStream("board.jpg");
		FileInputStream input = null;
		try {
			input = new FileInputStream(url2.getPath());
		}
		catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Image image = new Image(is);
		ImageView iv1 = new ImageView();
		iv1.setImage(image);
		((ImageView) gBoard.getChildren().get(0)).setImage(image);

		BackgroundFill color1 = new BackgroundFill(Color.TRANSPARENT, null, null);
		BackgroundFill colorTemp = new BackgroundFill(Color.ALICEBLUE, null, null);

		VBox vBox = (VBox) gBoard.getChildren().get(1);
		ScrollPane scrollPane = (ScrollPane) vBox.getChildren().get(0);

		scrollPane.setBackground(new Background(colorTemp));

		scrollPane = (ScrollPane) vBox.getChildren().get(1);
		scrollPane.setBackground(new Background(colorTemp));

		Scene scene = new Scene(gBoard, 1890, 990);

		stage.setTitle("Pandemiage");
		stage.setScene(scene);
		stage.setResizable(false);
		stage.show();

		String aijar = DEFAULT_AIJAR;
		String cityGraphFile = DEFAULT_CITYGRAPH_FILE;
		int difficulty = DEFAULT_DIFFICULTY;
		int turnDuration = DEFAULT_TURN_DURATION;
		int handSize = DEFAULT_HAND_SIZE;
		int seed = 1;
		int nPlayers = 1;

		boolean isGui = true;
		boolean isConsole = true;

		Gui gui = this;
			
        // create a new Thread and start gameEngine in it
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                // create and start
        		GameEngine g = new GameEngine(aijar, cityGraphFile, turnDuration, seed, difficulty, nPlayers, handSize, isGui,
        				isConsole, gui);
        		g.loop();
            }
        });
        // don't let thread prevent JVM shutdown
        thread.setDaemon(true);
        thread.start();	
	}

	public void add3dCube(Group root) {
		PerspectiveCamera camera;
		Rotate cameraRotateX, cameraRotateY, cameraRotateZ;
		Translate cameraTranslate;
		// Create Camera
		camera = new PerspectiveCamera(true);
		cameraRotateX = new Rotate(-20, Rotate.X_AXIS);
		cameraRotateY = new Rotate(30, Rotate.Y_AXIS);
		cameraRotateZ = new Rotate(40, Rotate.Z_AXIS);
		cameraTranslate = new Translate(50, 120, -200);
		camera.getTransforms().addAll(cameraRotateX, cameraRotateY, cameraRotateZ, cameraTranslate);
		root.getChildren().add(camera);
		// Create Material
		PhongMaterial phongMaterial = new PhongMaterial();
		phongMaterial.setDiffuseColor(Color.DARKRED);
		phongMaterial.setSpecularColor(Color.RED);
		// Create Box
		int size = 50;
		Box box = new Box(size, size, size);
		box.setMaterial(phongMaterial);
		root.getChildren().add(box);
	}

	public static void main(String[] args) {
		launch();
	}

	public Pane getgBoard() {
		return gBoard;
	}

	public void setgBoard(Pane gBoard) {
		this.gBoard = gBoard;
	}

	public Node createPlayer() {
		InputStream is = getClass().getClassLoader().getResourceAsStream("images/pawn-orange.png");
		Image image = new Image(is);
		ImageView iv1 = new ImageView();
		iv1.setImage(image);
		return iv1;
	}
}