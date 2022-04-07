# Position Calculator

<br/>

#### TL;DR: The application generated [this](https://docs.google.com/document/d/1-_rGSQW2lj9l4En2u0qTbFypkjeWP_DW4mqbFM6ips8/edit?usp=sharing) position aggregation result with the trade dataset share.

<br/>

### Design Assumptions
- Only trades with **action=NEW** are considered in the profit & loss (P&L) result computation. It is assumed (based on observation from the dataset) that trades with **action=AMEND** have corresponding trade entry whose **action=NEW** with same amount, tradeId, etc. Also, trades with **action=CANCEL** are assumed to be inconsequential as the transaction value (money) would have been rolled back;
- The trade price is the amount paid for the total trade volume;
- The price of trade entries are assumed to be in the same currency as the dataset does not communicate otherwise;
- By observation, the dataset is for Security. It is assumed that securities belong to companies/organizations. For a user to acquire it, the security has to be purchased first; i.e. **open position=BUY** and **close position=SELL**. This assumption is necessary to determine whether a typical user accrued a profit or loss for a typical BBG Code (security type). Hence, it is assumed that, consider **"Price Difference = Buy Price - Sell Price"**: if difference is negative, the user accrued a loss, else a user accrued a profit within the security holding period. 

<br/>

### Things to know before running the project
- The application is a Spring Boot application. Hence, Java 11 JDK is a requirement;
- Interaction with the application is via API. Postman tool can be used to access the application. Assume the application runs on port 8080, the API URL is as follows:
  - **Add new trade**: POST localhost:8080/trade/add
  - **Compute position aggregation result**: GET localhost:8080/trade/position/aggregate
- Due to limited memory resource allocation to a Spring Test Container, a different trade dataset is used for automated test purposes. The difference in the shared dataset (used while running the application to generate position aggregation result) and the test dataset is the number of trade entries. The CSV file structure remains the same. To toggle between what dataset to use, set the **"IS_TEST"** value in the Constants.java file (file path: src/main/java/com/ghco/positioncalculator/util/Constants.java).

<br/>

### What could be better
- If the assumption that the trade prices are in the same currency is wrong, I could have converted the trade prices to the same currency using FX rate in my computation;
- As opposed to loading the dataset data everytime the application is restarted, I could have persisted the trade data in a database (once and for all time) and leverage SQL queries for SELECT and GROUP queries used. This will make the application restart and load data faster.
