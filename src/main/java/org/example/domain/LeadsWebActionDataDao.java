package org.example.domain;


import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.ebean.EbeanServer;
import io.ebean.EbeanServerFactory;
import io.ebean.Query;
import io.ebean.config.ServerConfig;
import java.util.List;

/**

 *
 * @author magicliang
 *
 */

public class LeadsWebActionDataDao {
  private static final String RAW_SQL = "select\n"
          + "          effective_action_type,\n"
          + "          CAST(min_action_time2 AS DATE) as effective_action_time,\n"
          + "           count(*) as action_count\n\n"
          + "     from\n"
          + "     (\n"
          + "     select\n"
          + "     effective_action_type,\n"
          + "     sign_id,\n"
          + "     min(action_time2) as min_action_time2\n"
          + "     from\n"
          + "     (\n"
          + "     select\n"
          + "     sign_id,\n"
            + "     action_time2,\n"
            + "     CASE\n"
            + "     WHEN final_action_type = 'wx_1166' THEN '405'\n"
            + "     ELSE final_action_type\n"
            + "     END AS effective_action_type\n"
            + "     from\n"
            + "     leads_web_action_data\n"
            + "     where\n"
            + "     action_time2 between :actionTimeEnd\n"
            + "     and :actionTimeEnd\n"
            + "     and all_id = :all_id\n"
            + "     AND (\n"
            + "     OR og in (\n"
            + "     6,\n"
            + "     104,\n"
            + "     105,\n"
            + "     427,\n"
            + "     204,\n"
            + "     108,\n"
            + "     302,\n"
            + "     10000,\n"
            + "     402,\n"
            + "     403,\n"
            + "     405,\n"
            + "     502,\n"
            + "     504,\n"
            + "     409,\n"
            + "     316,\n"
            + "     412\n"
            + "     )\n"
            + "     OR og is null\n"
            + "     )\n"
            + "     order by\n"
            + "     action_time2 desc\n"
            + "     )\n"
            + "     group by\n"
            + "     effective_action_type,\n"
            + "     sign_id\n"
            + "     )\n"
            + "     group by\n"
            + "     effective_action_type,\n"
            + "     CAST(min_action_time2 AS DATE)\n"
            + "     </code>";


    /**
     * issue 1:
     *
     * this query will return [{},{},{},{},{},{},{},{},{},{},{},{},{},{},{},{},{},{},{},{},{},{},{},{},{},{},{},{},{}
     * ,{},{},{},{},{},{},{}]
     * condition to reproduce the empty records:
     *
     * for column action_count, we can use actionCount as member field, but if we want another name for business
     * purpose for examle "count", instead of actionCount as conventional name:
     * <code>
     *   @Column(name = "action_count")
     *   private Integer count;
     * </code>
     *
     * the column mapping will not work, and the whole result will be empty, that means column mapping annotation is
     * useless.
     *
     * @return list with empty records
     */
    public List<LeadsWebActionDataCountAggregate1> findNative() {
        Query<LeadsWebActionDataCountAggregate1> basicQuery =
                getServer().findNative(LeadsWebActionDataCountAggregate1.class,
                                RAW_SQL)
                        .setParameter("all_id", 12345)
                        .setParameter("actionTimeBegin", "2022-11-11 00:00:00")
                        .setParameter("actionTimeEnd", "2022-11-11 23:59:59");
        return basicQuery.findList();
    }

    /**
     * issue2:
     * as we can see the raw sql, the result of SQL is:
     *
     * effective_action_type, effective_action_time, action_count
     *
     * They can be mapped to java type: String, Date, Integer
     *
     * If we declare our instance member like this(the order is Integer):
     * <code>
     *
     * @return an exception will be thrown, there will be no result
     * @Column(name = "effective_action_time")
     *         *     private Date effectiveActionTime;
     * @Column(name = "effective_action_type")
     *         *     private String effectiveActionType;
     * @Column(name = "action_count")
     *         private Integer actionCount;
     *         </code>
     *
     *         We declare effectiveActionTime before effectiveActionType, we want the ebean to map column result of
     *         effective_action_time to effectiveActionTime, and we get an exception here:
     *
     *         javax.persistence.PersistenceException: Query threw SQLException:Text '204' could not be parsed at index
     *         0
     *         Query was: select
     *
     *         the 204 is result for column effective_action_type, as the first column result. ebean tries to map it
     *         to first instance member because it is first column in result set, ignoring that the mapped variable
     *         for effective_action_type accoriding to mapping is the second member variable.
     *
     *         So the ebean actual behavior is, when we use findDto api, the sequence of member is important, the
     *         mapping annotation is useless.
     */
    public List<LeadsWebActionDataCountAggregate2> findDto() {
        return getServer().findDto(LeadsWebActionDataCountAggregate2.class, RAW_SQL)
                .setParameter("all_id", 12345)
                .setParameter("actionTimeBegin", "2022-11-11 00:00:00")
                .setParameter("actionTimeEnd", "2022-11-11 23:59:59")
                .findList();
    }

    static public synchronized EbeanServer getServer() {
        HikariConfig config = new HikariConfig();
        config.setDriverClassName("com.clickhouse.jdbc.ClickHouseDriver");
        config.setJdbcUrl("jdbc:clickhouse://localhost:8888/myclickhouse-db");
        config.setUsername("myclickhouse.username");
        config.setPassword("myclickhouse.passwd");
        config.setConnectionTimeout(30000L);
        config.setMinimumIdle(5);
        config.setIdleTimeout(30000L);
        config.setMaximumPoolSize(50);
        config.setMaxLifetime(180000L);
        config.setValidationTimeout(5000L);
        config.setConnectionTestQuery("select 1");
        config.setRegisterMbeans(true);
        config.setConnectionInitSql(null);
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");


        ServerConfig serverConfig = new ServerConfig();
        serverConfig.setName("clickhouseserver");
        serverConfig.setDataSource(new HikariDataSource(config));
        serverConfig.setRegister(true);
        serverConfig.setDefaultServer(false);
        serverConfig.addPackage("domain");
        EbeanServer ebeanServer = EbeanServerFactory.create(serverConfig);
        return ebeanServer;
    }
}
