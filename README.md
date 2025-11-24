# ProjectBanksystemSimulatorSpringBootMonolithMongoDB

A monolithic banking backend built using Spring Boot 3 and MongoDB.  
Includes full REST API, documentation, setup instructions, and test cases.

---

##  Features

- Account creation, update, search, delete
- Deposit, withdrawal, and transfer transactions
- All data is stored in MongoDB
- Validation and global exception handling
- API documentation with Swagger/OpenAPI
- Service tests with JUnit 5 & Mockito

---

##  Installation & Setup

**Prerequisites:**  
- Java 17+  
- Maven 3.6+  
- MongoDB (local/remote)  

**Get started:**

git clone https://github.com/rahul4793/JavaFSD_Projects.git
cd JavaFSD_Projects/MiniProject_Banksystemsimulator_SpringBoot_Monolith_MongoDB


Make sure MongoDB is running locally, or edit your `src/main/resources/application.properties`:

spring.data.mongodb.uri=mongodb://localhost:27017/bankingsystem
server.port=8080


**Build & run the app:**

mvn clean install
mvn spring-boot:run

Or
java -jar target/*.jar

Server will listen at `http://localhost:8080`

---

##  API Usage

**Swagger Docs:**  
Open [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html) in your browser for all endpoints and live testing.

**Key Endpoints:**

| Method | Endpoint | Purpose |
| ------ | -------- | ------- |
| POST   | /api/accounts | Create new account |
| GET    | /api/accounts/{accountNumber} | Get account info |
| PUT    | /api/accounts/{accountNumber} | Update account |
| DELETE | /api/accounts/{accountNumber} | Delete account |
| PUT    | /api/accounts/{accountNumber}/deposit?amount={amt} | Deposit money |
| PUT    | /api/accounts/{accountNumber}/withdraw?amount={amt} | Withdraw money |
| POST   | /api/accounts/transfer | Transfer money |
| GET    | /api/accounts/{accountNumber}/transactions | Get transactions |

---

## Test APIs via cURL

Create an Account:
curl -X POST "http://localhost:8080/api/accounts"
-H "Content-Type: application/json"
-d '{"holderName":"Rahul Kumar"}'

Deposit Money:
curl -X PUT "http://localhost:8080/api/accounts/RAH1234/deposit?amount=500"

Withdraw Money:
curl -X PUT "http://localhost:8080/api/accounts/RAH1234/withdraw?amount=200"

Transfer:
curl -X POST "http://localhost:8080/api/accounts/transfer"
-d "sourceAccount=RAH1234&destinationAccount=XYZ4567&amount=100"

Get Account Information:
curl -X GET "http://localhost:8080/api/accounts/RAH1234"

Get Transaction History:
curl -X GET "http://localhost:8080/api/accounts/RAH1234/transactions"

---

##  How to Run Unit Tests

Service logic is tested with [JUnit5](https://junit.org/junit5/) and [Mockito](https://site.mockito.org/):

mvn test

All service layer logic is mock-tested and also covers error and edge cases.

---

##  Project Structure

src/
main/
java/com/example/demo/
controller/
service/
repository/
model/
exception/
config/
resources/
application.properties
test/
java/com/example/demo/service/AccountServiceTest.java
