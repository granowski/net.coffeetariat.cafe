package net.coffeetariat.cafe.controllers;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/wwwauthenticate")
public class WorldWideWebAuthenticate {
    @GetMapping
    public ResponseEntity get() {
        return firstChallenging();
    }

    private static ResponseEntity<Object> firstChallenging() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("WWW-Authenticate", "Coffee realm=\"beverages\", question=\"A squirrel gazes intently at you, what do you do?\"");
        return ResponseEntity.status(401).headers(headers).body(null);
    }

    @PostMapping
    public ResponseEntity<Object> post(@RequestHeader String authorization) {
        String token = "";

        if (authorization == null) return firstChallenging();
        if (authorization.isEmpty()) return firstChallenging();

        String normalizedAuthorizationValue = authorization.toLowerCase();

        if (normalizedAuthorizationValue.startsWith("coffee")) {
            // todo -> more logic, process the answer from the challenge question
            return ResponseEntity.ok().body(token);
        }
        else
            return ResponseEntity.status(403).body("Authorization header contains invalid scheme for this API.");
    }
}
