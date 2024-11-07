# Weather Service

Code Challenge

## Requirements

- Java 17
- Open Weather Map API key

## Environment Variable

On Windows:

```
set WEATHER_API_KEY=your_api_key_here
```

On Macos:

```
export WEATHER_API_KEY=your_api_key_here
```

## Starting the app

From the root of the directory, execute following commands:

Compile:

```
./mvnw clean compile;
```

Run:

```
./mvnw spring-boot:run
```

## Executing tests

From the root of the directory, execute following command:

```
./mvnw test
```

## Verifying H2 database content:

Visit <a href="http://localhost:8080/h2-console">http://localhost:8080/h2-console</a> and use username `sa` and password `password`

## Application API Key:

Application out of the box supports 5 api keys: "API_KEY_1", "API_KEY_2", "API_KEY_3", "API_KEY_4", "API_KEY_5"

## Fetching Weather data:

From Postman or alternate tool, craft following request:

- URL: `https://localhost:8080/weather?q=London,UK`
- HEADERS:
  - `Authorization`: `Bearer <any_api_key>`

and hit send, you should see a response like following:

```
{
    "weather": "<a_description_of_current_weather>"
}
```
