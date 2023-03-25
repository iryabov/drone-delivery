## Drones

---

### Overview

This is a REST service that allows you to manage a fleet of drones for medication delivery.


### Prerequisites

To build and run the application, the following software must be installed:
- Java 17
- Maven 3

Port 8080 must also not be occupied by another application.

### Build

To build the application, run the Maven command at the root of the project:
````shell
mvn clean package
````
The application will be built under the path `/target/drone-delivery.jar`.

### Run

To run the application, perform the Java command:
````shell
java -jar target/drone-delivery.jar
````
After starting the application, the program console will display the line:
````
Tomcat started on port(s): 8080 (http) with context path ''
````
You can see REST API documentation at the link:
http://localhost:8080/swagger-ui/index.html

:::note
You can set up the location where drones base with following properties:
````shell
java -jar target/drone-delivery.jar --drone.base.latitude=0.0 --drone.base.longitude=0.0
````
:::

### Test

After launching the application, you will be able to manage a fleet of 10 drones.

1. Run the following REST request to see your fleet of drones:
```shell
curl -X 'GET' \
  'http://localhost:8080/api/drones' \
  -H 'accept: */*'
```

You will see a list of your drones:
```json
[
  {
    "id": 1,
    "name": "LIGHTWEIGHT-01",
    "state": "IDLE",
    "batteryLevel": 100
  },
  {
    "id": 2,
    "name": "MIDDLEWEIGHT-02",
    "state": "IDLE",
    "batteryLevel": 100
  },
  ...
]
```

2. To view detailed information about a specific drone, run the command with drone `id`:
```shell
curl -X 'GET' \
  'http://localhost:8080/api/drones/1' \
  -H 'accept: */*'
```

You will see the following response:
````json
{
  "id": 1,
  "name": "LIGHTWEIGHT-01",
  "state": "IDLE",
  "batteryLevel": 100,
  "serial": "01",
  "droneModel": "LIGHTWEIGHT",
  "weightLimit": 0.5,
  "currentLocation": {
    "latitude": 0,
    "longitude": 0
  }
}
````
Where `currentLocation` is the current coordinates of the drone's location. 
Now it is in the warehouse.

3. To send a drone on a mission, you need to load the package with goods.
````shell
curl -X 'POST' \
  'http://localhost:8080/api/drones/1/load' \
  -H 'accept: */*' \
  -H 'Content-Type: application/json' \
  -d '{
  "items": [
    {
      "goodsId": 1,
      "quantity": 5
    }
  ]
}'
````
Where `goodsId` is the medication identifier and `quantity` is the required quantity in the order.

:::note
You can find all medications by following request:
````shell
curl -X 'GET' \
  'http://localhost:8080/api/references/medications' \
  -H 'accept: */*'
````
:::

After perform you will be returned the identifier of shipment:
````json5
{
  "id": 1 //shipment identifier
}
````


4. With it, you can track the delivery status:
````shell
curl -X 'GET' \
  'http://localhost:8080/api/drones/1/shipping/1' \
  -H 'accept: */*'
````


The request will return the detailed information about the shipping:
```json
{
  "id": 1,
  "deliveryStatus": "PENDING",
  "packageInfo": {
    "items": [
      {
        "goodsName": "Penicillin",
        "quantity": 5
      }
    ],
    "totalWeight": 0.25
  }
}
```
The `"deliveryStatus": "PENDING"` means that the package is ready to ship, but has not yet been sent.

5. Next you should send the drone to the delivery address:
````shell
curl -X 'POST' \
  'http://localhost:8080/api/drones/1/send' \
  -H 'accept: */*' \
  -H 'Content-Type: application/json' \
  -d '{
  "address": "бул. Драган Цанков 36, София, Болгария",
  "latitude": 42.67034,
  "longitude": 23.35111
}'
````

The service will respond with status 200. 
This means that the drone is flying along the coordinates.

:::warning 
Don't send the drone too far, otherwise it will waste the battery and won't be able to return!
:::

6. You can track drone's current state by following request:

````shell
curl -X 'GET' \
  'http://localhost:8080/api/drones/1/logs?event=STATE_CHANGE' \
  -H 'accept: */*'
````

In the response, 10 last changes in the states of the drone for the last hour:
````json
[
  {
    "time": "2023-03-23T09:52:42.417255",
    "event": "STATE_CHANGE",
    "oldValue": null,
    "newValue": "LOADING"
  },
  {
    "time": "2023-03-23T09:52:46.480172",
    "event": "STATE_CHANGE",
    "oldValue": "LOADING",
    "newValue": "LOADED"
  },
  {
    "time": "2023-03-23T09:53:03.192042",
    "event": "STATE_CHANGE",
    "oldValue": "LOADED",
    "newValue": "DELIVERING"
  }
]
````

:::tip 
To track the current position of the drone or the current battery charge, change the `event` parameter to `LOCATION_CHANGE` or `BATTERY_CHANGE`.
:::

7. When the drone reaches its destination, its status will change to `ARRIVED`, you need to unload it.
Use the following request:
````shell
curl -X 'POST' \
  'http://localhost:8080/api/drones/1/unload' \
  -H 'accept: */*' \
  -d ''
````
After that, the package will be considered delivered.

8. Then send the drone back to the warehouse:
````shell
curl -X 'POST' \
  'http://localhost:8080/api/drones/1/return' \
  -H 'accept: */*' \
  -d ''
````

After a while, the drone will return to the warehouse and start charging in order to go on a mission again.

### Implementation

The drone delivery service is a Spring Boot Application with an embedded database H2.

#### Java packages

- `/client` Drone fleet emulation 
- `/config` Spring configurations
- `/controller` REST controllers and handlers
- `/entity` JPA Entities
- `/enums` Reference values
- `/exception` Exception classes
- `/job` Background tasks
- `/mapper` Classes for mapping entities and DTOs
- `/model` DTO classes
- `/repository` JPA repositories
- `/service` API and implementation
- `/validation` Classes for validation

#### Database schema

![database](/images/database.png)

#### Drone states

![drone states](/images/dronestates.png)

#### Delivery statuses

![delivery statuses](/images/deliverystatuses.png)