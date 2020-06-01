package ru.job4j.store;

import org.apache.commons.dbcp2.BasicDataSource;
import ru.job4j.model.Account;
import ru.job4j.model.Hall;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class DbStore implements IStore {

    /**
     * database connection pooling
     */
    private static final BasicDataSource pool = new BasicDataSource();


    private static final class Lazy {
        private static final IStore INST = new DbStore();
    }

    public static IStore instOf() {
        return Lazy.INST;
    }

    /**
     * configure the database from db.properties
     * setMinIdle - set minimum number of connections
     * setMaxIdle - set maximum number of connections
     */
    public DbStore() {
        Properties cfg = new Properties();
        try (BufferedReader io = new BufferedReader(
                new FileReader("db.properties")
        )) {
            cfg.load(io);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
        try {
            Class.forName(cfg.getProperty("jdbc.driver"));
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
        pool.setDriverClassName(cfg.getProperty("jdbc.driver"));
        pool.setUrl(cfg.getProperty("jdbc.url"));
        pool.setUsername(cfg.getProperty("jdbc.username"));
        pool.setPassword(cfg.getProperty("jdbc.password"));
        pool.setMinIdle(5);
        pool.setMaxIdle(10);
        pool.setMaxOpenPreparedStatements(100);
        createTables();
    }

    /**
     * Create tables Hall & Account
     */
    private void createTables() {
        try (BufferedReader br = new BufferedReader(
                new FileReader("db" + File.separator + "schema.sql"))) {
            StringBuilder query = new StringBuilder();
            String line;
            try (Connection cn = pool.getConnection();
                 Statement st = cn.createStatement()) {
                while ((line = br.readLine()) != null) {
                    query.append(line);
                }
                st.execute(new String(query));
            }
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
    }

    @Override
    public List<Hall> places() {
        List<Hall> places = new ArrayList<>();
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement("SELECT * FROM hall")) {
            try (ResultSet it = ps.executeQuery()) {
                while (it.next()) {
                    places.add(new Hall(
                                    it.getInt("row"),
                                    it.getInt("place"),
                                    it.getInt("price"),
                                    it.getBoolean("free")
                            )
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return places;
    }

    /**
     * Производим оплату, добаляем в таблицу Account данные.
     * Делаем это атомарно, пhи помощи setAutoCommit = false
     *
     * Конструкция "select ... for update nowait" блокирующая.В этом случае при встрече со строкой,
     * заблокированной другой сессией, текущая сессия не будет ждать, а сразу сгенерирует ошибку
     * и сделает rollback.
     *
     * @param hall    места
     * @param account аккаунт, оплативший билет
     * @return результат операции
     */
    @Override
    public boolean makePayment(Hall hall, Account account) {
        boolean res = false;
        String selectPlace = "select * from hall where row = ? and place = ? for update nowait";
        String updatePlace = "update hall set free = true WHERE id = ?";
        try (Connection cn = pool.getConnection()) {
            cn.setAutoCommit(false);
            try (PreparedStatement sP = cn.prepareStatement(selectPlace)) {
                sP.setInt(1, hall.getRow());
                sP.setInt(2, hall.getPlace());
                ResultSet rs = sP.executeQuery();
                while (rs.next()) {
                    int id = rs.getInt(1);
                    try (PreparedStatement uP = cn.prepareStatement(updatePlace)) {
                        uP.setInt(1, id);
                        if (uP.executeUpdate() == 1) {
                            res = true;
                            addAccount(cn, account, id);
                            cn.commit();
                        }
                        else {
                            cn.rollback();
                        }
                    }
                }
            } catch (SQLException e) {
                cn.rollback();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

    private void addAccount(Connection conn, Account account, Integer id) {
        String addAcc = "insert into account (name, telnumber, hall_id) values (?, ?, ?)";
        try (PreparedStatement aA = conn.prepareStatement(addAcc)) {
            aA.setString(1, account.getName());
            aA.setString(2, account.getTelNumber());
            aA.setInt(3, id);
            aA.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Hall getPlace(Hall hall) {
        Hall res;
        int row = 0;
        int place = 0;
        int price = 0;
        boolean free = false;
        String query = "select * from hall where row = ? and place = ?";
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement(query)) {
            ps.setInt(1, hall.getRow());
            ps.setInt(2, hall.getPlace());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                row = rs.getInt("row");
                place = rs.getInt("place");
                price = rs.getInt("price");
                free = rs.getBoolean("free");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        res = new Hall(row, place, price, free);
        return res;
    }
}
