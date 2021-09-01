package mayton.rdf;

import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.Imaging;
import org.apache.commons.imaging.common.ImageMetadata;
import org.apache.commons.imaging.formats.jpeg.JpegImageMetadata;
import org.apache.commons.imaging.formats.tiff.TiffField;
import org.apache.commons.imaging.formats.tiff.TiffImageMetadata;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JpegMetaVisitor extends SimpleFileVisitor<Path> {

    static Logger logger = LoggerFactory.getLogger(JpegMetaVisitor.class);

    public static final Pattern JPEG_EXTENSION = Pattern.compile(".+\\.(?<extension>jpg|jfif|jpe|jpeg)$", Pattern.CASE_INSENSITIVE);

    private Model model;

    private int id = 0;

    public JpegMetaVisitor(Model model) {
        this.model = model;
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        String fileName = file.getFileName().toString();
        Matcher matcher = JPEG_EXTENSION.matcher(fileName);
        if (matcher.matches()) {
            logger.info("process jpeg file {}", fileName);
            logger.info("get metadata");
            ImageMetadata metadata;
            try {
                metadata = Imaging.getMetadata(new FileInputStream(file.toFile()), null);
                if (metadata != null) {
                    TiffImageMetadata items = ((JpegImageMetadata) metadata).getExif();
                    TiffToRdf tiffToRdf = new TiffToRdf();
                    id++;
                    for (TiffField tiffField : items.getAllFields()) {
                        Statement statement = tiffToRdf.apply(model, tiffField, id);
                        if (statement != null) {
                            model.add(statement);
                        }
                    }
                }
            } catch (ImageReadException e) {
                logger.warn("ImageReadException", e);
            }
        }
        return FileVisitResult.CONTINUE;
    }
}
