package com.reservaction.event_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "ImageData")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ImageData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String type;

    @Lob
    @Column(nullable = false)
    private byte[] compressedData;

    @Lob
    @Column(nullable = false)
    private byte[] decompressedData;

    public ImageData(byte[] compressedImage, byte[] decompressedImage) {
    }
}
