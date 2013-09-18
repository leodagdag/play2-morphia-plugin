package leodagdag.play2morphia.utils;

public enum ConfigKey {
    DB_MONGOURI("db.mongoURI"), /**/
    DB_SEEDS("db.seeds"), /**/
    DB_HOST("db.host"), /**/
    DB_PORT("db.port"), /**/
    DB_NAME("db.name"), /**/
    DB_USERNAME("db.username"), /**/
    DB_PASSWORD("db.password"), /**/
    ID_TYPE("id.type"), /**/
    DEFAULT_WRITE_CONCERN("defaultWriteConcern"), /**/
    COLLECTION_UPLOADS("collection.upload"), /**/
    LOGGER("logger");

    public static final String PREFIX = "morphia";

    private final String key;

    private ConfigKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

}
