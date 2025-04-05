Feature: Describe endpoints related to Delay controller

  @run
  Scenario Outline: The '<url>' should respond OK
    Given application is started
    When a GET request has been made 3 times to '<url>' endpoint with query parameters
      | delay   |
      | <delay> |
    Then the response status is 200
    And measured average '<latencyMetric>' metric has been recorded at ~<delay> ms with <tags> tags
    Examples:
      | url                         | delay | latencyMetric         | tags                      |
      | /api/delay/aop-annotation   | 5     | delay.service.latency | {"type":"aop-annotation"} |
      | /api/delay/aop-execution    | 5     | delay.service.latency | {"type":"aop-execution"}  |
      | /api/delay/bpp-dynamic      | 5     | delay.service.latency | {"type":"bpp-dynamic"}    |
      | /api/delay/bpp-cglib        | 5     | delay.service.latency | {"type":"bpp-cglib"}      |
      | /api/delay/timed-micrometer | 5     | delay.service.latency | {"type":"timed"}          |
      | /api/delay/native           | 5     |                       |                           |

  @run
  Scenario Outline: The '<url>' should respond OK
    Given application is started
    Then the '<beanName>' bean of the '<class>' class in the application context is <proxyType> proxy type
    Examples:
      | beanName                          | class                                               | proxyType   |
      | bppCGLibProxyClientDelayService   | com.github.jenkaby.service.delay.ClientDelayService | CGLIB       |
      | bppDynamicProxyClientDelayService | com.github.jenkaby.service.delay.ClientDelayService | JDK_DYNAMIC |
