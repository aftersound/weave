package io.aftersound.weave.zk;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Stat;

import java.util.ArrayList;
import java.util.List;

public class ZKNode {

    protected final ZooKeeper zk;
    protected final String name;
    protected final String path;

    protected Stat stat;

    ZKNode(ZooKeeper zk, String name, String path) {
        this.zk = zk;
        this.name = name;
        this.path = path;
    }

    public String name() {
        return name;
    }

    public String path() {
        return path;
    }

    public <N extends ZKNode> N checkExistence() throws Exception {
        this.stat = zk.exists(path, false);
        return (N)this;
    }

    public boolean exists() {
        return stat != null;
    }

    public void create(byte[] data, List<ACL> acls, CreateMode createMode) throws Exception {
        zk.create(path, data, acls, createMode);
    }

    public void create(List<ACL> acls, CreateMode createMode) throws Exception {
        zk.create(path, null, acls, createMode);
    }

    public void delete() throws Exception {
        zk.delete(path, stat.getVersion());
    }

    public byte[] getData() throws Exception {
        return zk.getData(path, false, null);
    }

    public ZKNode childNode(String childName) {
        return new ZKNode(zk, name, path + "/" + childName);
    }

    public List<ZKNode> childrenNodes() throws Exception {
        List<ZKNode> childrenNodes = new ArrayList<>();
        List<String> children = zk.getChildren(path, false);
        for (String child : children) {
            childrenNodes.add(childNode(child));
        }
        return childrenNodes;
    }

    public List<String> children() throws Exception {
        return zk.getChildren(path, false);
    }



}
