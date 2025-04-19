## Project description

This project is intended for performance testing. It's intentionally not included in the parent module because of
issues with running 2 gradle tasks simultaneously. Technically this project is managed by the parent project but to run
independently performance tasks, the [load-test/setting.gradle](settings.gradle) file was added.

## How to use

In order to run the test, it needs to execute the command from the **ROOT** project:

```shell
 ./gradlew -b ./load-test/build.gradle gatlingRun --simulation com.github.jenkaby.loadtest.DDOSSimulation
```

OR from here(IntelijiIdea)

```shell
cd .. && ./gradlew -b ./load-test/build.gradle gatlingRun --simulation com.github.jenkaby.loadtest.DDOSSimulation
```

where

- **-b** is a key to specify gradle build file
- **gatlingRun** is a task to run test
- **--simulation** is a key to specify Simulation to be run