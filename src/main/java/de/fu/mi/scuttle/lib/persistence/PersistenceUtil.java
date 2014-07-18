package de.fu.mi.scuttle.lib.persistence;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Table;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Metamodel;

import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.tools.schemaframework.SchemaManager;

/**
 * Utility methods for working with a JPA database.
 * 
 * @author Julian Fleischer
 * @since 1.0
 */
public class PersistenceUtil {

    /**
     * Uses the given EntityManager and creates all tables defined in the domain
     * package.
     * <p>
     * The current implementation of this method depends on EclipseLink.
     * </p>
     * 
     * @since 1.0
     * @param em
     *            The EntityManager used for creating the tables.
     */
    public static void createTables(final EntityManager em) {
        final DatabaseSession ds = em.unwrap(DatabaseSession.class);
        final SchemaManager sm = new SchemaManager(ds);

        sm.createDefaultTables(true);
    }

    public static void createTables(final String persistenceUnit,
            final String jdbcDriver,
            final String jdbcUser, final String jdbcPassword,
            final String jdbcUrl) {

        final Properties properties = PersistenceUtil.getProperties(jdbcDriver,
                jdbcUser, jdbcPassword, jdbcUrl);

        final EntityManagerFactory emf = Persistence
                .createEntityManagerFactory(
                        persistenceUnit, properties);
        final EntityManager em = EntityManagerWrapper.wrap(emf
                .createEntityManager());

        PersistenceUtil.createTables(em);

        em.close();
        emf.close();
    }

    public static List<String> getTables(final EntityManager em)
            throws SQLException {
        return getTablesLike(em, "%");
    }

    @SuppressWarnings("resource")
    public static List<String> getTablesLike(final EntityManager em,
            final String like)
            throws SQLException {
        final List<String> tables = new ArrayList<String>();

        final Connection c = em.unwrap(Connection.class);
        final DatabaseMetaData meta = c.getMetaData();
        try (final ResultSet result = meta
                .getTables(null, null, like, null)) {
            while (result.next()) {
                tables.add(result.getString(3));
            }
        }

        return tables;
    }

    public static void dropTablesStartingWith(final EntityManager em,
            final String prefix, final String dropCommand, final String before,
            final String after) throws SQLException {

        em.getTransaction().begin();

        if (before != null) {
            em.createNativeQuery(before).executeUpdate();
        }

        final List<String> tables = getTablesLike(em, prefix + "%");

        for (final String table : tables) {
            final String query = String.format(dropCommand, table);
            em.createNativeQuery(query).executeUpdate();
        }

        if (after != null) {
            em.createNativeQuery(after).executeUpdate();
        }

        em.getTransaction().commit();
    }

    /**
     * Retrieves a listing of all the tables in the database.
     * <p>
     * The current implementation of this method is independent of a particular
     * JPA implementation.
     * </p>
     * 
     * @since 1.0
     * @param em
     *            The EntityManager used to discover the database.
     * @return A List of table names in the selected database.
     */
    public static List<String> getSchemaTableNames(final EntityManager em) {
        final Metamodel meta = em.getMetamodel();
        final Set<EntityType<?>> entityTypes = meta.getEntities();
        final List<String> tableNames = new ArrayList<String>(
                entityTypes.size());
        for (final EntityType<?> entityType : entityTypes) {
            tableNames.add(entityType.getJavaType().getAnnotation(Table.class)
                    .name());
        }
        Collections.sort(tableNames);
        return tableNames;
    }

    /**
     * Retrieves a listing of all java types in the schema.
     * <p>
     * The current implementation of this method is independent of a particular
     * JPA implementation.
     * </p>
     * 
     * @since 1.0
     * @param em
     *            The EntityManager used to discover the database.
     * @return A List of class objects describing the entity POJOs.
     */
    public static List<Class<?>> getSchemaClasses(final EntityManager em) {
        final Metamodel meta = em.getMetamodel();
        final Set<EntityType<?>> entityTypes = meta.getEntities();
        final List<Class<?>> entityClasses = new ArrayList<Class<?>>(
                entityTypes.size());
        for (final EntityType<?> entityType : entityTypes) {
            entityClasses.add(entityType.getJavaType());
            entityType.getDeclaredSingularAttributes();
        }
        Collections.sort(entityClasses, new Comparator<Class<?>>() {
            @Override
            public int compare(final Class<?> left, final Class<?> right) {
                return left.getCanonicalName().compareTo(
                        right.getCanonicalName());
            }
        });
        return entityClasses;
    }

    public static Properties getProperties(final String jdbcDriver,
            final String jdbcUser,
            final String jdbcPassword, final String jdbcUrl) {

        final Properties properties = new Properties();
        properties.setProperty("javax.persistence.jdbc.driver", jdbcDriver);
        properties.setProperty("javax.persistence.jdbc.user", jdbcUser);
        properties.setProperty("javax.persistence.jdbc.password", jdbcPassword);
        properties.setProperty("javax.persistence.jdbc.url", jdbcUrl);

        return properties;
    }
}
