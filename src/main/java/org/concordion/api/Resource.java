package org.concordion.api;

import org.concordion.internal.util.Check;

public final class Resource {

    private final String path;
    private final String[] parts;
    private final String name;
    private final boolean isPackage;

    public Resource(String path) {
        path = path.replaceAll("\\\\", "/");
        Check.isTrue(path.startsWith("/"), "Internal error: Resource path should start with a slash");
        this.path = path;
        isPackage = endsWithSlash(path);
        parts = path.split("/");
        if (parts.length == 0) {
            name = "";
        } else {
            name = parts[parts.length - 1];
        }
    }

    public String getPath() {
        return path;
    }

    private boolean isPackage() {
        return isPackage;
    }

    private boolean endsWithSlash(String s) {
        return s.endsWith("/");
    }

    private Resource getPackage() {
        if (isPackage()) {
            return this;
        }
        return getParent();
    }

    public Resource getParent() {
        if (getPath().equals("/")) {
            return null;
        }
        String parentPath = "/";
        for (int i = 1; i < parts.length - 1; i++) {
            parentPath += parts[i] + "/";
        }
        return new Resource(parentPath);
    }

    public String getRelativePath(Resource resource) {

        if (resource.getPath().equals(path)) {
            return name;
        }

        // Find common stem and ignore it
        // Use ../ to move up the path from here to common stem
        // Append the rest of the path from resource
        String[] therePieces = resource.getPathPieces();
        String[] herePieces = getPathPieces();

        int sharedPiecesCount = 0;
        for (int i = 0; i < herePieces.length; i++) {
            if (therePieces.length <= i) {
                break;
            }
            if (therePieces[i].equals(herePieces[i])) {
                sharedPiecesCount++;
            } else {
                break;
            }
        }

        String r = "";

        for (int i = sharedPiecesCount; i < herePieces.length; i++) {
            r += "../";
        }

        for (int i = sharedPiecesCount; i < therePieces.length; i++) {
            r += therePieces[i] + "/";
        }

        if (resource.isPackage()) {
            return r;
        }
        return r + resource.getName();
    }

    private String[] getPathPieces() {
        String packagePath = getPackage().getPath();
        if ("/".equals(packagePath)) {
            return new String[]{""};
        }
        return packagePath.split("/");
    }

    public String getName() {
        return name;
    }

    public Resource getRelativeResource(String relativePath) {
        Check.isFalse(relativePath.startsWith("/"), "Relative path should not start with a slash");

        String subPath = removeAnyLeadingDotSlashFrom(relativePath);

        Resource p = getPackage();
        while (subPath.startsWith("../")) {
            p = p.getParent();
            if (p == null) {
                throw new RuntimeException("Path '" + relativePath + "' relative to '" + getPath() + "' "
                        + "evaluates above the root package.");
            }
            subPath = subPath.replaceFirst("../", "");
        }

        Check.isFalse(subPath.contains("../"),
                "The ../ operator is currently only supported at the start of expressions");

        return new Resource(p.getPath() + subPath);
    }

    private String removeAnyLeadingDotSlashFrom(String subPath) {
        return subPath.replaceFirst("^\\./", "");
    }

    @Override
    public String toString() {
        return "[Resource: " + path + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((path == null) ? 0 : path.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Resource other = (Resource) obj;
        if (path == null) {
            if (other.path != null) {
                return false;
            }
        } else if (!path.equals(other.path)) {
            return false;
        }
        return true;
    }
}
