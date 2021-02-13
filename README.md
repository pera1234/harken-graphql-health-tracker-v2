# harken-graphql-health-tracker-v2

### Introduction

As with v1, this application provides a regional diabetic heatmap, allowing users to share their health measures such as diabetes indicators anonymously.
The application allows reports and measures to be saved. This data can be viewed on a per city basis providing health summaries
to be displayed in barchart format, by date/health level indicator (cholesterol, blood sugar)   

### Details

This graphql server implementation is based on the com.graphql-java api and developed using a programmatic approach.
See v1 for a schema based with auto-configuration approach, with less upfront schema coding.

This project will use the data-loader library to resolve the graphql N + 1 issue, where techniques such as caching and batching queries 
are used to make the server more performant. 

In harken-graphql-health-tracker-v3, we have added Spring security for a token based authentication and authorisation solution for sensitive queries.

### Example queries

```
 query {
   users {
     id
     firstname
   }
 }

 query {
   userByEmail(email: "abc@abc.com") {
     id
     firstname
   }
 }

 userById(id: "600c75177badc40da7426a43") {
     id
     reports {
       id
       measure {
         measureDate
       }
     }
   }
 }

 query {
 	cities {
     id
     city
   }
 }

 query {
 	cityById(id: "600ea0788030fcb72e477e72") {
     id
     city
   }
 }

 query {
 	city(city: "Leeds") {
     id
     city
     lat
     lng
   }
 }

 query {
   reports(
     reportPageInput: {
       page: 1
       size: 10
       filter: {
         columnField: "city"
     		operatorValue: "contains"
     		value: "lon"
       }
       sort: {
         sort: "asc"
         field: "measureDate"
       }
     }
   ) {
     totalElements
     totalPages
     page
     content {
       id
       measure {
         measureDate
         level
         device
     	}
     }
   }
 }


 query {
   userById(id: "600c8273f0ee39239d26984a") {
     id
     firstname
     lastname
     email
     address {
       street
       postcode
     }
     reports {
       id
       city {
         id
         city
       }
       measure {
         measureDate
         level
         device
         diabetesIndicator
         pinLocation {
           latitude
           longitude
         }
       }
     }

   }
 }

 query {
   reportById(id: "6011f6d46a9c46585a7d5d88") {
     id
     city {
       city
     }
     measure {
       measureDate
       device
       level
       diabetesIndicator
       pinLocation {
         latitude
         longitude
       }
     }
     user {
       email
     }
   }
 }

 query {
   reportsIn(
     reportIds: ["600f02ec4afc2c501ead6653", "6011f6d46a9c46585a7d5d88"]
   ) {
     id
     user {
       firstname
       email
       password
     }
     measure {
       measureDate
     }
   }
 }

 query {
 reportsByUser(userId: "600c8273f0ee39239d26984a") {
   id
   measure {
     measureDate
   }
 }
 }

 query {
 	cityById(id: "600ea0788030fcb72e477e72") {
     id
     city
   }
 }

 query {
   healthQualities {
     id
     measureDate
     city
     cholesterolLevel {
       totalLevel
       count
       reports {
         id
         city {
           city
         }
         measure {
           measureDate
           pinLocation {
             latitude
           }
         }
       }
     }
     bloodsugarLevel {
       totalLevel
       count
     }
   }
 }

 query {
   healthQualitySummary(
     city: "600ea0788030fcb72e477e72"
     measureDate: "2021-01-26T00:00:00Z"
   ) {
     id
     city
     cholesterolLevel {
       totalLevel
       count
       reports {
         id
         measure {
           measureDate
           pinLocation {
             latitude
           }
         }
       }
     }
     bloodsugarLevel {
       totalLevel
       count
       reports {
         id
       }
     }
   }
 }

 query {
 healthQualityById(id: "6011f6d4b99984c2cbf0621f") {
     id
   	city
     measureDate
   bloodsugarLevel {
     totalLevel
     count
     reports {
       id
       city {
         lat
         city
       }
       measure {
         diabetesIndicator
         measureDate
         pinLocation {
           latitude
         }

       }
     }
   }
   cholesterolLevel {
     totalLevel
     count
     reports {
       id
       measure {
         measureDate
         pinLocation {
           latitude
         }
       }
     }
   }
   }
 }

 query {
   healthQualityHistory(
     healthHistoryInput: {
     city: "600ea0788030fcb72e477e72"
     diabetesIndicator: BLOOD_SUGAR
     }
   ) {
     city
 				histories {
           measureDate
           level
           reports
         }
   }
 }

 query {
   healthQualityHistory(
     healthHistoryInput: {
     city: "600ea0788030fcb72e477e72"
     diabetesIndicator: CHOLESTEROL
     }
   ) {
     city
 				histories {
           measureDate
           level
           reports
         }
   }
 }

 mutation {
 updateUser(id: "600c8273f0ee39239d26984a",
   userUpdateInput: {
     firstname: "john"
     lastname: "mcgee"
     email: "sssss@a.com",
     addressInput: {
       street: "valhalla rd",
       postcode:"nw6 W4"
     }
   }) {
     firstname
     lastname
   email
   address {
     street
     postcode
   }
 }
 }

 mutation {
   createUser(
     userInput: {
       email: "abc@abc.com"
       password: "better"
       addressInput: {
         city: "600ea0788030fcb72e477e72"
         street: "test rd"
       }
     }
   ) {
     id
     address {
       street
       city {
         id
         lat
       }
     }
   }
 }

 mutation {
   createReport(
     reportInput: {
       userId: "600c8273f0ee39239d26984a"
       cityId: "600ea0788030fcb72e477e72"
       measureInput: {
         measureDate: "2021-01-26T22:20:30.000Z"
         diabetesIndicator: BLOOD_SUGAR
         pinLocation: { latitude: "196", longitude: "196" }
         device: "AirClean"
         level: "996"
       }
     }
   ) {
     measure {
       diabetesIndicator
       device
     }
   }
 }

 mutation {
   createReports(
     reports: [
       {
         userId: "600c8273f0ee39239d26984a"
         cityId: "600ea0788030fcb72e477e72"
         measureInput: {
           measureDate: "2021-01-26T22:20:30.000Z"
           diabetesIndicator: BLOOD_SUGAR
           pinLocation: { latitude: "96", longitude: "96" }
           device: "Boots-brand-1"
           level: "5"
         }
       }
       {
         userId: "600c8273f0ee39239d26984a"
         cityId: "600ea0788030fcb72e477e72"
         measureInput: {
           measureDate: "2021-01-26T22:20:30.000Z"
           diabetesIndicator: CHOLESTEROL
           pinLocation: { latitude: "96", longitude: "96" }
           device: "Boots-brand-2"
           level: "10"
         }
       }
     ]
   )
 }

 mutation {
   createHQSummary(
     healthQualityInput: {
       city: "600ea0788030fcb72e477e72"
       measureDate: "2021-01-24T00:00:00Z"
       cholesterolLevel: {
         count: 1
         totalLevel: 250
       }
       bloodsugarLevel: {
         count: 2
         totalLevel: 500
       }

     }
   ) {
     id
   }
 }

 mutation {
 updateReport(
   id: "6021af307f2ecc093f9f1251"
   reportUpdateInput: {
     measureInput: {
       level: "9060"
       diabetesIndicator: BLOOD_SUGAR
       device: "NewDevice"
       measureDate: "2021-01-27T22:20:30Z"
       pinLocation: { latitude: "966", longitude: "96"}
     }
   }
 ) {
   city {
     city
   }
   measure {
     diabetesIndicator
     device
     pinLocation {
       latitude
       longitude
     }
   }
 }
 }
```

