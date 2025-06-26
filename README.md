# LiveBetterApp
The live better app is an app that has been designed to help users to manage their money without any stress as than can connect their bank accounts with the app and set their budget. 
This app helps them manage the budget they have set and helps them track their goal and show them in a form of graphical chart.
This app has made budgeting very easy with its way of using which even new users can adapt quickly.
features on the app
-Add Expense
-View Expense
-Set Budget
-View spendings graph
-category summary
-view rewards and badges
-track goal progress
the custom features on the app
- we have the reminder notification feature this feature helps users by bringing them notifications that have words like log more this will help them to save
- we have the category summary this helpsd users to view their expenses in a form of a pie chart with all the expenses
- 
To ensure the reliability and build integrity of the app, GitHub Actions was used for Continuous Integration (CI). An automated workflow was configured to:

Build the Android app automatically whenever code is pushed to the repository.

Run basic unit tests (if applicable) to verify that main functionalities do not break.

Check for build errors across environments (not just the local machine).

This process helps catch bugs early and ensures the app remains stable and functional throughout development.

The GitHub Actions workflow file is located at:
.github/workflows/build.yml

name: Android CI

on:
  push:
    branches:
      - main
  pull_request:
    branches:
      - main

jobs:
  build:
    name: Build Android Project
    runs-on: ubuntu-latest

    steps:
      - uses: actions/checkout@v2
      - name: Set up JDK 11
        uses: actions/setup-java@v2
        with:
          distribution: 'temurin'
          java-version: 11

      - name: Build with Gradle
        run: ./gradlew build

https://github.com/ST10388547/LiveBetterApp/

LINK TO THE VIDEO
https://youtube.com/shorts/_f3aYT_htd0?si=3A5ppHd4SmrxBJaj
