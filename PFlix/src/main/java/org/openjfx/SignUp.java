package org.openjfx;

import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import org.apache.commons.validator.routines.EmailValidator;

import java.io.IOException;
import java.sql.*;
import java.util.concurrent.TimeUnit;

public class SignUp {
    public Label inputError;
    public TextField username;
    public PasswordField password;
    public PasswordField confirmPassword;
    public TextField email;
    public DatePicker birthDate;
    public Button signUpButton;
    public TextField originCountry;
    public Button backToLoginButton;

    private Connection connection = Database.getConnection();

    public void inputVerify(MouseEvent mouseEvent) {
        String errors = "";

        String username = this.username.getText();
        String password = this.password.getText();
        String confirmPassword = this.confirmPassword.getText();
        String email = this.email.getText();
        String originCountry = this.originCountry.getText();

        if (usernameAlreadyExist(username))
            errors += "Username already exist\n";
        else if (!(username.trim().length() > 3 && username.trim().length() <= 30))
            errors += "Username must be between 3 and 30\n";

        if (!password.equals(confirmPassword))
            errors += "Password must match.\n";

        if (emailAlreadyExist(email))
            errors += "Email is already used.\n";
        else if (!EmailValidator.getInstance().isValid(email))
            errors += "Email is not valid\n";

        Date birthDate = new Date(new java.util.Date().getTime());
        if (this.birthDate.getValue() == null)
            errors += "You must enter a birth date";
        else {
            birthDate = Date.valueOf(this.birthDate.getValue());
            if (!verifyBirthDate(birthDate))
                errors += "You must have minimum ten year.\n";
        }

        if (errors.length() > 0) {
            inputError.setText(errors);
            return;
        }

        try {
            CallableStatement callableStatement = connection.prepareCall("{CALL MANAGER_ACCOUNTS.ADD_USER(?, ?, ?, 'm', ?, ?)}");
            callableStatement.setString(1, username);
            callableStatement.setString(2, password);
            callableStatement.setString(3, email);
            callableStatement.setDate(4, birthDate);
            callableStatement.setString(5, originCountry);
            if (callableStatement.execute())
                connection.commit();
            else
                connection.rollback();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            App.setRoot("primary");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean usernameAlreadyExist(String username) {
        try {
            CallableStatement statement = connection.prepareCall(
                    "BEGIN"
                            + " ? := CASE WHEN (MANAGER_ACCOUNTS.USERNAME_ALREADY_EXISTS(?)) "
                            + "       THEN 1 "
                            + "       ELSE 0"
                            + "      END;"
                            + "END;");

            statement.registerOutParameter(1, Types.INTEGER);
            statement.setString(2, username);

            statement.execute();

            return (statement.getInt(1) == 1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }

    private boolean emailAlreadyExist(String email) {
        try {
            CallableStatement statement = connection.prepareCall(
                    "BEGIN"
                            + " ? := CASE WHEN (MANAGER_ACCOUNTS.EMAIL_ALREADY_EXISTS(?)) "
                            + "       THEN 1 "
                            + "       ELSE 0"
                            + "      END;"
                            + "END;");

            statement.registerOutParameter(1, Types.INTEGER);
            statement.setString(2, email);

            statement.execute();

            return (statement.getInt(1) == 1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }

    private boolean verifyBirthDate(Date birthDate) {
        long diffInMillies = Math.abs(new java.util.Date().getTime() - birthDate.getTime());
        long diff = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
        return diff < 3650;
    }

    public void backToLogin(MouseEvent mouseEvent) {
        try {
            App.setRoot("primary");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
