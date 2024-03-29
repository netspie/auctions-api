# Auctions API

&nbsp;&nbsp;&nbsp;&nbsp; Auctions API which original purpose was to serve as an internal service of distributed system for a potential game system for trading items between players.

### Technologies

&nbsp;&nbsp;&nbsp;&nbsp; Kotlin, Ktor, Exposed/PostgreSQL, Firebase Authentication

### Architecture

&nbsp;&nbsp;&nbsp;&nbsp; Project is divided to layers based on varation of clean architecture and CQRS with repository pattern. It also containes basic layer for the most common extensions and building blocks which can be used anywhere.
  - [Basic layer](https://github.com/netspie/auctions-api/tree/main/src/auctions/src/main/kotlin/com/slaycard/basic)
  - [Domain Entities](https://github.com/netspie/auctions-api/tree/main/src/auctions/src/main/kotlin/com/slaycard/entities)
  - [Use Cases](https://github.com/netspie/auctions-api/tree/main/src/auctions/src/main/kotlin/com/slaycard/useCases)
  - [Infrastructure](https://github.com/netspie/auctions-api/tree/main/src/auctions/src/main/kotlin/com/slaycard/infrastructure)
  - [Auction Entity Tests](https://github.com/netspie/auctions-api/blob/main/src/auctions/src/test/kotlin/com/slaycard/AuctionTest.kt)
  
## API

**Access Roles**  
  
Each endpoint requires a certain role to be able to execute it successfully. Depending on a role the same endpoint might return different amounts of data. The roles, from the least to the most privileged, are:

- Guest - no token required in authorization Header
- Any Signed User - token required identyifing the user
- Resource Owner User - token required and the user must be the owner of the resource
- Admin - token required with administrator privileges

&nbsp;&nbsp;&nbsp;&nbsp;  
**Endpoints**
<br>  
[Routes Code Implementation](https://github.com/netspie/auctions-api/blob/main/src/auctions/src/main/kotlin/com/slaycard/api/plugins/Routing.kt)
| HTTP Method | Endpoint | Description | Access Role |
| --- | --- | --- | --- | 
| `GET` | `/auctions/{auctionId}` | Get specific auction details | Guest |
| `GET` | `/auctions` | Get multiple auctions details | Guest |
| `POST` | `/auctions` | Add new auction | Any Signed User |
| `DELETE` | `/auctions/{auctionId}` | Cancel ongoing auction | Resource Owner User |
| --- | --- | --- | --- |
| `POST` | `/auctions/{auctionId}/bids` | Add new bid | Any Signed Owner User |

&nbsp;&nbsp;&nbsp;&nbsp;  
## API Details

### Requests  

Data resources are accessed via standard HTTP requests in UTF-8 format to an API endpoint. The Web API uses the following HTTP verbs:

| Http Method | Description |
| --- | --- |
| GET | Retrieves resources |
| POST | Creates resources |
| PUT | Changes and/or replaces resources or collections |
| DELETE | Deletes resources |

### Responses  

Web API normally returns JSON in the response body. In case of some errors it will return HTTP status code (500 or 403). The response body JSON is a special "Result" object which might contain a value - if it's a GET request, and/or it might additionally contain messages depending on how the request was processed. A message 

&nbsp;&nbsp;&nbsp;&nbsp; *Result Fields*

- object
  - messages - array of objects
    - object
      - level - object
        - name - string; could be "error", "warning" or "info"
      - content - string; message
      
*Example:*
```
HTTP/1.1 400 Bad Request
{
    "messages": [
        {
            "level": {
                "name": "error"
            },
            "content": "The new price must be greater than previous by 5% or more"
        }
    ]
}
```

### Example API Endpoint - Get auction

| HTTP Method | Endpoint | Description | Access Role |
| --- | --- | --- | --- | 
| `GET` | `/auctions/{auctionId}` | Get specific auction details | Guest |

#### Description

Get details for a specific auction.

#### Success Response

##### *200*
&nbsp;&nbsp;&nbsp;&nbsp; Object containing a value with auction

&nbsp;&nbsp;&nbsp;&nbsp; *Fields*
- object
  - value - object
    - id - string
    - sellingUserId - string; uuid format
    - itemId - string; uuid format
    - itemName - string, max 30 characters
    - description - string; max 2000 characters
    - quantity - integer
    - startingPrice - integer
    - currentPrice - integer
    - startTime - dateTime; YYYY-MM-DDThh:mm:ss.msμsns
    - originalDurationHours - integer
    - isFinished - boolean
    - inCancelled - boolean
    - winnerUserId - string; uuid format

&nbsp;&nbsp;&nbsp;&nbsp; *Example*

```json
{
    "value": {
      "id": "880c35cc-94d2-4177-83d2-69847b7b9ed1",
      "sellingUserId": "c64f1808-108c-4afb-a759-148f8472167d",
      "itemName": "Axe",
      "description": "Very sharp axe",
      "quantity": 1,
      "startingPrice": 100,
      "currentPrice": 100,
      "startTime": "2023-12-31T16:52:28.334451",
      "originalDurationHours": 72,
      "isFinished": false,
      "isCancelled": false,
      "winnerId": "null",
      "cancelTime": null
  }
}
```


