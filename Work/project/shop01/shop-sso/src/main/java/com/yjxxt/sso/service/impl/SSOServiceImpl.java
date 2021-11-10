package com.yjxxt.sso.service.impl;

import com.yjxxt.common.result.Md5Util;
import com.yjxxt.common.result.UUIDUtil;
import com.yjxxt.sso.mapper.AdminMapper;
import com.yjxxt.sso.pojo.Admin;
import com.yjxxt.sso.pojo.AdminExample;
import com.yjxxt.sso.service.ISSOService;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service(version = "1.0")
public class SSOServiceImpl  implements ISSOService {

    @Autowired
    private AdminMapper adminMapper;


    @Value("${user.ticket}")
    private String userTicket;

    @Resource(name = "redisTemplate")
    private RedisTemplate<String,Object> redisTemplate;


    @Override
    public String login(String userName, String password) {
        /**
         * 1.参数校验
         *    userName   password 非空
         * 2.用户记录存在查询
         *     根据用户名查询用户记录
         * 3.密码校验
         *     MD5(password+盐值)
         * 4.生成票据并存储(redis) 默认失效时间30min
         *    ticket
         * 5.返回票据
         */
        /*1.参数校验*/
        if(StringUtils.isBlank(userName)){
            System.out.println("用户名非空!");
            return null;
        }
        if(StringUtils.isBlank(password)){
            System.out.println("密码非空!");
            return null;
        }

        /*2.用户记录存在查询*/
        AdminExample example=new AdminExample();
        AdminExample.Criteria criteria = example.createCriteria();
        criteria.andUserNameEqualTo(userName);
        List<Admin> adminList =adminMapper.selectByExample(example);
        if(CollectionUtils.isEmpty(adminList)){
            System.out.println("用户记录不存在!");
            return null;
        }
        /*3.密码校验（md5（password+盐值））*/
        Admin admin = adminList.get(0);
        password = Md5Util.getMd5WithSalt(password,admin.getEcSalt());
        if(!password.equals(admin.getPassword())){
            System.out.println("密码错误!");
            return null;
        }

         /*4.生成票据并存储到redis*/
        /**
         *  1.系统毫秒数
         *  2.UUID
         *  3.JWT(subject  用户信息)
         *  4.加密算法
         */
        //userTicket = ;
        String ticket = UUIDUtil.getUUID();
        // 设置用户信息30分钟有效
        redisTemplate.opsForValue().set(userTicket+":"+ticket,admin,30, TimeUnit.MINUTES);

        /*5.返回票据*/
        return ticket;
    }

    @Override
    public Admin validateTicket(String ticket) {
        /**
         * 1.参数非空校验
         *     ticket 非空
         * 2.票据有效性校验
         *     判断key 是否失效
         * 3.返回用户信息
         *    userTicket+":"+ticket-->value(admin)
         */
        if(StringUtils.isBlank(ticket)){
            System.out.println("票据为空!");
            return null;
        }
        if(!redisTemplate.hasKey(userTicket+":"+ticket)){
            System.out.println("票据已失效!");
            return null;
        }
        return (Admin) redisTemplate.opsForValue().get(userTicket+":"+ticket);
    }

    @Override
    public void expireTicket(String ticket) {
        redisTemplate.expire(ticket,30,TimeUnit.MINUTES);
    }



    @Override
    public void removeTicket(String ticket) {
        if(!redisTemplate.hasKey(userTicket+":"+ticket)){
            return;
        }
        redisTemplate.delete(userTicket+":"+ticket);
    }


}
