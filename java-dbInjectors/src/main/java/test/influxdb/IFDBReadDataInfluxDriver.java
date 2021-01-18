package test.influxdb;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.QueryApi;
import com.influxdb.client.WriteApi;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;
import com.influxdb.query.FluxRecord;
import com.influxdb.query.FluxTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import test.Math.SinFonction;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

public class IFDBReadDataInfluxDriver {
    private static Logger LOG = LoggerFactory.getLogger("LOG");


    public static void main(String[] args) {
        InfluxDBClient client = InfluxDBClientFactory.create(IFDBConst.HOST, IFDBConst.token.toCharArray());
        int nbElems = 10 * 24 * 60 * 60;
        long start = System.currentTimeMillis();

        String query = "" +
                "from(bucket: \"fsa\")\n" +
                "  |> range(start: 1970-01-01T00:00:00Z)\n" +
                "  |> limit(n: "+nbElems+")  \n" +
                "  |> filter(fn: (r) => r[\"_measurement\"] == \"sensor\")\n" +
                "  |> yield(name: \"mean\")";
        QueryApi service = client.getQueryApi();
        List<FluxTable> res = service.query(query, IFDBConst.org);
        double add = 0D;
        for (FluxTable re : res) {
            LOG.info("INFO  {}", re);
            List<FluxRecord> records = re.getRecords();
            for (FluxRecord record : records) {
                //LOG.info("Record: {} - {}", record.getTime(), record.getValue());
                add += (Double)record.getValue();
            }
        }
        client.close();
        long end = System.currentTimeMillis();
        LOG.info("Read in {} = {} elements / seconds", Duration.ofMillis(end - start), "" + (1.0 * nbElems / Duration.ofMillis(end - start).toMillis() * 1000));

    }
}
