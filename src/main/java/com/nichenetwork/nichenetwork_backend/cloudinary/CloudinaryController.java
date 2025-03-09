package com.nichenetwork.nichenetwork_backend.cloudinary;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/api/cloudinary")
public class CloudinaryController {

    private final CloudinaryService cloudinaryService;

    public CloudinaryController(CloudinaryService cloudinaryService) {
        this.cloudinaryService = cloudinaryService;
    }

    @Operation(
            summary = "Carica un'immagine su Cloudinary",
            requestBody = @RequestBody(
                    content = @Content(
                            mediaType = "multipart/form-data",
                            schema = @Schema(type = "string", format = "binary")
                    )
            )
    )
    @PostMapping(value = "/upload", consumes = "multipart/form-data")
    public ResponseEntity<?> uploadImage(@RequestParam("file")MultipartFile file,  HttpServletRequest request) {

        try {
            System.out.println("🔹 Nuova richiesta ricevuta: " + request.getMethod() + " " + request.getRequestURI());
            System.out.println("🔹 Headers: " + Collections.list(request.getHeaderNames()));
            System.out.println("🔹 Content-Type: " + request.getContentType());

            if (file.isEmpty()) {
                System.out.println("⚠️ Il file è vuoto!");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Errore: il file è vuoto!");
            }

            System.out.println("✅ Nome file: " + file.getOriginalFilename());
            System.out.println("✅ Dimensione file: " + file.getSize() + " bytes");
            System.out.println("✅ Tipo file: " + file.getContentType());

            Map result = cloudinaryService.uploadImage(file);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Errore durante l'upload dell'immagine: " + e.getMessage());
        }
    }
}
