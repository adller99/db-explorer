# DB-EXPLORER
This app allows you to browse and explore databases (schemas, tables, table details, preview table data)

## Getting started

### 1. Run the app
1. Build the app by executing `./gradlew build` in the root directory
2. Cd into the docker folder by executing `cd docker`   
3. Start the app by executing `docker-compose up -d`.

### 2. Start the playground DB
In order to test the app against a playground database with some prepared tables and data
1. Cd into the docker folder by executing `cd docker`
2. Start the database by executing `docker-compose -f docker-compose-playground-db.yml up -d`

## How to use the app
1. check the swagger at `http://localhost:8080/swagger-ui.html#`
2. Explore the postman collection located in the `/postman` folder

### 1. How to connect to the playground DB
1. Send a post request to `/database/` with the following payload:
<code>   {
   "name": "playground1",
   "hostname": "<YOUR_IP_ADDRESS>",
   "port": 5433,
   "databaseName": "postgres",
   "username": "postgres",
   "password": "docker"
   }
</code>
