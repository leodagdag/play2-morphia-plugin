package leodagdag.play2morphia;

import com.google.code.morphia.annotations.Transient;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSInputFile;
import org.bson.types.ObjectId;
import play.Logger;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

public class Blob {

    @Transient
    private GridFSDBFile gridFSDBFile;

    public Blob() {
    }

    public Blob(InputStream is, String type) {
        this();
        set(is, type);
    }

    public Blob(File inputFile, String type) {
        this();
        try {
            set(inputFile, type);
        } catch (IOException e) {
            Logger.error(String.format("File not found: %s (%s)", inputFile.getAbsolutePath(), e.getMessage()));
        }
    }

    public Blob(String id) {
        DBObject queryObj = new BasicDBObject("name", id);
        gridFSDBFile = MorphiaPlugin.gridFs().findOne(queryObj);
    }

    void set(File file, String type) throws IOException {
        if (!file.exists()) {
            Logger.warn(String.format("File not exists: %s", file));
            return;
        }
        GridFSInputFile inputFile = MorphiaPlugin.gridFs().createFile(file);
        inputFile.setContentType(type);
        inputFile.save();
        this.gridFSDBFile = MorphiaPlugin.gridFs().findOne(new ObjectId(inputFile.getId().toString()));
    }

    void set(InputStream is, String type) {
        GridFSInputFile inputFile = MorphiaPlugin.gridFs().createFile(is);
        inputFile.setContentType(type);
        inputFile.put("name", UUID.randomUUID().toString());
        inputFile.save();
        gridFSDBFile = MorphiaPlugin.gridFs().findOne(new ObjectId(inputFile.getId().toString()));
    }

    public long length() {
        return gridFSDBFile == null ? 0 : gridFSDBFile.getLength();
    }

    public String type() {
        return gridFSDBFile.getContentType();
    }

    public boolean exists() {
        return gridFSDBFile != null && gridFSDBFile.getId() != null;
    }

    public GridFSDBFile getGridFSFile() {
        return gridFSDBFile;
    }

    public static void delete(String name) {
        MorphiaPlugin.gridFs().remove(new BasicDBObject("name", name));
    }
}
