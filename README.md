# The Coffeetariat Cafe Service APIs

This Web API hosts many features for various functionality in the form of REST endpoints.

## Regenerating the NIEM Core classes

The NIEM core classes should generate automatically through the maven build. The jaxb2-maven-plugin is configured to
read the NIEM core and pulls in all the imported schemas and etc and generates everything.
Surprisingly, this was all able to be done without binding files.

## The '/www-authenticate' route, what is it?

This endpoint is for testing to see how your browser will react to
a _401_ status code and the _WWW-Authenticate_ header on a response.

Calling the endpoint with a _HTTP GET_ will give you a _401_ with
a _WWW-Authenticate_ header with a scheme of 'Coffee' and a question.

Example GET in raw HTTP:

        GET /www-authenticate HTTP/1.1

Example in curl:

        curl -i -X "GET" http://localhost:8080/www-authenticate 

The client may _HTTP POST_ a response to that question with a request
that has an _Authorization_ header with a scheme and answer.

Example in raw HTTP:

        POST /www-authenticate HTTP/1.1
        Authorization: Coffee answer="I stare intently back."

Example in curl:

        curl -i -X "POST" -H "Authorization: Coffee answer=\"I stare intently back.\"" http://localhost:8080/wwwauthenticate
