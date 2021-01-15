package test.timescale;

import com.datastax.oss.driver.api.core.cql.Row;
import test.Math.SinFonction;

import java.sql.*;
import java.time.Duration;

import static com.datastax.oss.driver.internal.core.time.Clock.LOG;

public class TimeSDBRead {
    public static void main(String[] args) {
        SinFonction sinFonction = new SinFonction(0d, 20d, 0d, Duration.ofHours(12).getSeconds(), 0d);

        try (Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost/scms_test", "sartor", "Tho2vo3+")) {
            // "0" means disabling the timeout, when doing isValid checks
            boolean isValid = conn.isValid(0);
            System.out.println("Do we have a valid db connection? = " + isValid);
            if (!isValid) {
                throw new RuntimeException("Not connected");
            }

            long start = System.currentTimeMillis();
            LOG.info("Before the execute Query");
            Statement stmt = conn.createStatement();
            LOG.info("After the execute Query");
            ResultSet rs = stmt.executeQuery("SELECT ilabel, ts, val FROM sensor");
            int nbSecADay = 24 * 3600;
            int nbReaded = 0;
            rs.next();
            nbReaded++;
            LOG.info("End of the fetch");
            while (rs.next()) {
                nbReaded++;
                if (nbReaded % nbSecADay == 0) {
                    LOG.info("Readed {} day", nbReaded / nbSecADay);
                }
            }
            long end = System.currentTimeMillis();
            LOG.info("Readed {} elements", nbReaded);
            LOG.info("Readed in {}", Duration.ofMillis(end - start));

        } catch (Exception throwables) {
            throwables.printStackTrace();
        }
    }
}
