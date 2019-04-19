package hu.skawa.migrator_maven_plugin.model;

import com.google.common.base.CharMatcher;

public class InternalDependency {
    private String groupId;
    private String artifactId;
    private String version;
    private String bazelName;
    private String bazelArtifact;
    private String jarServer;
    private String pomServer;

    private String hash;

    public InternalDependency() {
    }

    public InternalDependency(String groupId, String artifactId, String version, String hash) {
        super();
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version;
        this.hash = hash;
    }

    public String getArtifactId() {
        return artifactId;
    }

    public String getBazelArtifact() {
        return bazelArtifact;
    }

    public String getBazelName() {
        return sanitize(this.groupId + "." + this.artifactId);
    }

    public String getGroupId() {
        return groupId;
    }

    public String getHash() {
        return hash;
    }

    public String getPomServer() {
        return pomServer;
    }

    public String getJarServer() {
        return jarServer;
    }

    public String getVersion() {
        return version;
    }

    public void setArtifactId(String artifactId) {
        this.artifactId = artifactId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public void setPomServer(String pomServer) {
        this.pomServer = pomServer;
    }

    public void setJarServer(String sourceServer) {
        this.jarServer = sourceServer;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String toBazelDirective(Boolean addHash, Boolean addServer) {
        StringBuilder sb = new StringBuilder("native.maven_jar(\n");
        sb.append("\tname = \"");

        // sanitize name
        this.bazelName = sanitize(this.groupId + "." + this.artifactId);
        sb.append(this.bazelName);

        sb.append("\",\n\t");
        sb.append("artifact = \"");
        this.bazelArtifact = this.groupId + ":" + this.artifactId + ":" + this.version;
        sb.append(this.bazelArtifact);
        sb.append("\",");

        if (addHash) {
            sb.append("\n\tsha1 = \"");
            sb.append(this.hash);
            sb.append("\",");
        }

        if (addServer) {
            sb.append("\n\tserver = \"");
            sb.append(this.jarServer);
            sb.append("\",");
        }

        sb.append("\n)");
        return sb.toString();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Group: ");
        sb.append(this.groupId);
        sb.append("\n");
        sb.append("Artifact: ");
        sb.append(this.artifactId);
        sb.append("\n");
        sb.append("Version: ");
        sb.append(this.version);
        sb.append("\n");
        return sb.toString();
    }


    public String toBazelReference() {
        return String.format(
            "native.java_library(\n" +
            "        name = \"%s\",\n" +
            "        visibility = [\"//visibility:public\"],\n" +
            "        exports = [\"@%s//jar\"],\n" +
            "    )", this.bazelName, this.bazelName);
    }

    private String sanitize(CharSequence input) {
        return CharMatcher.javaLetterOrDigit().or(CharMatcher.is('_')).negate().replaceFrom(input, "_");
    }
}
