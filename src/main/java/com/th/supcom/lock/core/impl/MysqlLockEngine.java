package com.th.supcom.lock.core.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import com.th.supcom.lock.core.DistLockInfo;
import com.th.supcom.lock.core.ILockEngine;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @function 锁引擎-Zookeeper实现
 * @date 2019年1月16日 下午2:49:24
 * @author 李桥
 * @version 1.0
 */
@Service
@Slf4j
public class MysqlLockEngine implements ILockEngine
{
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public boolean acquire (DistLockInfo lockInfo)
    {
        String lockKey = lockInfo.getLockKey ();
        if (null != getPrimaryDistLockInfo (lockKey))
        {
            return false;

        }
        try
        {
            jdbcTemplate.update ("insert into t_distlock_info(lock_key,lock_value,expire,acquire_timeout,acquire_count,create_date) values(?,?,?,?,?,?)",
                                 lockInfo.getLockKey (), lockInfo.getLockValue (), lockInfo.getExpire (),
                                 lockInfo.getAcquireTimeout (), lockInfo.getAcquireCount (), lockInfo.getCreateDate ());
        }
        catch (org.springframework.dao.DuplicateKeyException e)
        {

            //log.error ("该锁已经被被其它主体占用，不可争抢到锁，请重试！");
            return false;

        }
        catch (Exception e)
        {
            //log.error ("获取mysql锁异常!");
            return false;

        }
        return true;
    }

    @Override
    public boolean releaseLock (DistLockInfo lockInfo)
    {
        String lockKey = lockInfo.getLockKey ();
        DistLockInfo dbLockInfo = getPrimaryDistLockInfo (lockInfo.getLockKey ());
        if (null == dbLockInfo)
        {
            log.error ("不需要释放锁，因为从未获得过！");
            return true;
        }
        jdbcTemplate.update ("delete from t_distlock_info where lock_key=? ", lockKey);
        log.info ("释放锁成功，锁:" + dbLockInfo);
        return true;
    }

    private static class DistLockInfoRowMapper implements RowMapper <DistLockInfo>
    {
        @Override
        public DistLockInfo mapRow (ResultSet rs, int arg1) throws SQLException
        {
            DistLockInfo distLockInfo = new DistLockInfo ();
            distLockInfo.setLockKey (rs.getString ("lock_key"));
            distLockInfo.setLockValue (rs.getString ("lock_value"));
            distLockInfo.setExpire (rs.getLong ("expire"));
            distLockInfo.setAcquireTimeout (rs.getLong ("acquire_timeout"));
            distLockInfo.setAcquireCount (rs.getInt ("acquire_count"));
            distLockInfo.setCreateDate (rs.getTimestamp ("create_date"));
            return distLockInfo;
        }

    }

    private DistLockInfo getPrimaryDistLockInfo (String lockKey)
    {
        List <DistLockInfo> resultList = jdbcTemplate.query ("select * from t_distlock_info where lock_key=?",
                                                             new DistLockInfoRowMapper (), lockKey);
        if (null != resultList && resultList.size () > 0)
        {
            return resultList.get (0);

        }

        return null;
    }
}
