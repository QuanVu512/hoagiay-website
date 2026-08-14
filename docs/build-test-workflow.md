# Build And Test Workflow

Use this workflow to keep local verification fast and predictable.

## Fast Compile Check

```powershell
.\gradlew.bat --no-daemon --offline quickCheck
```

This compiles main and test code without starting Spring and without connecting to Neon.

## Unit Tests

```powershell
.\gradlew.bat --no-daemon --offline test
```

The test task has a 2 minute timeout and should stay fast. Keep database and Spring context tests out of this task.

## Integration Tests

```powershell
.\gradlew.bat --no-daemon integrationTest
```

Use this task for slower tests that start Spring or connect to a database. It has a 5 minute timeout.

## Local Profile

Local development should use:

```properties
SPRING_PROFILES_ACTIVE=dev
```

Production deployment should set:

```properties
SPRING_PROFILES_ACTIVE=prod
```
