package com.cogover.template.server.network.pub_sub.db_data_change;

/**
 * @author huydn on 8/5/24 16:31
 */
public class DatabaseDataChange {

    private DatabaseDataChange() {
    }

    public static class Type {
        private Type() {
        }

        public static final String UPDATE = "update";
        public static final String INSERT = "insert";
        public static final String DELETE = "delete";
    }

    public static class Database {
        private Database() {
        }

        public static final String MYSQL = "mysql";
        public static final String MONGO = "mongodb";
    }

}
