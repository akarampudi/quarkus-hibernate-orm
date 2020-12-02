# new feature
# Tags: optional

Feature: A Fruit shop

  Scenario: find all available fruits in the shop
    Given I want to stub the mock server
    When I want to add a new fruit to the shop
    Then I found following fruits in the store
      | Apple  |
      | Banana |
      | Cherry |
      | Pear   |
