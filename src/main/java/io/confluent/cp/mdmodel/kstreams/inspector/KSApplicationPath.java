package io.confluent.cp.mdmodel.kstreams.inspector;

import java.io.File;

public class KSApplicationPath {

    String queryFileName = null;
    String queryFolder = null;
    String queryBufferFolder = null;

    public void setKSQLFilename(String filename) {
        queryFileName = filename;
    }

    public void setFolder(String folder) {
        queryFolder = folder;
    }

    public File getKSQLFile() {
        return new File( queryFolder + "/" + queryFileName );
    }

    public void setKSQLBufferFolder(String default_queryBufferFolder) {
        queryBufferFolder = default_queryBufferFolder;
    }
}
