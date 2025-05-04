package integration_test.mysql_util;

import lombok.Getter;
import lombok.Setter;

/**
 * @author huydn on 4/8/24 16:28
 */
@Getter
@Setter
public class ColumnDetail {

    private String name;
    private String type;

    public ColumnDetail(String name, String type) {
        this.name = name;
        this.type = type;
    }

}
