package wtf.metio.yosql.generator.blocks.jdbc;

// TODO: remove? JdbcNamesConfiguration exists as well
public interface JdbcNames {

    String dataSource();
    String connection();
    String statement();
    String metaData();
    String resultSet();
    String columnCount();
    String columnLabel();
    String batch();
    String list();
    String jdbcIndex();
    String index();
    String row();

}