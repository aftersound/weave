package io.aftersound.weave.common;

import org.apache.commons.cli.*;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.NameFileFilter;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.StringJoiner;

public class LibraryTool {

    private final File localMavenRepository;
    private final int baseIndex;

    public LibraryTool(String localMavenRepository) {
        File localMavenRepo = new File(localMavenRepository);
        assert localMavenRepo.exists() && localMavenRepo.isDirectory() : (localMavenRepository + " is not a directory");
        this.localMavenRepository = localMavenRepo;
        this.baseIndex = localMavenRepository.split("\\/").length;
    }

    public void install(String listFile, String targetDirectory) throws Exception {
        File targetLocation = new File(targetDirectory);
        assert targetLocation.exists() && targetLocation.isDirectory() : (targetDirectory + " is not a directory");

        final List<String> jarNames = new ArrayList<>();
        try (FileReader fr = new FileReader(listFile)) {
            BufferedReader bufferedReader = new BufferedReader(fr);
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                String jarName = line.trim();
                if (!jarName.isEmpty() && !jarName.startsWith("#")) {
                    jarNames.add(jarName);
                }
            }
        } catch (Exception e) {
        }

        Collection<File> files = FileUtils.listFiles(
                localMavenRepository,
                new NameFileFilter(jarNames.toArray(new String[jarNames.size()])),
                DirectoryFileFilter.INSTANCE
        );

        for (File file : files) {
            String[] strArray = file.toString().split("\\/");
            final int length = strArray.length;

            StringJoiner groupIdBuilder = new StringJoiner(".");
            for (int i = baseIndex; i <= length - 4; i++) {
                groupIdBuilder.add(strArray[i]);
            }
            final String groupId = groupIdBuilder.toString();
            final String version = strArray[length - 2];
            final String artifactId = strArray[length - 3];
            final String artifactFileName = strArray[length - 1];

            final String targetFileName = groupId + "#" + artifactId + "#" + version + "#" + artifactFileName;
            final File targetFile = new File(targetLocation.getPath(), targetFileName);
            FileUtils.copyFile(file, targetFile);
        }

    }

    public static void main(String[] args) {
        final Options options = new Options();
        options.addOption(new Option("l", "jar-name-list-file", true, "the name of file contains jar names"));
        options.addOption(new Option("t", "target-directory", true, "target directory"));
        options.addOption(new Option("m", "maven-local-repository", false, "Maven local repository"));

        // create the parser
        CommandLineParser parser = new DefaultParser();
        try {
            // parse the command line arguments
            CommandLine line = parser.parse(options, args);

            final String localMavenRepository;
            String o = line.getOptionValue('m');
            localMavenRepository = o != null ? o : (System.getProperty("user.home") + "/.m2/repository/");

            final String jarNameListFile = line.getOptionValue('l');
            final String targetDirectory = line.getOptionValue('t');

            new LibraryTool(localMavenRepository).install(jarNameListFile, targetDirectory);
        }
        catch (ParseException e) {
            // oops, something went wrong
            System.err.println("Parsing failed.  Reason: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Parsing failed.  Reason: " + e.getMessage());
        }
    }

}
