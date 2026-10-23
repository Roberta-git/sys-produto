package org.sysproduto.utils;

import java.nio.file.Paths;

public class PathFXML {
    public static String pathBase() {
        return Paths.get("src/main/java/org/sysproduto/view").toAbsolutePath().toString();
    }
}
