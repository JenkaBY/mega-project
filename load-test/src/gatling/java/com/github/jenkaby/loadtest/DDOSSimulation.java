package com.github.jenkaby.loadtest;

import com.github.jenkaby.loadtest.endpoint.RestApiEndpoint;
import io.gatling.javaapi.core.PopulationBuilder;
import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.http;

public class DDOSSimulation extends Simulation {

    static final HttpProtocolBuilder httpProtocol =
            http.baseUrl("http://localhost:8080")
                    .acceptHeader("application/json")
                    .userAgentHeader("Gatling test UA");


    static final ScenarioBuilder scn1 = scenario("Send request to the '%s' endpoint".formatted("native and others"))
//            .pause(10000)
            .roundRobinSwitch()
            .on(
                    RestApiEndpoint.delayEndpoint("native"),
                    RestApiEndpoint.delayEndpoint("timed-micrometer"),
                    RestApiEndpoint.delayEndpoint("without-measurement"),
                    RestApiEndpoint.delayEndpoint("bpp-dynamic"),
                    RestApiEndpoint.delayEndpoint("bpp-cglib"),
                    RestApiEndpoint.delayEndpoint("aop-annotation"),
                    RestApiEndpoint.delayEndpoint("aop-execution")
            )
            .exitHereIfFailed();

    static PopulationBuilder injectionProfile(ScenarioBuilder scn) {
        return scn.injectOpen(
                rampUsersPerSec(0).to(7).during(1),
                constantUsersPerSec(7).during(1000));
    }

    // Set up the simulation with scenarios, load profiles, and assertions
    {
        setUp(injectionProfile(scn1))
                .protocols(httpProtocol);
    }
}




