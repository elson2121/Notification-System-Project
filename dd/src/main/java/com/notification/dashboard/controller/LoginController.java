package com.notification.dashboard.controller;

import com.notification.Main; // ADD THIS IMPORT
import com.notification.auth.AuthService;
import com.notification.dashboard.util.AlertHelper;
import com.notification.model.User; // ADD THIS IMPORT
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

public class LoginController implements Initializable {
    
    // Login Form Elements
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private TextField visiblePasswordField;
    @FXML private CheckBox showPasswordCheckbox;
    @FXML private CheckBox rememberMeCheckbox;
    @FXML private Button loginButton;
    @FXML private Hyperlink forgotPasswordLink;
    @FXML private Hyperlink registerLink;
    @FXML private HBox passwordContainer;
    @FXML private VBox loginForm;
    @FXML private VBox rightPanel;
    
    // Registration Form Elements
    @FXML private VBox registerForm;
    @FXML private TextField regUsernameField;
    @FXML private TextField regEmailField;
    @FXML private PasswordField regPasswordField;
    @FXML private TextField regVisiblePasswordField;
    @FXML private PasswordField regConfirmPasswordField;
    @FXML private CheckBox regShowPasswordCheckbox;
    @FXML private CheckBox regTermsCheckbox;
    @FXML private RadioButton regUserRole;
    @FXML private RadioButton regAdminRole;
    @FXML private Button registerButton;
    @FXML private Button regBackButton;
    @FXML private Button altBackButton;
    @FXML private Hyperlink backToLoginLink;
    @FXML private Hyperlink termsLink;
    @FXML private HBox regPasswordContainer;
    
    // Error Labels
    @FXML private Label regUsernameError;
    @FXML private Label regEmailError;
    @FXML private Label regPasswordError;
    @FXML private Label regConfirmPasswordError;
    @FXML private Label regRoleError;
    @FXML private Label regTermsError;
    
    // ToggleGroup
    @FXML private ToggleGroup roleGroup;
    
