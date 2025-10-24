package com.renatobonfim.aemblogbackend.utils;

import com.renatobonfim.aemblogbackend.config.Constants;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.springframework.web.multipart.MultipartFile;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

public class CustomUtils {

    public static String saveImage(String entityID, MultipartFile multipartFile, String storageLocation) {

        BiFunction<String, MultipartFile, String> imageFunction = (id, file) -> {
            try {
                String fileName = id + fileExtension.apply(multipartFile.getOriginalFilename());
                Path fileStorageLocation = Paths.get(storageLocation).normalize();

                if (!Files.exists(fileStorageLocation)) {
                    Files.createDirectories(fileStorageLocation);
                }
                Files.copy(multipartFile.getInputStream(), fileStorageLocation.resolve(fileName), REPLACE_EXISTING);

                return Constants.CURRENT_HOST +  storageLocation + fileName;

            } catch (Exception ex) {
                throw new RuntimeException("unable to save image");
            }
        };
        return imageFunction.apply(entityID, multipartFile);
    }

    private static final Function<String, String> fileExtension = (filename) -> Optional.of(filename).filter(name -> name.contains("."))
            .map(name -> "." + name.substring(filename.lastIndexOf(".") + 1)).orElse(".png");
}
