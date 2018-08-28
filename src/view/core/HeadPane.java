package view.core;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;

/**
 * @author boyce - 2018/1/16.
 */
public class HeadPane extends MenuBar {
    private AppPane app;
    public HeadPane(AppPane app) {
        super();
        this.app = app;
        //this.setPadding(new Insets(10, 10, 10, 10));
        //this.setMinWidth(600);

        Menu templates = new Menu("模板");

        MenuItem javaTemplate = new MenuItem("JavaTemplate");
        javaTemplate.setOnAction(action -> {
            this.app.changeBody();
        });
        templates.getItems().add(javaTemplate);


        this.setStyle("-fx-background-color: rgba(205,248,241,0.95)");
        this.getMenus().add(templates);
    }
}
