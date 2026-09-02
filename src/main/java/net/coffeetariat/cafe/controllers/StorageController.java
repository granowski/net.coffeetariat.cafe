package net.coffeetariat.cafe.controllers;

import net.coffeetariat.cafe.security.GryptographyTokenValidator;
import net.coffeetariat.cafe.services.StorageService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.nio.file.NotDirectoryException;
import java.util.List;

@RestController
@RequestMapping("/api/storage")
public class StorageController {

    private final StorageService storageService;
    private final GryptographyTokenValidator tokenValidator;

    public StorageController(StorageService storageService, GryptographyTokenValidator tokenValidator) {
        this.storageService = storageService;
        this.tokenValidator = tokenValidator;
    }

    private String getClientId(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new SecurityException("Missing or invalid Authorization header");
        }
        String token = authHeader.substring(7);
        try {
            return tokenValidator.validateAndGetClientId(token);
        } catch (Exception e) {
            throw new SecurityException("Authentication failed: " + e.getMessage(), e);
        }
    }

    @GetMapping("/**")
    public ResponseEntity<?> get(@RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authHeader,
                                 HttpServletRequest request) {
        try {
            String clientId = getClientId(authHeader);
            String path = extractPath(request);
            
            try {
                // Try listing as directory first
                List<String> contents = storageService.list(clientId, path);
                return ResponseEntity.ok(contents);
            } catch (NotDirectoryException e) {
                // It's a file, read it
                byte[] data = storageService.readFile(clientId, path);
                return ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_OCTET_STREAM)
                        .body(data);
            }
        } catch (NoSuchFileException e) {
            return ResponseEntity.notFound().build();
        } catch (SecurityException e) {
            if (e.getMessage() != null && (e.getMessage().contains("Authentication failed") || 
                e.getMessage().contains("Missing") || 
                e.getMessage().contains("Invalid Authorization"))) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
            }
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("/**")
    public ResponseEntity<?> post(@RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authHeader,
                                  @RequestParam(value = "type", defaultValue = "file") String type,
                                  @RequestBody(required = false) byte[] content,
                                  HttpServletRequest request) {
        try {
            String clientId = getClientId(authHeader);
            String path = extractPath(request);

            if ("directory".equalsIgnoreCase(type)) {
                storageService.createDirectory(clientId, path);
            } else {
                storageService.createFile(clientId, path, content != null ? content : new byte[0]);
            }
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (SecurityException e) {
            if (e.getMessage() != null && (e.getMessage().contains("Authentication failed") || 
                e.getMessage().contains("Missing") || 
                e.getMessage().contains("Invalid Authorization"))) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
            }
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PutMapping("/**")
    public ResponseEntity<?> put(@RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authHeader,
                                 @RequestBody byte[] content,
                                 HttpServletRequest request) {
        try {
            String clientId = getClientId(authHeader);
            String path = extractPath(request);
            storageService.createFile(clientId, path, content);
            return ResponseEntity.ok().build();
        } catch (SecurityException e) {
            if (e.getMessage() != null && (e.getMessage().contains("Authentication failed") || 
                e.getMessage().contains("Missing") || 
                e.getMessage().contains("Invalid Authorization"))) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
            }
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @DeleteMapping("/**")
    public ResponseEntity<?> delete(@RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authHeader,
                                    HttpServletRequest request) {
        try {
            String clientId = getClientId(authHeader);
            String path = extractPath(request);
            storageService.delete(clientId, path);
            return ResponseEntity.noContent().build();
        } catch (NoSuchFileException e) {
            return ResponseEntity.notFound().build();
        } catch (SecurityException e) {
            if (e.getMessage() != null && (e.getMessage().contains("Authentication failed") || 
                e.getMessage().contains("Missing") || 
                e.getMessage().contains("Invalid Authorization"))) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
            }
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    private String extractPath(HttpServletRequest request) {
        String fullPath = (String) request.getAttribute(org.springframework.web.servlet.HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
        String bestMatchPattern = (String) request.getAttribute(org.springframework.web.servlet.HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        
        // Remove prefix "/api/storage"
        String prefix = bestMatchPattern.replace("/**", "");
        if (fullPath.startsWith(prefix)) {
            String path = fullPath.substring(prefix.length());
            if (path.startsWith("/")) {
                path = path.substring(1);
            }
            return path;
        }
        return "";
    }
}
