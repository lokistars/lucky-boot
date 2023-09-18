package com.lucky.platform.binlog;

import com.github.shyiko.mysql.binlog.BinaryLogClient;
import com.github.shyiko.mysql.binlog.event.*;
import java.io.IOException;

/**
 * @author: Loki
 * @data: 2022-05-20 09:40
 */
public class BinlogConnector {

    private static String host ="10.10.11.46";
    private static Integer port = 3306;
    private static String userName ="root";
    private static String password ="123456";

    public static void main(String[] args) throws IOException {
        BinaryLogClient client = new BinaryLogClient(host,port,userName,password);
        client.registerEventListener(event -> {
            final EventData data = event.getData();
            if (data instanceof TableMapEventData){
                TableMapEventData table = (TableMapEventData) data;
                System.out.println(table.getTableId()+""+table.getTable());
            }
            if (data instanceof UpdateRowsEventData) {
                System.out.println("Update:");
                System.out.println(data.toString());
            } else if (data instanceof WriteRowsEventData) {
                System.out.println("Insert:");
                System.out.println(data.toString());
            } else if (data instanceof DeleteRowsEventData) {
                System.out.println("Delete:");
                System.out.println(data.toString());
            }
        });

        client.connect();
    }
}
