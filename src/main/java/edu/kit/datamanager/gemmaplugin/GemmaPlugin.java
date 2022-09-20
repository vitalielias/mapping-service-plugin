package edu.kit.datamanager.gemmaplugin;

import edu.kit.datamanager.mappingservice.plugins.*;
import edu.kit.datamanager.mappingservice.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;
import java.nio.file.Path;

public class GemmaPlugin implements IMappingPlugin{
    private final Logger LOGGER = LoggerFactory.getLogger(GemmaPlugin.class);
    private static final String GEMMA_REPOSITORY = "https://github.com/kit-data-manager/gemma.git";
    private static final String GEMMA_BRANCH = "master";
    private static Path gemmaDir;

    @Override
    public String name() {
        return "GEMMA";
    }

    @Override
    public String description() {
        return "GEMMA is a tool written in Python that allows to map from JSON and XML to JSON. Furthermore, it allows to map wit a mapping schema.";
    }

    @Override
    public String version() {
        return "0.1.0";
    }

    @Override
    public String uri() {
        return "https://github.com/kit-data-manager/gemma";
    }

    @Override
    public MimeType[] inputTypes() {
        return new MimeType[]{MimeTypeUtils.APPLICATION_JSON, MimeTypeUtils.APPLICATION_XML};
    }

    @Override
    public MimeType[] outputTypes() {
        return new MimeType[]{MimeTypeUtils.APPLICATION_JSON};
    }

    @Override
    public void setup() {
        LOGGER.info("Checking and installing dependencies for Gemma: gemma, xmltodict, wget");
        try {
            PythonRunnerUtil.runPythonScript("-m", "pip", "install", "xmltodict", "wget");
            PythonRunnerUtil.runPythonScript("-m", new LoggerOutputStream(LOGGER, LoggerOutputStream.Level.DEBUG), new LoggerOutputStream(LOGGER, LoggerOutputStream.Level.DEBUG), "pip", "install", "xmltodict", "wget");
            gemmaDir = FileUtil.cloneGitRepository(GEMMA_REPOSITORY, GEMMA_BRANCH);
        } catch (Exception e) {
           e.printStackTrace();
        }
    }

    @Override
    public MappingPluginState mapFile(Path mappingFile, Path inputFile, Path outputFile) throws MappingPluginException {
        LOGGER.trace("Run gemma on '{}' with mapping '{}' -> '{}'", inputFile, mappingFile, outputFile);
        return PythonRunnerUtil.runPythonScript(gemmaDir + "/mapping_single.py", mappingFile.toString(), inputFile.toString(), outputFile.toString());
    }
}
