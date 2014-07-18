package de.fu.mi.scuttle.lib;

/**
 * Static configuration.
 * 
 * @author Julian Fleischer
 * @since 2013-08-15
 */
public class DbConfig {

    /**
     * The start page which {@link ScuttleIndexHtml} will redirect to if no
     * specific target is given.
     */
    public static final String FIRST_PAGE = "/kvv:index";

    /**
     * A unique identifier for the version of the db schema. This information is
     * also stored in the database. It helps Scuttle to find out whether it
     * needs to be reinstalled or not.
     */
    public static final String DB_VERSION = "2013-11-24";

    /**
     * The prefix for tables in the database.
     */
    public static final String TABLE_PREFIX = "mvs_scuttle_";

    /**
     * The prefix for join tables in the database.
     */
    public static final String JOIN_TABLE_PREFIX = "msv_scuttle_";

    /**
     * The name of id columns in the database.
     */
    public static final String ID_COLUMN = "k_id";

    /**
     * The name of uuid columns in the database.
     */
    public static final String UUID_COLUMN = "k_uuid";

    /**
     * The name for the creation time column in the database.
     */
    public static final String CREATION_TIME_COLUMN = "k_creation_time";

    /**
     * The name for the last modified time in the database.
     */
    public static final String LAST_MODIFICATION_TIME_COLUMN = "k_last_modification_time";

    /**
     * The prefix for ordinary columns.
     */
    public static final String COLUMN_PREFIX = "c_";

    /**
     * The prefix for foreign key columns.
     */
    public static final String FOREIGN_KEY_PREFIX = "f_";

    /**
     * The length of ordinary names.
     */
    public static final int NAME_LENGTH = 200;

    /**
     * 
     */
    public static final int FEATURE_NAME_LENGTH = 30;

    /**
     * The length of strings which uniquely identify something.
     */
    public static final int STRING_ID_LENGTH = 40;

    /**
     * The length of arbitrary data fields (4k).
     */
    public static final int DATA_LENGTH = 4096;

    /**
     * The length of the name of a privilege.
     * 
     * See {@link de.fu.mi.scuttle.domain.User#getPrivileges()}.
     */
    public static final int PRIVILEGE_LENGTH = 32;

    /**
     * The length of the name of an action.
     */
    public static final int ACTION_LENGTH = 32;

}
