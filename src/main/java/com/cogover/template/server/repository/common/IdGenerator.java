package com.cogover.template.server.repository.common;

import com.cogover.exception.DbException;
import com.cogover.template.server.util.ServerUtil;
import lombok.Getter;

public class IdGenerator {

    public static final int LIMIT = 10;
    public static final int MAX_LENGTH = 12;

    @Getter
    private String prefix;
    private final DbRepository<?> repository;

    public IdGenerator(String prefix, DbRepository<?> repository) {
        this.prefix = prefix;
        this.repository = repository;
    }

    /**
     * ID can duy nhat cho toan bo he thong
     */
    public String generate(String mysqlId) {
        String id;
        for (int i = 0; i < LIMIT; i++) {
            id = prefix + ServerUtil.randomString(MAX_LENGTH - getPrefix().length());
            try {
                Object record = repository.findOneById(mysqlId, id);
                if (record == null) {
                    return id;
                }
            } catch (DbException e) {
                throw new RuntimeException("Cannot generate unique id");
            }
        }
        throw new RuntimeException("Cannot generate unique id");
    }

}
