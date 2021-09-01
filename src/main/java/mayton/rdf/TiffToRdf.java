package mayton.rdf;

import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.formats.tiff.TiffField;
import org.apache.commons.imaging.formats.tiff.fieldtypes.FieldType;
import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static mayton.rdf.Constants.JPG_NS;

public class TiffToRdf {

    public static Logger logger = LoggerFactory.getLogger(TiffToRdf.class);

    public Statement apply(Model model, TiffField tiffField, int fileId) throws ImageReadException {
        // TODO: Impl
        if (tiffField.getFieldType() == FieldType.ASCII) {
            return model.createLiteralStatement(
                    model.createResource(JPG_NS + "id" + fileId),
                    model.createProperty(JPG_NS + tiffField.getTagName()),
                    tiffField.getStringValue());
        } else if (tiffField.getFieldType() == FieldType.LONG) {
            return model.createLiteralStatement(
                    model.createResource(JPG_NS + "id" + fileId),
                    model.createProperty(JPG_NS + tiffField.getTagName()),
                    tiffField.getIntValue());
        } else if (tiffField.getFieldType() == FieldType.DOUBLE) {{
            return model.createLiteralStatement(
                    model.createResource(JPG_NS + "id" + fileId),
                    model.createProperty(JPG_NS + tiffField.getTagName()),
                    tiffField.getDoubleValue());
        } else {
            logger.warn("Unable to recognize tiffFiled type = {}, ({})", tiffField.getFieldType(), tiffField.getTagName());
            return null;
        }
    }
}
