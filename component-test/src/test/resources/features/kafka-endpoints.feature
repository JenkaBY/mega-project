Feature: Describe endpoints related to KafkaMessageController

  Scenario Outline: String message sent via GET endpoint should be delivered onto a topic
    Given application is started
    When a GET request has been made to '/api/kafka/send' endpoint with query parameters
      | key      | payload   |
      | <msgKey> | <msgData> |
    Then the response status is 200
    And 1 message was received on the 'notification.fct.message.v0' topic
    And the received notification message is
      | messageKey | payload   |
      | <msgKey>   | <msgData> |
    Examples:
      | msgKey  | msgData              |
      | msgKey1 | message payload data |

  Scenario Outline: DLT incoming message when message key contains 'error'
    Given application is started
    When a GET request has been made to '/api/kafka/send' endpoint with query parameters
      | key      | payload   |
      | <msgKey> | <msgData> |
    Then the response status is 200
    And 1 message was received on the 'notification.fct.message.v0.dlt' topic
    And the received notification message is
      | messageKey | payload   |
      | <msgKey>   | <msgData> |
    Examples:
      | msgKey   | msgData |
      | error    | ANY     |
      | error123 | ANY     |
      | 123error | ANY     |
      | ErROR    | ANY     |

  Scenario Outline: JSON message sent via POST endpoint should be delivered onto the topic
    Given application is started
    And the following TransactionEvent payload is
      | status   | amount | txnId   |
      | <status> | 10.1   | <txnId> |
    When a POST request has been made to '/api/kafka/<topic>' endpoint with query parameters
      | key      |
      | <msgKey> |
    Then the response status is 201
    And 1 message was received on the '<topic>' topic
    And the db contains the following MessageLog records
      | encoding | status   | topic   | modifiedBy              | txnId   |
      | JSON     | <status> | <topic> | TransactionJsonListener | <txnId> |
    Examples:
      | topic                            | msgKey  | status | txnId |
      | business.fct.transaction-json.v0 | msgKey1 | NEW    | TXN1  |