package test;

import com.nbugs.table.manage.RedisManage;
import com.nbugs.table.flink.UserDOSink;

/**
 * @author dongweima
 **/
public class UserSinkTest {

    public static void main(String[] args) {
        //Properties properties = ApplicationUtil.getApplicationConfig();
        //RedisManage redisManage = ApplicationUtil.getRedisManage(properties);
        RedisManage redisManage = new RedisManage("172.17.0.3", 6379, 3000,
                "Nbugs2020!", 4, 500, 500, 3000L);
        //UserDOSink userDOSink = new UserDOSink(redisManage, properties);
        UserDOSink userDOSink = new UserDOSink(redisManage);
        userDOSink.doUpdate("{\"countState\":1,\"countTime\":1581786295788,\"id\":2343679,\"msgId\":5528}");
    }
}
