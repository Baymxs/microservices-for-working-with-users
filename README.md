# Microservices for working with users
Microservice architecture project that contains a web service and processing service. The web service receives requests from the client and sends them to the message queue and creates a callback queue to receive a response for a specific request from another service. The processing service receives messages from the queue, processes the requests accordingly and sends a response to the callback queue.
# REST API Reference
## Response Codes

```
200: Success
400: Bad request
404: Cannot be found
```
## Create user
---
#### Request URL:
`POST /api/v1/user/create`
#### Request Body:

```json
{
    "name": "Ivan",
    "surname": "Ivanov", 
    "patronymic": "Ivanovich", 
    "email": "ivanov_ivan777@gmail.com"
 }
```
#### Successful Response:
**Status:** `200`
```json
{
    "body": null,
    "message": "Success"
}
```
#### Failed Response:
**Status:** `400`
```json
{
    "body": null,
    "message": "User with email [ivanov_ivan777@gmail.com] has already been created"
}
```

## Update user
---
#### Request URL:
`PUT /api/v1/user/{id}`
#### Request Body:

```json
{
    "name": "Nikita",
    "surname": "Ivanov", 
    "patronymic": "Ivanovich", 
    "email": "ivanov_nikita383@gmail.com"
 }
```
#### Successful Response:
**Status:** `200`
```json
{
    "body": null,
    "message": "Success"
}
```
#### Failed Response:
**Status:** `404`
```json
{
    "body": null,
    "message": "User with id [2] not found"
}
```

## Get user
---
#### Request URL:
`GET /api/v1/user/{id}`

#### Successful Response:
**Status:** `200`
```json
{
    "body": {
        "name": "Nikita",
        "surname": "Ivanov", 
        "patronymic": "Ivanovich", 
        "email": "ivanov_nikita383@gmail.com"
    },
    "message": "Success"
}
```
#### Failed Response:
**Status:** `404`
```json
{
    "body": null,
    "message": "User with id [2] not found"
}
```

## Delete user
---
#### Request URL:
`DELETE /api/v1/user/{id}`

#### Successful Response:
**Status:** `200`
```json
{
    "body": null,
    "message": "Success"
}
```
#### Failed Response:
**Status:** `404`
```json
{
    "body": null,
    "message": "User with id [2] not found"
}
```

# License
MIT

----
> GitHub: [@baymxs](https://github.com/Baymxs) 
> VK: [@baymxs](https://vk.com/baymxs)
