package cn.wr.collect.sync.constants;

import org.apache.commons.lang3.StringUtils;

public class TimeFields {
    public static final String ZERO_TIME = "0000-00-00 00:00:00";
    public static final String ZERO_DATE = "0000-00-00";

    public static final String [] fields = {
            "gmtCreated",
            "gmtUpdated",
            "goods_create_time",
            "goods_update_time",
            "created_at",
            "updated_at",
            "gmt_created",
            "gmt_updated",
            "gmtcreated",
            "gmtupdated",
            "last_date",
            "last_pg_date",
            "expiry_date",
            "finish_at",
            "start_at",
            "release_at",
            "start_time",
            "end_time",
            "last_get",
    };


    public static boolean checkContain(String field) {
        for (String f : fields) {
            if (StringUtils.equals(f, field)) {
                return true;
            }
        }
        return false;
    }
}
