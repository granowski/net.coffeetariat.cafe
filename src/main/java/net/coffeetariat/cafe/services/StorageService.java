package net.coffeetariat.cafe.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class StorageService {

    private final Path rootLocation;

    public StorageService(@Value("${storage.base-path:./client-data}") String baseStoragePath) throws IOException {
        this.rootLocation = Paths.get(baseStoragePath).toAbsolutePath().normalize();
        Files.createDirectories(rootLocation);
    }

    private Path getClientPath(String clientId) throws IOException {
        Path clientPath = rootLocation.resolve(clientId).normalize();
        if (!clientPath.startsWith(rootLocation)) {
            throw new SecurityException("Client ID attempt to escape storage root");
        }
        if (!Files.exists(clientPath)) {
            Files.createDirectories(clientPath);
        }
        return clientPath;
    }

    private Path resolvePath(String clientId, String subPath) throws IOException {
        Path clientRoot = getClientPath(clientId);
        Path resolved = clientRoot.resolve(subPath).normalize();
        if (!resolved.startsWith(clientRoot)) {
            throw new SecurityException("Path traversal attempt");
        }
        return resolved;
    }

    public List<String> list(String clientId, String subPath) throws IOException {
        Path targetPath = resolvePath(clientId, subPath);
        if (!Files.exists(targetPath)) {
            throw new NoSuchFileException(subPath);
        }
        if (!Files.isDirectory(targetPath)) {
            throw new NotDirectoryException(subPath);
        }
        try (Stream<Path> stream = Files.list(targetPath)) {
            Path clientRoot = getClientPath(clientId);
            return stream.map(p -> clientRoot.relativize(p).toString())
                    .collect(Collectors.toList());
        }
    }

    public void createFile(String clientId, String subPath, byte[] content) throws IOException {
        Path targetPath = resolvePath(clientId, subPath);
        Files.createDirectories(targetPath.getParent());
        Files.write(targetPath, content, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    public void createDirectory(String clientId, String subPath) throws IOException {
        Path targetPath = resolvePath(clientId, subPath);
        Files.createDirectories(targetPath);
    }

    public byte[] readFile(String clientId, String subPath) throws IOException {
        Path targetPath = resolvePath(clientId, subPath);
        if (Files.isDirectory(targetPath)) {
            throw new IsDirectoryException(subPath);
        }
        return Files.readAllBytes(targetPath);
    }

    public void delete(String clientId, String subPath) throws IOException {
        Path targetPath = resolvePath(clientId, subPath);
        if (!Files.exists(targetPath)) {
            throw new NoSuchFileException(subPath);
        }
        if (Files.isDirectory(targetPath)) {
            deleteRecursively(targetPath);
        } else {
            Files.delete(targetPath);
        }
    }

    private void deleteRecursively(Path path) throws IOException {
        try (Stream<Path> stream = Files.walk(path)) {
            List<Path> paths = stream.sorted((a, b) -> b.compareTo(a)).collect(Collectors.toList());
            for (Path p : paths) {
                Files.delete(p);
            }
        }
    }

    public static class IsDirectoryException extends IOException {
        public IsDirectoryException(String message) {
            super(message);
        }
    }
}
