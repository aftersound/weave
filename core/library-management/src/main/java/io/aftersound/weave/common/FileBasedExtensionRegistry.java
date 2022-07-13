package io.aftersound.weave.common;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

public class FileBasedExtensionRegistry implements ExtensionRegistry {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final String directory;
    private final String file;

    private final Object lock = new Object();

    private transient List<ExtensionInfo> extensionInfoList;

    private transient Map<Integer, ExtensionInfo> lookup;

    public FileBasedExtensionRegistry(String directory, String file) {
        Path dir = Paths.get(directory);
        Path f = Paths.get(directory, file);
        assert Files.isDirectory(dir) && Files.exists(f) && Files.isRegularFile(f);

        this.directory = directory;
        this.file = file;

        List<ExtensionInfo> extensionInfoList = readExtensionInfos(f);

        this.extensionInfoList = extensionInfoList;
        this.lookup = buildLookup(extensionInfoList);
    }

    @Override
    public void register(ExtensionInfo extensionInfo) {
        synchronized (lock) {
            Path filePath = Paths.get(directory, file);
            try {
                Files.copy(
                        filePath,
                        Paths.get(directory, file + "." + System.currentTimeMillis()),
                        StandardCopyOption.REPLACE_EXISTING);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            Map<Integer, ExtensionInfo> byKey = new HashMap<>(lookup);
            byKey.put(getKey(extensionInfo), extensionInfo);
            List<ExtensionInfo> extensionInfoList = new LinkedList<>(byKey.values());
            Collections.sort(extensionInfoList, ExtensionInfoComparator.INSTANCE);

            try {
                byte[] bytes = MAPPER.writeValueAsBytes(extensionInfoList);
                Files.write(filePath, bytes);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            this.extensionInfoList = Collections.unmodifiableList(extensionInfoList);
            this.lookup = buildLookup(extensionInfoList);
        }
    }

    @Override
    public List<ExtensionInfo> list() {
        List<ExtensionInfo> extensionInfoList = new LinkedList<>(lookup.values());
        Collections.sort(extensionInfoList, ExtensionInfoComparator.INSTANCE);
        return extensionInfoList;
    }

    @Override
    public ExtensionInfo get(String group, String name, String version) {
        return lookup.get(getKey(group, name, version));
    }

    @Override
    public List<ExtensionInfo> get(String group, String name) {
        assert group != null : "group is null";
        assert name != null : "name is null";

        List<ExtensionInfo> target = new LinkedList<>();
        for (ExtensionInfo ei : extensionInfoList) {
            if (group.equals(ei.getGroup()) && name.equals(ei.getName())) {
                target.add(ei);
            }
        }
        return target;
    }

    @Override
    public List<ExtensionInfo> get(String group) {
        assert group != null : "group is null";

        List<ExtensionInfo> target = new LinkedList<>();
        for (ExtensionInfo ei : extensionInfoList) {
            if (group.equals(ei.getGroup())) {
                target.add(ei);
            }
        }
        return target;
    }

    private static int getKey(ExtensionInfo ei) {
        return getKey(ei.getGroup(), ei.getName(), ei.getVersion());
    }

    private static int getKey(String group, String name, String version) {
        return (group + ":" + name + ":" + version).hashCode();
    }

    private static List<ExtensionInfo> readExtensionInfos(Path file) {
        try (InputStream is = new FileInputStream(file.toFile())) {
            List<ExtensionInfoImpl> extensionInfoImplList = MAPPER.readValue(is, new TypeReference<List<ExtensionInfoImpl>>() {});

            List<ExtensionInfo> extensionInfoList = new LinkedList<>();
            for (ExtensionInfoImpl eii : extensionInfoImplList) {
                extensionInfoList.add(eii);
            }
            Collections.sort(extensionInfoList, ExtensionInfoComparator.INSTANCE);

            return Collections.unmodifiableList(extensionInfoList);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static Map<Integer, ExtensionInfo> buildLookup(List<ExtensionInfo> extensionInfoList) {
        Map<Integer, ExtensionInfo> lookup = new HashMap<>();
        for (ExtensionInfo ei : extensionInfoList) {
            int key = getKey(ei);
            lookup.put(key, ei);
        }
        return Collections.unmodifiableMap(lookup);
    }

    private static class ExtensionInfoComparator implements Comparator<ExtensionInfo> {

        public static final ExtensionInfoComparator INSTANCE = new ExtensionInfoComparator();

        @Override
        public int compare(ExtensionInfo o1, ExtensionInfo o2) {
            return id(o1).compareTo(id(o2));
        }

        private String id(ExtensionInfo ei) {
            return ei.getGroup() + ":" + ei.getName() + ":" + ei.getVersion() + ":" + ei.getBaseType() + ":" + ei.getType();
        }

    }

}
