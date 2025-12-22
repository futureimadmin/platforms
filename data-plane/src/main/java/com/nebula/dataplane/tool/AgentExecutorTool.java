package com.nebula.dataplane.tool;

import com.google.adk.tools.Annotations;
import com.google.adk.tools.ToolContext;
import com.nebula.dataplane.model.CodeFile;
import com.nebula.dataplane.model.CodingOutput;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

@Component
public class AgentExecutorTool {
    /**
     * Creates or updates a file with the given package structure and content.
     * If the file already exists, it will be overwritten.
     *
     * @return A message indicating success or failure
     */
    public static Map<String, Object> generateExecutorJobSources (
            @Annotations.Schema(name="planId", description = "Plan Id of the execution plan") String planId,
            @Annotations.Schema(name="localPath", description = "Local path to save files e.g, functions directory in data-plane") String localPath,
            @Annotations.Schema(name="files", description = "The files to create or update") CodingOutput files,
            @Annotations.Schema(name="toolContext", description = "The tool context") ToolContext toolContext) {
        try {
            saveFiles(localPath, files.getCodeFiles());
            toolContext.state().put("Status", "Successfully generated Executor Function");
            return toolContext.state();
        } catch (IOException e) {
            toolContext.state().put("Status", "Error in generating sources for planId: "+planId);
            return toolContext.state();
        }
    }

    private static void saveFiles(String basePath, List<CodeFile> files) throws IOException {
        for (CodeFile file : files) {
            String fileName = file.getFileName();
            String content = file.getContent();

            // Create parent directories if they don't exist
            Path targetFile = Paths.get(basePath, fileName);
            Files.createDirectories(targetFile.getParent());

            // Save the file
            Files.writeString(targetFile, content, StandardCharsets.UTF_8);
        }
    }

    private static String generatePomXml(String planId) {
        StringBuilder pom = new StringBuilder();
        pom.append("<project xmlns=\\\"http://maven.apache.org/POM/4.0.0\\\" xmlns:xsi=\\\"http://www.w3.org/2001/XMLSchema-instance\\\"\\n")
                .append("    xsi:schemaLocation=\\\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd\\\">\\n")
                .append("    <modelVersion>4.0.0</modelVersion>\\n\\n")
                .append("    <groupId>com.nebula.dataplane.executors</groupId>\\n")
                .append("    <artifactId>").append(planId).append("-job</artifactId>\\n")
                .append("    <version>1.0.0</version>\\n")
                .append("    <packaging>jar</packaging>\\n\\n")
                .append("    <properties>\\n")
                .append("        <maven.compiler.source>17</maven.compiler.source>\\n")
                .append("        <maven.compiler.target>17</maven.compiler.target>\\n")
                .append("    </properties>\\n\\n")
                .append("    <dependencies>\\n")
                .append("        <dependency>\\n")
                .append("            <groupId>junit</groupId>\\n")
                .append("            <artifactId>junit</artifactId>\\n")
                .append("            <version>4.13.2</version>\\n")
                .append("            <scope>test</scope>\\n")
                .append("        </dependency>\\n")
                .append("        <dependency>\\n")
                .append("            <groupId>com.google.truth</groupId>\\n")
                .append("            <artifactId>truth</artifactId>\\n")
                .append("            <version>1.4.0</version>\\n")
                .append("            <scope>test</scope>\\n")
                .append("        </dependency>\\n")
                .append("        <dependency>\\n")
                .append("            <groupId>com.google.cloud</groupId>\\n")
                .append("            <artifactId>google-cloud-logging</artifactId>\\n")
                .append("            <version>3.15.0</version>\\n")
                .append("            <scope>test</scope>\\n")
                .append("        </dependency>\\n")
                .append("    </dependencies>\\n\\n")
                .append("    <build>\\n")
                .append("        <plugins>\\n")
                .append("            <plugin>\\n")
                .append("                <groupId>org.apache.maven.plugins</groupId>\\n")
                .append("                <artifactId>maven-jar-plugin</artifactId>\\n")
                .append("                <version>3.3.0</version>\\n")
                .append("                <configuration>\\n")
                .append("                    <archive>\\n")
                .append("                        <manifest>\\n")
                .append("                            <addClasspath>true</addClasspath>\\n")
                .append("                            <mainClass>com.nebula.dataplane.executors.job.ExecutorJob</mainClass>\\n")
                .append("                        </manifest>\\n")
                .append("                    </archive>\\n")
                .append("                </configuration>\\n")
                .append("            </plugin>\\n")
                .append("        </plugins>\\n")
                .append("    </build>\\n")
                .append("</project>\\n");
        return pom.toString();
    }
}