package org.openjfx;

import java.io.IOException;
import java.sql.*;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

public class PrimaryController {

    private Connection connection = Database.getConnection();

    public Button loginButton;
    public TextField username;
    public PasswordField password;
    public Label invalidInput;

    public void loginFunction() {
        boolean logged = false;
        String username = this.username.getText();
        String password = this.password.getText();

        try {
            CallableStatement statement = connection.prepareCall(
                    "BEGIN"
                            + " ? := CASE WHEN (MANAGER_ACCOUNTS.LOGIN(?, ?)) "
                            + "       THEN 1 "
                            + "       ELSE 0"
                            + "      END;"
                            + "END;");

            statement.registerOutParameter(1, Types.INTEGER);
            statement.setString(2, username);
            statement.setString(3, password);

            statement.execute();

            logged = (statement.getInt(1) == 1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (logged) {
            try {
                App.setRoot("secondary");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else
            invalidInput.setVisible(true);
    }

    public void goToSignUp(MouseEvent mouseEvent) {
        try {
            App.setRoot("signUp");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
