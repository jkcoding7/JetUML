package org.jetuml.gui;

import javafx.scene.Scene;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

/**
 * 
 */
public class UserGuideView 
{
	private Stage aStage = new Stage();
	private WebView aView = new WebView();
	
	public UserGuideView()
	{
		//aView.getEngine().load("https://www.jetuml.org/docs/user-guide.html");
		//aView.getEngine().loadContent("aaaaaaaa**header**", "text/markdown");
		aView.getEngine().load("https://www.jetuml.org/docs/user-guide.html");
		aStage.setScene(new Scene(aView));
	}
	
	public void show()
	{
		aStage.setResizable(true);
		aStage.showAndWait();
	}
}
