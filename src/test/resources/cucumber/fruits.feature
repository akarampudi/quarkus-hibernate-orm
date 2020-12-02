# new feature
# Tags: optional

Feature: A Fruit shop

  Scenario: find all available fruits in the shop
    Given I want to stub the mock server
    When I want to add following fruit to the shop
      | Pear |
    And I want to add another following fruit to the shop
      | Watermelon |
    Then I found following fruits in the store
      | Apple  |
      | Banana |
      | Cherry |
      | Pear   |
