package studio.dillonb07.slackbridge.config;

import com.google.gson.GsonBuilder;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import studio.dillonb07.slackbridge.Config;

import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;

import static studio.dillonb07.slackbridge.Slackbridge.*;


public class ConfigManager {

    public static void init(boolean throwException) throws Exception {
        if (CONFIG_FILE.length() != 0) {
            try {
                FileUtils.copyFile(CONFIG_FILE, BACKUP_CONFIG_FILE);
                load();
            } catch (Exception e) {
                if (throwException) {
                    throw e;
                }

                LOGGER.error(ExceptionUtils.getStackTrace(e));
            }
        } else {
            create();

            LOGGER.error("-----------------------------------------");
            LOGGER.error("Error: The config file cannot be found or is empty!");
            LOGGER.error("");
            LOGGER.error("Please follow the documentation to configure Slackbridge before restarting the server!");
            LOGGER.error("More information + Docs: https://github.com/DillonB07/Slackbridge");
            LOGGER.error("");
            LOGGER.error("Stopping the server...");
            LOGGER.error("-----------------------------------------");

            System.exit(0);
        }
    }

    private static void create() {
        try (FileOutputStream outputStream = new FileOutputStream(CONFIG_FILE)) {
            String jsonString = new GsonBuilder()
                    .setPrettyPrinting()
                    .disableHtmlEscaping()
                    .create()
                    .toJson(new Config());

            IOUtils.write(jsonString, outputStream, StandardCharsets.UTF_8);
        } catch (Exception e) {
            LOGGER.error(ExceptionUtils.getStackTrace(e));
        }
    }

    private static void load() {
        try {
            CONFIG = new GsonBuilder()
                    .setPrettyPrinting()
                    .disableHtmlEscaping()
                    .create()
                    .fromJson(IOUtils.toString(CONFIG_FILE.toURI(), StandardCharsets.UTF_8), Config.class);
        } catch (Exception e) {
            LOGGER.error(ExceptionUtils.getStackTrace(e));
        }
    }
}