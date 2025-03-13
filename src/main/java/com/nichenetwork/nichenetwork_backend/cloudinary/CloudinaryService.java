package com.nichenetwork.nichenetwork_backend.cloudinary;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class CloudinaryService {

    @Autowired
    private Cloudinary cloudinary;

    public CloudinaryService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    public Map uploadImage(MultipartFile file) throws IOException {
        return cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap("folder", "uploads", "public_id", file.getOriginalFilename()));
    }

    public void deleteImage(String publicId) throws IOException {
        System.out.println("elimino l'immagine su cloudinary con public ID: " + publicId);
        Map result = cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
        System.out.println("Risultato dell'eliminazione: " + result);
    }

    public Map uploadImageFromUrl(String imageUrl) throws IOException {

        Map<String, Object> options = new HashMap<>();
        options.put("folder", "uploads");

        System.out.println("🔹Caricamento da URL: " + imageUrl);

        Map result = cloudinary.uploader().upload(imageUrl, options);

        System.out.println("🔹Risultato caricamento da URL: " + result);

        return result;
    }
}
