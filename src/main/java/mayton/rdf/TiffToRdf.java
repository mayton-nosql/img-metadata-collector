package mayton.rdf;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.formats.tiff.TiffField;
import org.apache.commons.imaging.formats.tiff.fieldtypes.FieldType;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Triple;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Function;

import static mayton.rdf.Constants.JPG_NS;
import static mayton.rdf.RdfTools.normalizeLiteral;

public class TiffToRdf implements Function<Triple<Model, TiffField, Integer>, Statement> {

    public static Logger logger = LoggerFactory.getLogger(TiffToRdf.class);

    @Override
    public Statement apply(Triple<Model, TiffField, Integer> modelTiffFieldIntegerTriple) {
        Model model = modelTiffFieldIntegerTriple.getLeft();
        TiffField tiffField = modelTiffFieldIntegerTriple.getMiddle();
        int fileId = modelTiffFieldIntegerTriple.getRight();
        try {
            // TODO: Impl
            String tagName = normalizeLiteral(tiffField.getTagName());
            String subject = JPG_NS + "id" + fileId;
            if (tiffField.getFieldType() == FieldType.ASCII) {
                return model.createLiteralStatement(
                        model.createResource(subject),
                        model.createProperty(JPG_NS + tagName),
                        tiffField.getStringValue());
            } else if (tiffField.getFieldType() == FieldType.LONG) {
                return model.createLiteralStatement(
                        model.createResource(subject),
                        model.createProperty(JPG_NS + tagName),
                        tiffField.getIntValue());
            } else if (tiffField.getFieldType() == FieldType.BYTE) {
                return model.createLiteralStatement(
                        model.createResource(subject),
                        model.createProperty(JPG_NS + tagName),
                        Hex.encodeHexString(tiffField.getByteArrayValue()));
            } else if (tiffField.getFieldType() == FieldType.SHORT) {
                Object val = tiffField.getValue();
                if (val instanceof Short) {
                    int intVal = ((Short) val).intValue();
                    return model.createLiteralStatement(
                            model.createResource(subject),
                            model.createProperty(JPG_NS + tagName),
                            intVal);
                } else {
                    return null;
                }
            } else if (tiffField.getFieldType() == FieldType.DOUBLE) {
                return model.createLiteralStatement(
                        model.createResource(subject),
                        model.createProperty(JPG_NS + tagName),
                        tiffField.getDoubleValue());
            } else {
                logger.warn("Unable to recognize tiffFiled type = {}, tagname = {}",
                        tiffField.getFieldType(), tiffField.getTagName());
                return null;
            }
        } catch (ImageReadException ex) {
            return null;
        }
    }
}
