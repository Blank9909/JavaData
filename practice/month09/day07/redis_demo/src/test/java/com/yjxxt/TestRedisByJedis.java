package com.yjxxt;

import com.yjxxt.bean.User;
import com.yjxxt.utils.SerializeUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Transaction;
import redis.clients.jedis.params.SetParams;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
public class TestRedisByJedis {

    /*  */
    /**
     * 连接redis
     *//*
    @Test
    public void initConn01() {
        //创建jedis对象，连接redis服务
        Jedis jedis = new Jedis("192.168.203.128", 6379);

        //设置认证密码
        jedis.auth("root");

        //指定数据库，默认是0
        jedis.select(1);

        //使用ping命令，测试链接是否成功
        String result = jedis.ping();
        System.out.println(result);  //返回pong

        //添加一条数据
        jedis.set("username","zhangsan");
        //获取一条数据
        String username = jedis.get("username");
        System.out.println(username);
        //释放资源
        if(jedis!=null){
            jedis.close();
        }
    }

    @Test
    public void initConn02(){
        //初始化redis客户端连接池
        JedisPool jedisPool = new JedisPool(new JedisPoolConfig(), "192.168.203.128", 6379,10000,"root");
        //从连接池获取连接
        Jedis jedis = jedisPool.getResource();
        //指定数据库，默认是0
        jedis.select(2);
        //使用ping命令，测试链接是否成功
        String result = jedis.ping();
        System.out.println(result);
        //添加一条数据
        jedis.set("username","zhangsan");
        //获取一条数据
        String username = jedis.get("username");
        System.out.println(username);
        //释放资源
        if(jedis!=null){
            jedis.close();
        }
    }*/

    @Autowired
    private JedisPool jedisPool;

    private Jedis jedis = null;

    //初始化jesdis对象实例
    @Before
    public void initConn() {
        jedis = jedisPool.getResource();
    }

    //释放资源
    @After
    public void closeConn() {
        if (jedis != null) {
            jedis.close();
        }
    }

    //1.操作String
    @Test
    public void testString() {
        //添加一条数据
        jedis.set("username", "zhangsan");
        jedis.set("age", "18");
        //添加多条数据 参数奇数为key 偶数为value
        jedis.mset("address", "bj", "sex", "1");
        //获取一条数据
        String username = jedis.get("username");
        System.out.println(username);
        //获取多条数据
        List<String> list = jedis.mget("username", "age", "address", "sex");

        list.forEach(System.out::println);

    }

    @Test
    public void testHash() {
        jedis.hset("userInfo", "name", "lisi");
        //添加多条数据
        HashMap<String, String> map = new HashMap<>();
        map.put("age", "20");
        map.put("sex", "1");
        jedis.hmset("userInfo", map);
        //获取一条数据
        List<String> list = jedis.hmget("userInfo", "age", "sex");
        list.forEach(System.out::println);

        // 获取Hash类型所有的数据
        Map<String, String> userMap = jedis.hgetAll("userInfo");
        for (Map.Entry<String, String> userInfo : userMap.entrySet()) {
            System.out.println(userInfo.getKey() + "--" +
                    userInfo.getValue());
        }
    }

    @Test
    public void testList() {
        // 左添加(上)
        // jedis.lpush("students", "Wang Wu", "Li Si");
        // 右添加(下)
        // jedis.rpush("students", "Zhao Liu");
        // 获取 start起始下标 end结束下标 包含关系
        List<String> students = jedis.lrange("students", 0, 2);
        for (String stu : students) {
            System.out.println(stu);
        }
        // 获取总条数
        Long total = jedis.llen("students");
        System.out.println("总条数：" + total);
        // 删除单条 删除列表中第一次出现的Li Si
        // jedis.lrem("students", 1, "Li Si");
        // 删除多条
        // jedis.del("students");
    }

    // 5.操作sorted set-有序
    @Test
    public void testSortedSet() {
        Map<String, Double> scoreMembers = new HashMap<>();
        scoreMembers.put("zhangsan", 7D);
        scoreMembers.put("lisi", 3D);
        scoreMembers.put("wangwu", 5D);
        scoreMembers.put("zhaoliu", 6D);
        scoreMembers.put("tianqi", 2D);
        // 添加数据
        jedis.zadd("score", scoreMembers);
        // 获取数据
        Set<String> scores = jedis.zrange("score", 0, 4);
        for (String score: scores) {
            System.out.println(score);
        }
        // 获取总条数
        Long total = jedis.zcard("score");
        System.out.println("总条数：" + total);
        // 删除
        //jedis.zrem("score", "zhangsan", "lisi");
    }


    // Redis中以层级关系、目录形式存储数据
    @Test
    public void testdir(){
        jedis.set("user:01", "user_zhangsan");
        System.out.println(jedis.get("user:01"));
    }

    @Test
    public void testExpire() {
        // 方法一：
        jedis.set("code", "test");
        jedis.expire("code", 180);// 180秒
        jedis.pexpire("code", 180000L);// 180000毫秒
        jedis.ttl("code");// 获取秒
        // 方法二：
        jedis.setex("code", 180, "test");// 180秒
        jedis.psetex("code", 180000L, "test");// 180000毫秒
        jedis.pttl("code");// 获取毫秒
        // 方法三：
        SetParams setParams = new SetParams();
        //不存在的时候才能设置成功
        // setParams.nx();
        // 存在的时候才能设置成功
        setParams.xx();
        //设置失效时间，单位秒
        // setParams.ex(30);
        //查看失效时间，单位毫秒
        setParams.px(30000);
        jedis.set("code","test",setParams);
    }

    // 操作事务
    @Test
    public void testMulti() {
        Transaction tx = jedis.multi();
        // 开启事务
        tx.set("tel", "10010");
        // 提交事务
        // tx.exec();
        // 回滚事务
        tx.discard();
    }
    // 删除
    @Test
    public void testDelete() {
        // 删除 通用 适用于所有数据类型
        jedis.del("score");
    }


    // 操作byte
    @Test
    public void testByte() {
        User user = new User();
        user.setId(2);
        user.setUsername("zhangsan");
        user.setPassword("123");
// 序列化
        byte[] userKey = SerializeUtil.serialize("user:" + user.getId());
        byte[] userValue = SerializeUtil.serialize(user);
        jedis.set(userKey, userValue);
// 获取数据
        byte[] userResult = jedis.get(userKey);
// 反序列化
        User u = (User) SerializeUtil.unserialize(userResult);
        System.out.println(u);
    }

}
