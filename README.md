# Weather Service

Code Challenge

## Design and Implementation Document

[Design and Implementation Document](./Design_and_Implementation_Doc-Prashant_Sihag-Tech_Assessment.pdf)

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

## Verifying H2 database content:

- Visit <a href="http://localhost:8080/h2-console">http://localhost:8080/h2-console</a> and use username `sa` and password `password`
- Once logged in, select "WEATHER_MODEL" from left hand panel.
- Run following query:

```
SELECT * FROM WEATHER_MODEL;
```

You should see a table with the query parameter that you passed to the api request above as well as the weather description stored in a table.

## Rate Limiting

The application limits each api key's usage using following configurations:

- `timeWindowInMinutes`: Determines a time window in which only certain amount of requests per api key would be allowed.
- `allowedTotalCallsInWindow`: Determines how many requests per api key woud be allowed in a provided time window.

By default they are set to 60 minutes time window for a maximum of 5 requests per api key.
Feel free to update these values in `application.properties`
