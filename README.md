# OHDM Traveler 

OHDM Traveler allows to create a route between a start and an end point under consideration of a given date in a [OHDM DB](https://github.com/OpenHistoricalDataMap).
This project is based on the [bachelor thesis](https://www.sharksystem.net/htw/FP_ICW_BA_MA/2020_Mishin_Bachelorarbeit.pdf) written by Kirill Mishin.

The idea is that in the future the route can be calculated under consideration of historical events.
At the moment two different types of persons (Noble and Farmer) are implemented to show an example of a route calculation under consideration of historical events. Restricted areas can be set to represent Pest zones, the Nobel will be informed of the Pest zone and therefore avoid them. On the other hand the Farmer is not informed and will traverse them. 
## Prerequisites

### You have access to:  
- **OHDM DB**, more info [here](https://github.com/OpenHistoricalDataMap/OSMImportUpdate/wiki). 
- **Rendering DB**, more info [here](https://github.com/OpenHistoricalDataMap/OSMImportUpdate/wiki/RenderingDB).

### DB extensions:
- **[hstore](https://www.postgresql.org/docs/9.1/hstore.html)**
- **[pgrouting](https://pgrouting.org/)** (at least v.3.0.0 is recommended) 
- **[postgis](https://postgis.net/)**  

## Prerequisites setup guide:

1. Download any osm file, you can find a small sample on our repository (sample.osm).
2. Download the [JDBC driver](https://jdbc.postgresql.org/download.html).
3. Create these files: "intermediate.txt", "ohdm.txt", "rendering.txt".
   1. The format these files have to follow is described [here](https://github.com/OpenHistoricalDataMap/OSMImportUpdate/wiki).
4. Create a Postgres DB with these [extensions](#db-extensions):

5. Create these schemas: ohdm, intermediate, rendering.
6. Create the [intermediate DB](https://github.com/OpenHistoricalDataMap/OSMImportUpdate/wiki/O2I).
7. Create the [OHDM DB](https://github.com/OpenHistoricalDataMap/OSMImportUpdate/wiki/I2D).
8. Create the [Rendering DB](https://github.com/OpenHistoricalDataMap/OSMImportUpdate/wiki/D2R).


## Deployment #1 (No REST Server, will only create results in the DB)

1. Clone this repository
2. Create a schema in your Postgres DB where the routing results will be stored (routing for example).
3. Under *src/main/config* you will find two example csv files, **odhm_parameter_example.csv** and **search_parameter_example.csv**.
Insert in the second row of both files the required information, you can find more info about this under [odhm_parameter.csv](#odhm_parametercsv).
4. Run jar **or**
5. Set in build.gradle
```java
mainClassName="traveler/TravelerMain.java"
```
6. Run with gradlew
```java
gradlew run --args="-r [path to ohdm_parameter.csv] -s [path to search_parameter.csv]"
```
7. Optionally debug mode can be run with:  
``` java
-d [true/false]
```
8. The results will be stored in the routing schema.


## Deployment #2 (With REST Server)

1. Clone this repository
2. Create a schema in your Postgres DB where the routing results will be stored.
3. Under *src/main/java/config* you will find two example csv files, **odhm_parameter_example.csv** and **search_parameter_example.csv**.
Insert in the second row of both files the required information, you can find more info about this under [odhm_parameter.csv](#odhm_parametercsv).
4. Run jar **or**
5. Set in build.gradle
```java
mainClassName="traveler/RestTravelerMain"
```
6. Run with gradlew
```java
gradlew run --args="-r [path to ohdm_parameter.csv]"
```
7. Optionally debug mode can be run with:  
``` java
-d [true/false]
```
8. REST Server waits for POST request With JSON Body
9. REST Server replies with JSON 

## JSON Request format example
```json
{"classofperson": "farmer", "transporttype": "bicycle", "waterwayincl": "true", "startpoint": {"latitude": "52.457907", "longitude": "13.527333"}, "endpoint": {"latitude": "52.444784", "longitude": "13.507886"}, "day": "2019-12-1", "restricted_area": {}}
```
## JSON Reply format example
```json
{"travel_time":"00:16:52.46232","request_id":"f84ff5dcc3dc4ecb85129c9fba05891e"}
```
## odhm_parameter.csv

"odhm_parameter.csv" has to follow this format:

host | port | username | password | dbname | schema
-----|------|----------|----------|--------|--------
host IP | port number | username | password if required | name of the DB | name of scheme where results will be saved ([Deployment 2.](#deployment))

### Example:


host | port | username | password | dbname | schema
-----|------|----------|----------|--------|--------
localhost | 5432 | admin | superPassword | OHDM | routing

## search_parameter.csv

"search_parameter.csv" has to follow this format:
classofperson | transporttype | waterwayincl | startpoint_latitude | startpoint_longitude | endpoint_latitude | endpoint_longitude | day |
-------------|-------------|-------------|-------------|-------------|-------------|-------------|-------------|

**This is only for illustration, should follow csv format!**

classofperson: \[1, 2\] (1 will traverse restricted areas, 2 will avoid them) 

transporttype: \[Walking, Horse, Carriage, Car, Boat, Bicycle\] (Case insensitive)

waterwayincl: \[true,flase\]

startpoint_latitude: \[startpoint latitude\] 

startpoint_longitude: \[startpoint longitude\]

endpoint_latitude: \[endpoint latitude\]

endpoint_longitude: \[endpoint longitude\]

day: \[YYYY-MM-DD\]


### Example:
classofperson | transporttype | waterwayincl | startpoint_latitude | startpoint_longitude | endpoint_latitude | endpoint_longitude | day |
-------------|-------------|-------------|-------------|-------------|-------------|-------------|-------------|
2 | walking | false | 52.457907 | 13.527333 | 52.461204 | 13.513603 | 2019-12-1 |
