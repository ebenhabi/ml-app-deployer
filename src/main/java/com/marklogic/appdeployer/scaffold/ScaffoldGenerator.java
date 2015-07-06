package com.marklogic.appdeployer.scaffold;

import java.io.File;
import java.io.IOException;

import org.springframework.util.FileCopyUtils;

import com.fasterxml.jackson.core.PrettyPrinter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.marklogic.appdeployer.AppConfig;
import com.marklogic.appdeployer.spring.RestApiUtil;
import com.marklogic.clientutil.LoggingObject;

/**
 * Lots of protected methods in here to encourage subclassing and overriding behavior.
 */
public class ScaffoldGenerator extends LoggingObject {

    protected ObjectMapper objectMapper = new ObjectMapper();
    private PrettyPrinter prettyPrinter = new DefaultPrettyPrinter();

    public void generateScaffold(String path, AppConfig config) {
        File rootDir = new File(path);

        File configDir = getConfigDir(rootDir);
        configDir.mkdirs();

        File modulesDir = getModulesDir(rootDir);
        modulesDir.mkdirs();

        generateRestApiFile(configDir, config);
        generateContentDatabaseFile(configDir, config);
        generateSecurityFiles(configDir, config);
    }

    protected void generateSecurityFiles(File configDir, AppConfig config) {
        File rolesDir = new File(configDir, "security/roles");
        rolesDir.mkdirs();
        writeFile(buildAppRole(config), new File(rolesDir, config.getName() + "-role.json"));

        File usersDir = new File(configDir, "security/users");
        usersDir.mkdirs();
        writeFile(buildAppUser(config), new File(usersDir, config.getName() + "-user.json"));
    }

    protected ObjectNode buildAppRole(AppConfig config) {
        ObjectNode node = objectMapper.createObjectNode();
        node.put("role-name", config.getName() + "-role");
        ArrayNode array = node.putArray("role");
        array.add("rest-writer");
        return node;
    }

    protected ObjectNode buildAppUser(AppConfig config) {
        ObjectNode node = objectMapper.createObjectNode();
        String name = config.getName() + "-user";
        node.put("user-name", name);
        node.put("password", name);
        ArrayNode roles = node.putArray("role");
        roles.add(config.getName() + "-role");
        return node;
    }

    protected void generateRestApiFile(File configDir, AppConfig config) {
        writeFile(buildRestApiJson(config), new File(configDir, "rest-api.json"));
    }

    protected ObjectNode buildRestApiJson(AppConfig config) {
        return RestApiUtil.buildDefaultRestApiJson();
    }

    protected void generateContentDatabaseFile(File configDir, AppConfig config) {
        File databasesDir = new File(configDir, "databases");
        databasesDir.mkdirs();

        writeFile(buildContentDatabaseJson(config), new File(databasesDir, "content-database.json"));
    }

    protected ObjectNode buildContentDatabaseJson(AppConfig config) {
        ObjectNode node = objectMapper.createObjectNode();
        node.put("database-name", "%%DATABASE%%");
        ArrayNode array = node.putArray("range-element-index");
        ObjectNode index = array.addObject();
        index.put("scalar-type", "string");
        index.put("namespace-uri", "CHANGEME");
        index.put("localname", "CHANGEME");
        index.put("collation", "http://marklogic.com/collation");
        index.put("range-value-positions", false);
        index.put("invalid-values", "reject");
        return node;
    }

    protected void writeFile(ObjectNode node, File f) {
        try {
            logger.info("Writing: " + f.getAbsolutePath());
            byte[] bytes = objectMapper.writer(prettyPrinter).writeValueAsBytes(node);
            FileCopyUtils.copy(bytes, f);
        } catch (IOException ie) {
            throw new RuntimeException("Unable to write JSON to file at: " + f.getAbsolutePath() + "; cause: "
                    + ie.getMessage(), ie);
        }
    }

    protected File getConfigDir(File rootDir) {
        return new File(rootDir, "src/main/ml-config");
    }

    protected File getModulesDir(File rootDir) {
        return new File(rootDir, "src/main/ml-modules");
    }

}
