Feature: encode message into picture
  Scenario: encoding without logging in
    Given user opens home page
    When user uploads picture with message "Hello"
    Then user sees encoded picture
