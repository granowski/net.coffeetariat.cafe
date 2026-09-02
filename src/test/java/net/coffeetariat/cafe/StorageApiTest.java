package net.coffeetariat.cafe;

import net.coffeetariat.cafe.security.GryptographyTokenValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class StorageApiTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GryptographyTokenValidator tokenValidator;

    private final String testClientId = "test-client";
    private final String testToken = "Bearer valid-token";

    @BeforeEach
    public void setup() throws Exception {
        when(tokenValidator.validateAndGetClientId("valid-token")).thenReturn(testClientId);
        
        // Clean up storage directory
        Path storageRoot = Path.of("./client-data");
        if (Files.exists(storageRoot)) {
            Files.walk(storageRoot)
                .sorted(Comparator.reverseOrder())
                .map(Path::toFile)
                .forEach(File::delete);
        }
    }

    @Test
    public void testCrudOperations() throws Exception {
        // 1. List root (empty)
        mockMvc.perform(get("/api/storage")
                .header("Authorization", testToken))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        // 2. Create directory
        mockMvc.perform(post("/api/storage/my-dir")
                .header("Authorization", testToken)
                .param("type", "directory"))
                .andExpect(status().isCreated());

        // 3. List root (should contain my-dir)
        mockMvc.perform(get("/api/storage")
                .header("Authorization", testToken))
                .andExpect(status().isOk())
                .andExpect(content().json("[\"my-dir\"]"));

        // 4. Create file in directory
        mockMvc.perform(post("/api/storage/my-dir/test.txt")
                .header("Authorization", testToken)
                .content("Hello World")
                .contentType(MediaType.APPLICATION_OCTET_STREAM))
                .andExpect(status().isCreated());

        // 5. Read file
        mockMvc.perform(get("/api/storage/my-dir/test.txt")
                .header("Authorization", testToken))
                .andExpect(status().isOk())
                .andExpect(content().string("Hello World"));

        // 6. Update file
        mockMvc.perform(put("/api/storage/my-dir/test.txt")
                .header("Authorization", testToken)
                .content("Updated Content")
                .contentType(MediaType.APPLICATION_OCTET_STREAM))
                .andExpect(status().isOk());

        // 7. Read updated file
        mockMvc.perform(get("/api/storage/my-dir/test.txt")
                .header("Authorization", testToken))
                .andExpect(status().isOk())
                .andExpect(content().string("Updated Content"));

        // 8. Delete file
        mockMvc.perform(delete("/api/storage/my-dir/test.txt")
                .header("Authorization", testToken))
                .andExpect(status().isNoContent());

        // 9. Read deleted file (404)
        mockMvc.perform(get("/api/storage/my-dir/test.txt")
                .header("Authorization", testToken))
                .andExpect(status().isNotFound());

        // 10. Delete directory
        mockMvc.perform(delete("/api/storage/my-dir")
                .header("Authorization", testToken))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testAuthFailure() throws Exception {
        when(tokenValidator.validateAndGetClientId("invalid-token")).thenThrow(new SecurityException("Invalid token"));

        mockMvc.perform(get("/api/storage")
                .header("Authorization", "Bearer invalid-token"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testPathTraversal() throws Exception {
        mockMvc.perform(get("/api/storage/../other-client")
                .header("Authorization", testToken))
                .andExpect(status().isForbidden());
    }
}
