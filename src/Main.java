import com.lssemail.datasource.PoolConnection;
import com.lssemail.datasource.PoolDataSource;
import com.lssemail.datasource.impl.PoolDataSourceImpl;

import java.util.concurrent.TimeUnit;

public class Main {

    public static void main(String[] args) throws Exception{
        System.out.println("Hello World!");

        PoolDataSource dataSource = new PoolDataSourceImpl();
        for(int i=0; i< 30 ; i++){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        PoolConnection connection = dataSource.getDataSource();
                        TimeUnit.SECONDS.sleep(1);
                        connection.closeConnection();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }).start();
        }

    }
}
