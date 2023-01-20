package edu.kit.datamanager.NEPMetadataMapping;

import edu.kit.datamanager.mappingservice.plugins.*;
import edu.kit.datamanager.mappingservice.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;
import java.nio.file.Path;

public class NEPMetadataMapping implements IMappingPlugin{
    private final Logger LOGGER = LoggerFactory.getLogger(NEPMetadataMapping.class);
    private static final String REPOSITORY = "https://github.com/kit-data-manager/SEM-Mapping-Tool.git";
    private static final String BRANCH = "main";
    private static Path dir;

    @Override
    public String name() {
        return "SEMMetadataMapping";
    }

    @Override
    public String description() {
        return "A mapping tool that facilitates metadata mapping from TIFF formats to JSON schemas.";
    }

    @Override
    public String version() {
        return "0.1.0";
    }

    @Override
    public String uri() {
        return "https://github.com/kit-data-manager/SEM-Mapping-Tool";
    }

    @Override
    public MimeType[] inputTypes() {
        return new MimeType[]{};
    }

    @Override
    public MimeType[] outputTypes() {
        return new MimeType[]{MimeTypeUtils.APPLICATION_JSON};
    }

    @Override
    public void setup() {
        LOGGER.info("Checking and installing dependencies for SEM Mapping Tool: ");
        try {
            PythonRunnerUtil.runPythonScript("-m", "pip", "install", "pydicom", "jsonschema", "logging", "zipfile", "typing", "datetime");
            dir = FileUtil.cloneGitRepository(REPOSITORY, BRANCH);
        } catch (Exception e) {
           e.printStackTrace();
        }
    }

    @Override
    public MappingPluginState mapFile(Path mappingFile, Path inputFile, Path outputFile) throws MappingPluginException {
        LOGGER.trace("Run NEPMetadataMapping on '{}' with mapping '{}' -> '{}'", mappingFile, inputFile, outputFile);
        return PythonRunnerUtil.runPythonScript(dir + "/dicom_mapping_script.py", mappingFile.toString(), inputFile.toString(), outputFile.toString());
    }
}
