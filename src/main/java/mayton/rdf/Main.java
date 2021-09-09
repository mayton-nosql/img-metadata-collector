package mayton.rdf;

import org.apache.commons.cli.*;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.jena.graph.Graph;
import org.apache.jena.ontology.impl.OntModelImpl;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

import static mayton.rdf.Constants.*;

public class Main {



    public static Logger logger = LoggerFactory.getLogger(Main.class);

    public static Options createOptions() {
        return new Options()
                .addRequiredOption("s", "source", true, "Source file")
                .addRequiredOption("o", "out", true, "Out prefix");
    }

    public static void main(String[] args) throws ParseException, IOException {
        CommandLineParser parser = new DefaultParser();
        Options options = createOptions();
        if (args.length == 0) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("java -jar img-metadata-collector-xx.jar", createOptions(), true);
        } else {
            CommandLine line = parser.parse(options, args);
            if (line.hasOption("h")) {
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp("java -jar img-metadata-collector-xx.jar", createOptions(), true);
                return;
            }
            process(line);
        }
    }

    private static void process(CommandLine line) throws IOException {
        logger.info("Start");

        Model model = ModelFactory.createDefaultModel();

        model.setNsPrefix("rdf",  RDF_NS);
        model.setNsPrefix("rdfs", RDFS_NS);
        model.setNsPrefix("xsd",  XSD_NS);
        model.setNsPrefix("owl",  OWL_NS);

        model.setNsPrefix("jpg",  Constants.JPG_NS);
        model.setNsPrefix("file", Constants.FILE_NS);

        JpegMetaVisitor jpegMetaVisitor = new JpegMetaVisitor(model);

        StopWatch stopWatch = StopWatch.createStarted();
        Files.walkFileTree(Path.of(line.getOptionValue("s")), jpegMetaVisitor);
        stopWatch.stop();

        String out = line.getOptionValue("o");

        logger.info("Export ttl");

        StopWatch ttlWatch = StopWatch.createStarted();
        try(OutputStream os = new FileOutputStream(out + ".ttl")) {
            RDFDataMgr.write(
                    os,
                    model,
                    RDFFormat.TURTLE);
        }
        ttlWatch.stop();

        StopWatch rdfWatch = StopWatch.createStarted();
        try(OutputStream os = new FileOutputStream(out + ".xml")) {
            RDFDataMgr.write(
                    os,
                    model,
                    RDFFormat.RDFXML);
        }
        rdfWatch.stop();

        logger.info("Elapsed time:");
        logger.info("TURTLE save time  : {} s", ttlWatch.getTime(TimeUnit.SECONDS));
        logger.info("RDF/XML save time : {} s", rdfWatch.getTime(TimeUnit.SECONDS));
        logger.info("Finish");
    }
}
