package com.reservaction.event_service.service;

import com.reservaction.event_service.entity.ImageData;
import com.reservaction.event_service.repository.ImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Optional;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

@Service
public class ImageService {
    @Autowired
    private ImageRepository imageRepository;

    public ImageData uploadImage(MultipartFile file) throws IOException {
        return imageRepository.save(ImageData.builder()
                .name(file.getOriginalFilename())
                .type(file.getContentType())
                .imageData(compressImage(file.getBytes()))
                .build());
    }

    public byte[] downloadImage(String fileName) {
        Optional<ImageData> dbImageData = imageRepository.findByName(fileName);
        return decompressImage(dbImageData.get().getImageData());
    }

    private byte[] compressImage(byte[] data) {
        Deflater deflater = new Deflater();
        deflater.setLevel(Deflater.BEST_COMPRESSION);
        deflater.setInput(data);
        deflater.finish();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
        byte[] tmp = new byte[4 * 1024];
        while (!deflater.finished()) {
            int size = deflater.deflate(tmp);
            outputStream.write(tmp, 0, size);
        }
        return outputStream.toByteArray();
    }

    private byte[] decompressImage(byte[] data) {
        Inflater inflater = new Inflater();
        inflater.setInput(data);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
        byte[] tmp = new byte[4 * 1024];
        try {
            while (!inflater.finished()) {
                int count = inflater.inflate(tmp);
                outputStream.write(tmp, 0, count);
            }
        } catch (Exception ignored) {
        }
        return outputStream.toByteArray();
    }
}
