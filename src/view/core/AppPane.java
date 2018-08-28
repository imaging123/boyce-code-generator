package view.core;

import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;

/**
 * @author boyce - 2018/1/16.
 */
public class AppPane extends BorderPane {
    private HeadPane headPane;
    private Pane bodyPane = EMPTY_PANE;

    public AppPane() {
        headPane = new HeadPane(this);
        this.setTop(headPane);
        this.setCenter(bodyPane);
    }

    private static final Pane EMPTY_PANE = new EmptyPane();

    private static class EmptyPane extends BorderPane {
        private EmptyPane() {
            Label centerIntro = new Label(
                "1. Choose a resources.template. ");
            centerIntro.setFont(Font.font(18.0));
            this.setCenter(centerIntro);
        }
    }

    public void changeBody() {
        this.bodyPane = new JavaTemplateBodyPane();
        this.bodyPane.setVisible(true);
        this.setCenter(bodyPane);
    }
}
