package org.neo4j.imports;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.helpers.collection.IteratorUtil;
import org.neo4j.io.fs.FileUtils;
import org.neo4j.tooling.GlobalGraphOperations;

import java.io.File;

/**
 * @author mh
 * @since 01.03.15
 * jdbc: http://dev.mysql.com/doc/connector-j/en/connector-j-reference-configuration-properties.html
 * Source: https://dev.mysql.com/doc/employee/en/employees-installation.html
 * Structure: https://dev.mysql.com/doc/employee/en/sakila-structure.html
 * Download: http://dev.mysql.com/doc/index-other.html
 */
public class DataImporterEmployeeTest {

    public static final String STORE_DIR = "target/employee.db";

    public static void main(String[] args) throws Exception {
        FileUtils.deleteRecursively(new File(STORE_DIR));
        long time = System.currentTimeMillis();
        Rules rules = new Rules(); // asList("titles")
        new DatabaseImporter("jdbc:mysql://localhost:3306/employees?user=root", "employees",STORE_DIR).run(rules);

        long delta = (System.currentTimeMillis() - time) / 1000;
        String result = importInfo();
        System.out.println(result+ " in "+ delta + " seconds");
    }

    private static String importInfo() {
        GraphDatabaseService db = new GraphDatabaseFactory().newEmbeddedDatabase(STORE_DIR);

        try (Transaction tx = db.beginTx()) {
            int nodes = IteratorUtil.count(db.getAllNodes());
            int rels = IteratorUtil.count(GlobalGraphOperations.at(db).getAllRelationships());
            return "Imported nodes "+nodes+" rels "+rels;
        } finally {
            db.shutdown();
        }
    }
}
