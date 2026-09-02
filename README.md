# The Coffeetariat Cafe Service APIs

This Web API hosts many features for various functionality in the form of REST endpoints.

## Regenerating the NIEM Core classes

The NIEM core classes should generate automatically through the maven build. The jaxb2-maven-plugin is configured to
read the NIEM core and pulls in all the imported schemas and etc and generates everything.
Surprisingly, this was all able to be done without binding files.

## Running the Cafe Application

The project contains multiple application classes. To run the Cafe service specifically, use the following command:

```bash
./mvnw spring-boot:run -Dspring-boot.run.mainClass=net.coffeetariat.cafe.Application
```

The service is configured to run on port `8666` (defined in `src/main/resources/application.properties`).

## Storage API Demo

A fully automated demo script `demo.sh` is provided to demonstrate the interaction between the `gryptography` authentication service and the `cafe` storage service.

### Prerequisites
- The `gryptography` service must be running on port `8080`.
- The `cafe` service must be running on port `8666`.
- `openssl`, `curl`, and `jq` must be installed.
- The script is written for `zsh`.

### Running the Demo
```bash
./demo.sh
```

The script will:
1. Register a new client with the `gryptography` service.
2. Perform a challenge-response authentication using embedded Q&A data.
3. Obtain a JWT token.
4. Upload a document to the `cafe` storage service.
5. List the contents of the storage.
6. Retrieve the uploaded document.

## The '/www-authenticate' route, what is it?

This endpoint is for testing to see how your browser will react to
a _401_ status code and the _WWW-Authenticate_ header on a response.

Calling the endpoint with a _HTTP GET_ will give you a _401_ with
a _WWW-Authenticate_ header with a scheme of 'Coffee' and a question.

Example GET in raw HTTP:

        GET /www-authenticate HTTP/1.1

Example in curl:

        curl -i -X "GET" http://localhost:8666/www-authenticate 


Example result from previous curl:

        HTTP/1.1 401 
        WWW-Authenticate: Coffee realm="beverages", question="A squirrel gazes intently at you, what do you do?"
        Content-Length: 0
        Date: Sat, 24 Aug 2024 04:32:15 GMT

The client may _HTTP POST_ a response to that question with a request
that has an _Authorization_ header with a scheme and answer.

Example in raw HTTP:

        POST /www-authenticate HTTP/1.1
        Authorization: Coffee answer="I stare intently back."

Example in curl:

        curl -i -X "POST" -H "Authorization: Coffee answer=\"I stare intently back.\"" http://localhost:8666/www-authenticate

Example response to previous curl:

        HTTP/1.1 200 
        Content-Type: text/plain;charset=UTF-8
        Content-Length: 0
        Date: Sat, 24 Aug 2024 04:34:38 GMT

With the challenge POST'd, the server side processes
the request and returns a result. Today this method doesn't
do much, but it was in the spirit of work I had done
for authenticating clients with password-less login.
The server had the client registered with their RSA public key
and the client would sign the challenge with their RSA private key.
The server could then verify that the client was the client
by verifying their public key.
I had made some effort to add HOBA information
to the mozilla documentation.