    private final AuthService authService = new AuthService();
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ensureElementsVisible();
        setupLoginForm();
        setupRegistrationForm();
        setupAnimations();
        initializeToggleGroup();
        setupResizeHandling();
    }
    
    private void ensureElementsVisible() {
        // ሁሉም ንጥረ ነገሮች እንዲታዩ ማረጋገጥ
        if (loginButton != null) {
            loginButton.setVisible(true);
            loginButton.setManaged(true);
        }
        
        if (registerButton != null) {
            registerButton.setVisible(true);
            registerButton.setManaged(true);
        }
        
        if (regBackButton != null) {
            regBackButton.setVisible(true);
            regBackButton.setManaged(true);
        }
        
        if (altBackButton != null) {
            altBackButton.setVisible(true);
            altBackButton.setManaged(true);
        }
        
        // የመግቢያ መስኮቶች
        if (usernameField != null) {
            usernameField.setVisible(true);
            usernameField.setManaged(true);
        }
        
        if (passwordField != null) {
            passwordField.setVisible(true);
            passwordField.setManaged(true);
        }
        
        // የምዝገባ መስኮቶች
        if (regUsernameField != null) {
            regUsernameField.setVisible(true);
            regUsernameField.setManaged(true);
        }
        
        if (regEmailField != null) {
            regEmailField.setVisible(true);
            regEmailField.setManaged(true);
        }
        
        if (regPasswordField != null) {
            regPasswordField.setVisible(true);
            regPasswordField.setManaged(true);
        }
        
        if (regConfirmPasswordField != null) {
            regConfirmPasswordField.setVisible(true);
            regConfirmPasswordField.setManaged(true);
        }
        
        // ምልክት ሳጥኖች
        if (showPasswordCheckbox != null) {
            showPasswordCheckbox.setVisible(true);
            showPasswordCheckbox.setManaged(true);
        }
        
        if (regShowPasswordCheckbox != null) {
            regShowPasswordCheckbox.setVisible(true);
            regShowPasswordCheckbox.setManaged(true);
        }
        
        if (regTermsCheckbox != null) {
            regTermsCheckbox.setVisible(true);
            regTermsCheckbox.setManaged(true);
        }
        
        // ራዲዮ አዝራሮች
        if (regUserRole != null) {
            regUserRole.setVisible(true);
            regUserRole.setManaged(true);
        }
        
        if (regAdminRole != null) {
            regAdminRole.setVisible(true);
            regAdminRole.setManaged(true);
        }
    }
    
    private void setupResizeHandling() {
        // የማያላቅ መጠን ሲቀይር ለማስተካከል
        if (loginForm != null && loginForm.getScene() != null) {
            loginForm.getScene().widthProperty().addListener((obs, oldVal, newVal) -> {
                adjustLayoutForSize(newVal.doubleValue(), loginForm.getScene().getHeight());
            });
            
            loginForm.getScene().heightProperty().addListener((obs, oldVal, newVal) -> {
                adjustLayoutForSize(loginForm.getScene().getWidth(), newVal.doubleValue());
            });
        }
    }
    
    private void adjustLayoutForSize(double width, double height) {
        // በማያላቅ መጠን ላይ በመመርኮዝ ንድፍን ማስተካከል
        if (width < 850 || height < 550) {
            adjustForSmallScreen();
        } else {
            adjustForNormalScreen();
        }
    }
    
    private void adjustForSmallScreen() {
        // ለአነስተኛ ማያላቅ መጠን ንድፍ
        if (loginButton != null) {
            loginButton.setStyle("-fx-font-size: 14px; -fx-padding: 10 20;");
        }
        
        if (registerButton != null) {
            registerButton.setStyle("-fx-font-size: 14px; -fx-padding: 10 20;");
        }
    }
    
    private void adjustForNormalScreen() {
        // ለመደበኛ ማያላቅ መጠን ንድፍ
        if (loginButton != null) {
            loginButton.setStyle("");
        }
        
        if (registerButton != null) {
            registerButton.setStyle("");
        }
    }
    
    private void initializeToggleGroup() {
        if (roleGroup == null) {
            roleGroup = new ToggleGroup();
        }
        
        if (regUserRole != null) {
            regUserRole.setToggleGroup(roleGroup);
        }
        
        if (regAdminRole != null) {
            regAdminRole.setToggleGroup(roleGroup);
        }
        
        if (regUserRole != null) {
            regUserRole.setSelected(true);
        }
    }
    
    private void setupLoginForm() {
        if (loginButton != null) {
            loginButton.setOnAction(event -> handleLogin());
        }
        
        if (usernameField != null) {
            usernameField.setOnKeyPressed(this::handleLoginEnterKey);
        }
        
        if (passwordField != null) {
            passwordField.setOnKeyPressed(this::handleLoginEnterKey);
        }
        
        if (visiblePasswordField != null) {
            visiblePasswordField.setOnKeyPressed(this::handleLoginEnterKey);
        }
        
        if (forgotPasswordLink != null) {
            forgotPasswordLink.setOnAction(event -> handleForgotPassword());
        }
        
        if (registerLink != null) {
            registerLink.setOnAction(event -> showRegistrationForm());
        }
        
        setupPasswordToggle();
    }
    
    private void setupRegistrationForm() {
        if (backToLoginLink != null) {
            backToLoginLink.setOnAction(event -> showLoginForm());
        }
        
        if (regBackButton != null) {
            regBackButton.setOnAction(event -> showLoginForm());
        }
        
        if (altBackButton != null) {
            altBackButton.setOnAction(event -> showLoginForm());
        }
        
        if (termsLink != null) {
            termsLink.setOnAction(event -> showTermsAndConditions());
        }
        
        if (registerButton != null) {
            registerButton.setOnAction(event -> handleRegistration());
        }
        
        if (regUsernameField != null) {
            regUsernameField.setOnKeyPressed(this::handleRegisterEnterKey);
        }
        
        if (regEmailField != null) {
            regEmailField.setOnKeyPressed(this::handleRegisterEnterKey);
        }
        
        if (regPasswordField != null) {
            regPasswordField.setOnKeyPressed(this::handleRegisterEnterKey);
        }
        
        if (regConfirmPasswordField != null) {
            regConfirmPasswordField.setOnKeyPressed(this::handleRegisterEnterKey);
        }
        
        setupRegistrationPasswordToggle();
        setupErrorClearing();
    }
    
    private void setupAnimations() {
        if (loginForm != null) {
            FadeTransition fade = new FadeTransition(Duration.millis(500), loginForm);
            fade.setFromValue(0);
            fade.setToValue(1);
            fade.play();
        }
    }
    
    private void setupPasswordToggle() {
        if (showPasswordCheckbox == null || passwordField == null || visiblePasswordField == null) {
            return;
        }
        
        showPasswordCheckbox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                visiblePasswordField.setText(passwordField.getText());
                visiblePasswordField.setVisible(true);
                visiblePasswordField.setManaged(true);
                passwordField.setVisible(false);
                passwordField.setManaged(false);
                
                if (passwordContainer != null) {
                    passwordContainer.getChildren().clear();
                    passwordContainer.getChildren().add(visiblePasswordField);
                }
            } else {
                passwordField.setText(visiblePasswordField.getText());
                passwordField.setVisible(true);
                passwordField.setManaged(true);
                visiblePasswordField.setVisible(false);
                visiblePasswordField.setManaged(false);
                
                if (passwordContainer != null) {
                    passwordContainer.getChildren().clear();
                    passwordContainer.getChildren().add(passwordField);
                }
            }
        });
    }
    
    private void setupRegistrationPasswordToggle() {
        if (regShowPasswordCheckbox == null || regPasswordField == null || regVisiblePasswordField == null) {
            return;
        }
        
        regShowPasswordCheckbox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                regVisiblePasswordField.setText(regPasswordField.getText());
                regVisiblePasswordField.setVisible(true);
                regVisiblePasswordField.setManaged(true);
                regPasswordField.setVisible(false);
                regPasswordField.setManaged(false);
                
                if (regPasswordContainer != null) {
                    regPasswordContainer.getChildren().clear();
                    regPasswordContainer.getChildren().add(regVisiblePasswordField);
                }
            } else {
                regPasswordField.setText(regVisiblePasswordField.getText());
                regPasswordField.setVisible(true);
                regPasswordField.setManaged(true);
                regVisiblePasswordField.setVisible(false);
                regVisiblePasswordField.setManaged(false);
                
                if (regPasswordContainer != null) {
                    regPasswordContainer.getChildren().clear();
                    regPasswordContainer.getChildren().add(regPasswordField);
                }
            }
        });
    }
    
    private void setupErrorClearing() {
        if (regUsernameField != null && regUsernameError != null) {
            regUsernameField.focusedProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal) regUsernameError.setVisible(false);
            });
        }
        
        if (regEmailField != null && regEmailError != null) {
            regEmailField.focusedProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal) regEmailError.setVisible(false);
            });
        }
        
        if (regPasswordField != null && regPasswordError != null) {
            regPasswordField.focusedProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal) regPasswordError.setVisible(false);
            });
        }
        
        if (regConfirmPasswordField != null && regConfirmPasswordError != null) {
            regConfirmPasswordField.focusedProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal) regConfirmPasswordError.setVisible(false);
            });
        }
        
        if (regTermsCheckbox != null && regTermsError != null) {
            regTermsCheckbox.selectedProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal) regTermsError.setVisible(false);
            });
        }
    }
    
    private void handleLogin() {
        if (usernameField == null || passwordField == null) {
            AlertHelper.showError("Error", "Login form not properly initialized.");
            return;
        }
        
        String username = usernameField.getText().trim();
        String password = getLoginPassword();
        
        if (username.isEmpty() || password.isEmpty()) {
            AlertHelper.showError("Validation Error", "Please enter both username and password.");
            return;
        }
        
        try {
            // Call authService.login and get the User object
            User user = authService.authenticateAndGetUser(username, password);
            
            if (user != null) {
                AlertHelper.showSuccess("Login Successful", "Welcome to Instant Notification Engine!");
                
                // Redirect to main dashboard
                handleSuccessfulLogin(user);
                
                if (rememberMeCheckbox != null && rememberMeCheckbox.isSelected()) {
                    saveCredentials(username, password);
                }
            } else {
                AlertHelper.showError("Login Failed", "Invalid username or password.");
                clearLoginPasswordFields();
                shakeLoginForm();
            }
        } catch (Exception e) {
            AlertHelper.showError("Login Error", "An error occurred during login: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // NEW METHOD: Handle successful login redirect
    private void handleSuccessfulLogin(User user) {
        try {
            Stage stage = (Stage) loginButton.getScene().getWindow();
            Main.loadMainDashboard(stage, user);
        } catch (Exception e) {
            e.printStackTrace();
            AlertHelper.showError("Error", "Failed to load dashboard: " + e.getMessage());
        }
    }
    
    private void handleRegistration() {
        if (regUsernameField == null || regEmailField == null || 
            regPasswordField == null || regConfirmPasswordField == null || 
            regTermsCheckbox == null || regUserRole == null) {
            AlertHelper.showError("Error", "Registration form not properly initialized.");
            return;
        }
        
        String username = regUsernameField.getText().trim();
        String email = regEmailField.getText().trim();
        String password = getRegistrationPassword();
        String confirmPassword = regConfirmPasswordField.getText();
        String role = regUserRole.isSelected() ? "user" : "admin"; // Changed to lowercase for consistency
        boolean agreedToTerms = regTermsCheckbox.isSelected();
        
        clearAllErrors();
        
        boolean isValid = true;
        
        if (username.isEmpty()) {
            if (regUsernameError != null) {
                regUsernameError.setText("Username is required");
                regUsernameError.setVisible(true);
            }
            isValid = false;
        } else if (username.length() < 3) {
            if (regUsernameError != null) {
                regUsernameError.setText("Username must be at least 3 characters");
                regUsernameError.setVisible(true);
            }
            isValid = false;
        } else if (!username.matches("^[a-zA-Z0-9_]+$")) {
            if (regUsernameError != null) {
                regUsernameError.setText("Username can only contain letters, numbers, and underscores");
                regUsernameError.setVisible(true);
            }
            isValid = false;
        }
        
        if (email.isEmpty()) {
            if (regEmailError != null) {
                regEmailError.setText("Email is required");
                regEmailError.setVisible(true);
            }
            isValid = false;
        } else if (!isValidEmail(email)) {
            if (regEmailError != null) {
                regEmailError.setText("Please enter a valid email address");
                regEmailError.setVisible(true);
            }
            isValid = false;
        }
        
        if (password.isEmpty()) {
            if (regPasswordError != null) {
                regPasswordError.setText("Password is required");
                regPasswordError.setVisible(true);
            }
            isValid = false;
        } else if (password.length() < 6) {
            if (regPasswordError != null) {
                regPasswordError.setText("Password must be at least 6 characters");
                regPasswordError.setVisible(true);
            }
            isValid = false;
        }
        
        if (confirmPassword.isEmpty()) {
            if (regConfirmPasswordError != null) {
                regConfirmPasswordError.setText("Please confirm your password");
                regConfirmPasswordError.setVisible(true);
            }
            isValid = false;
        } else if (!password.equals(confirmPassword)) {
            if (regConfirmPasswordError != null) {
                regConfirmPasswordError.setText("Passwords do not match");
                regConfirmPasswordError.setVisible(true);
            }
            isValid = false;
        }
        
        if (!agreedToTerms) {
            if (regTermsError != null) {
                regTermsError.setText("You must agree to the terms and conditions");
                regTermsError.setVisible(true);
            }
            isValid = false;
        }
        
        if (!isValid) {
            shakeRegistrationForm();
            return;
        }
        
        try {
            boolean success = authService.register(username, email, password, role);
            
            if (success) {
                AlertHelper.showSuccess("Registration Successful", 
                    "Your account has been created successfully!\nYou can now login with your credentials.");
                showLoginForm();
                clearRegistrationForm();
                
                if (usernameField != null) {
                    usernameField.setText(username);
                }
                
                if (passwordField != null) {
                    passwordField.requestFocus();
                }
            } else {
                AlertHelper.showError("Registration Failed", 
                    "Username or email already exists. Please try different credentials.");
                shakeRegistrationForm();
            }
        } catch (Exception e) {
            AlertHelper.showError("Registration Error", "An error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void clearAllErrors() {
        if (regUsernameError != null) regUsernameError.setVisible(false);
        if (regEmailError != null) regEmailError.setVisible(false);
        if (regPasswordError != null) regPasswordError.setVisible(false);
        if (regConfirmPasswordError != null) regConfirmPasswordError.setVisible(false);
        if (regTermsError != null) regTermsError.setVisible(false);
    }
    
    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        Pattern pattern = Pattern.compile(emailRegex);
        return pattern.matcher(email).matches();
    }
    
    private void showRegistrationForm() {
        clearRegistrationForm();
        
        if (loginForm != null) {
            loginForm.setVisible(false);
        }
        
        if (registerForm != null) {
            registerForm.setVisible(true);
            
            if (regUsernameField != null) {
                regUsernameField.requestFocus();
            }
        }
    }
    
    private void showLoginForm() {
        if (registerForm != null) {
            registerForm.setVisible(false);
        }
        
        if (loginForm != null) {
            loginForm.setVisible(true);
            
            if (usernameField != null) {
                usernameField.requestFocus();
            }
        }
    }
    
    private void showTermsAndConditions() {
        String terms = 
            "TERMS AND CONDITIONS FOR INSTANT NOTIFICATION ENGINE\n\n" +
            "1. Acceptance of Terms\n" +
            "   By creating an account, you agree to be bound by these Terms and Conditions.\n\n" +
            "2. User Responsibilities\n" +
            "   - You are responsible for maintaining the confidentiality of your account\n" +
            "   - You agree to use the service only for lawful purposes\n" +
            "   - You will not send spam or malicious content\n\n" +
            "3. Service Usage\n" +
            "   - The service is provided \"as is\" without warranties\n" +
            "   - We reserve the right to suspend accounts violating these terms\n" +
            "   - You agree to receive service-related notifications\n\n" +
            "4. Privacy\n" +
            "   - Your data will be handled according to our Privacy Policy\n" +
            "   - We do not share your personal information with third parties\n\n" +
            "5. Modifications\n" +
            "   - We may update these terms from time to time\n" +
            "   - Continued use after changes constitutes acceptance\n\n" +
            "By checking the agreement box, you acknowledge that you have read, " +
            "understood, and agree to be bound by these Terms and Conditions.";

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Terms and Conditions");
        alert.setHeaderText("Instant Notification Engine - Terms of Service");
        alert.setContentText(terms);
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        alert.showAndWait();
    }
    
    private void handleLoginEnterKey(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            handleLogin();
        }
    }
    
    private void handleRegisterEnterKey(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            handleRegistration();
        }
    }
    
    private void handleForgotPassword() {
        AlertHelper.showInfo("Forgot Password", 
            "Please contact your administrator to reset your password.\n" +
            "Email: admin@notification.com\n" +
            "Phone: +1 (555) 123-4567");
    }
    
    private String getLoginPassword() {
        if (showPasswordCheckbox != null && showPasswordCheckbox.isSelected()) {
            return visiblePasswordField != null ? visiblePasswordField.getText() : "";
        } else {
            return passwordField != null ? passwordField.getText() : "";
        }
    }
    
    private String getRegistrationPassword() {
        if (regShowPasswordCheckbox != null && regShowPasswordCheckbox.isSelected()) {
            return regVisiblePasswordField != null ? regVisiblePasswordField.getText() : "";
        } else {
            return regPasswordField != null ? regPasswordField.getText() : "";
        }
    }
    
    private void clearLoginPasswordFields() {
        if (passwordField != null) passwordField.clear();
        if (visiblePasswordField != null) visiblePasswordField.clear();
        if (passwordField != null) passwordField.requestFocus();
    }
    
    private void clearRegistrationForm() {
        if (regUsernameField != null) regUsernameField.clear();
        if (regEmailField != null) regEmailField.clear();
        if (regPasswordField != null) regPasswordField.clear();
        if (regVisiblePasswordField != null) regVisiblePasswordField.clear();
        if (regConfirmPasswordField != null) regConfirmPasswordField.clear();
        if (regTermsCheckbox != null) regTermsCheckbox.setSelected(false);
        if (regUserRole != null && roleGroup != null) regUserRole.setSelected(true);
        clearAllErrors();
    }
    
    private void saveCredentials(String username, String password) {
        System.out.println("Remember me selected for user: " + username);
    }
    
    private void shakeLoginForm() {
        if (rightPanel != null) {
            shakeNode(rightPanel);
        }
    }
    
    private void shakeRegistrationForm() {
        if (registerForm != null) {
            shakeNode(registerForm);
        }
    }
    
    private void shakeNode(javafx.scene.Node node) {
        TranslateTransition shake = new TranslateTransition(Duration.millis(50), node);
        shake.setFromX(0);
        shake.setToX(10);
        shake.setCycleCount(6);
        shake.setAutoReverse(true);
        shake.play();
    }
}