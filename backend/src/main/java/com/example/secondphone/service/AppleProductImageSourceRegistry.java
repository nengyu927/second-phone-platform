package com.example.secondphone.service;

import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Component;

@Component
public class AppleProductImageSourceRegistry {
    public record ImageSource(String sourceType, String previewUrl, String sourcePageUrl) { }

    private static final Map<String, ImageSource> SOURCES = Map.of(
            "iPhone 13", new ImageSource(
                    "WIKIMEDIA_COMMONS",
                    "https://commons.wikimedia.org/wiki/Special:Redirect/file/IPhone%2013%20iPhone%204s%20frontside.jpg",
                    "https://commons.wikimedia.org/wiki/File:IPhone_13_iPhone_4s_frontside.jpg"),
            "iPhone 14 Pro Max", new ImageSource(
                    "WIKIMEDIA_COMMONS",
                    "https://commons.wikimedia.org/wiki/Special:Redirect/file/Deep%20Purple%20iPhone%2014%20Pro%20Max%20front%20photo.jpg",
                    "https://commons.wikimedia.org/wiki/File:Deep_Purple_iPhone_14_Pro_Max_front_photo.jpg"),
            "Apple Watch Series 8", new ImageSource(
                    "WIKIMEDIA_COMMONS",
                    "https://commons.wikimedia.org/wiki/Special:Redirect/file/Apple%20Watch%20Series%208%20Midnight%20Aluminium%20Case.jpg",
                    "https://commons.wikimedia.org/wiki/File:Apple_Watch_Series_8_Midnight_Aluminium_Case.jpg"),
            "iPad Air 5", new ImageSource(
                    "APPLE_OFFICIAL",
                    "https://www.apple.com/newsroom/images/product/ipad/standard/Apple-iPad-Air-hero-color-lineup-220308_big.jpg.large.jpg",
                    "https://www.apple.com/newsroom/2022/03/apple-introduces-the-most-powerful-and-versatile-ipad-air-ever/"));

    public Optional<ImageSource> find(String normalizedModelName) {
        return Optional.ofNullable(SOURCES.get(normalizedModelName));
    }
}
