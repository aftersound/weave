package io.aftersound.weave.common;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.NameFileFilter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.*;

public class LibraryManager {

    private final File localMavenRepository;
    private final int baseIndex;

    public LibraryManager(String localMavenRepository) {
        File localMavenRepo = new File(localMavenRepository);
        assert localMavenRepo.exists() && localMavenRepo.isDirectory() : (localMavenRepository + " is not a directory");
        this.localMavenRepository = localMavenRepo;
        this.baseIndex = localMavenRepository.split("\\/").length;
    }

    public void findAndExec(String jarNameListFile, Action action) throws Exception {
        final List<String> jarNames = new ArrayList<>();
        try (FileReader fr = new FileReader(jarNameListFile)) {
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

        Collections.sort(jarNames, (o1, o2) -> o1.compareTo(o2));

        action.setJarNameList(Collections.unmodifiableList(jarNames));

        Collection<File> files = FileUtils.listFiles(
                localMavenRepository,
                new NameFileFilter(jarNames.toArray(new String[jarNames.size()])),
                DirectoryFileFilter.INSTANCE
        );

        Collections.sort((List<File>) files, (o1, o2) -> o1.toString().compareTo(o2.toString()));

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

            Map<String, String> libInfo = new HashMap<>(4);
            libInfo.put("groupId", groupId);
            libInfo.put("artifactId", artifactId);
            libInfo.put("version", version);
            libInfo.put("artifactFileName", artifactFileName);

            action.act(file, libInfo);
        }
    }

    public static abstract class Action {

        private List<String> jarNameList;

        protected void setJarNameList(List<String> jarNameList) {
            this.jarNameList = jarNameList;
        }

        public List<String> getJarNameList() {
            return jarNameList;
        }

        public abstract void act(File file, Map<String, String> libraryInfo) throws Exception;
    }

    public static final class CompositeAction extends Action {

        private final Action[] actions;

        public CompositeAction(Action... actions) {
            this.actions = actions;
        }

        @Override
        protected void setJarNameList(List<String> jarNameList) {
            super.setJarNameList(jarNameList);
            for (Action action : actions) {
                action.setJarNameList(jarNameList);
            }
        }

        @Override
        public void act(File file, Map<String, String> libraryInfo) throws Exception {
            for (Action action : actions) {
                action.act(file, libraryInfo);
            }
        }
    }

}
