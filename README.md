# Backend Server Setup Steps

## 1. Navigate to the Project Directory

Open a terminal and navigate to the project folder where the Node.js dependencies should be installed.

`cd app/src/main/java/com/example/smishingdetectionapp/DataBase`

## 2. Install Required Node Packages

To install the dependencies, use the following command:

`npm install`

This will create a `node_modules` folder, if it does not exist, and update the `package.json` file.

## 3. Run the Server

Once dependencies are installed, execute the server, which points to the `DB_Connection.js` file:

`node .`

Ensure you do not miss the `.` at the end.
If the script runs successfully, it should establish a connection to the database.
