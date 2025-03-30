Feature: Describe endpoints related to Delay controller

  @run
  Scenario Outline: The '<url>' should respond OK
    Given application is started
    When a GET request has been made to '<url>' endpoint with query parameters
      | delay   |
      | <delay> |
    Then the response status is 200
    And measured '<latencyMetric>' metric has been recorded at ~<delay> ms with <tags> tags
    Examples:
      | url                       | delay | latencyMetric         | tags                      |
      | /api/delay/aop-annotation | 5     | delay.service.latency | {"type":"aop-annotation"} |
      | /api/delay/aop-execution  | 5     | delay.service.latency | {"type":"aop-execution"}  |
      | /api/delay/bpp            | 5     | delay.service.latency | {"type":"bpp"}            |
      | /api/delay/native         | 5     |                       |                           |