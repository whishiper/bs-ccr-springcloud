package cn.bosenkeji.service.impl;


import cn.bosenkeji.annotation.cache.ZSetCacheEvict;
import cn.bosenkeji.annotation.cache.ZSetCacheable;
import cn.bosenkeji.interfaces.RedisInterface;
import cn.bosenkeji.mapper.UserMapper;
import cn.bosenkeji.service.UserService;
import cn.bosenkeji.vo.User;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {

    @Resource
    private UserMapper userMapper;

    @Cacheable(value = RedisInterface.USER_REDIS_ID_KEY,key = "#id",unless = "#result==null")
    @Override
    public User get(int id) {
        return userMapper.selectByPrimaryKey(id);
    }

    @Override
    public User getByUsername(String username) {
        return userMapper.selectByUsername(username);
    }

    @Override
    public User getByTel(String tel) {
        return userMapper.selectByTel(tel);
    }

    @Override
    public Integer add(User user) {
        return userMapper.insertSelective(user);
    }

    @ZSetCacheEvict(key = RedisInterface.USER_REDIS_USERNAME_KEY,score = "#user.id",unless = "#result < 1")
    @Caching(
            evict = {
                    @CacheEvict(value = RedisInterface.USER_REDIS_ID_KEY,key = "#user.id",condition = "#result > 0"),
                    @CacheEvict(value = RedisInterface.COMBO_DAY_LIST_UPC_ID_KEY,allEntries = true,condition = "#result > 0")
            }
    )
    @Override
    public Integer updateByPrimaryKeySelective(User user) {
        if(user.getPassword() != null) {
            user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
        }
        return userMapper.updateByPrimaryKeySelective(user);
    }

    /**
     * 选择性 更行用户信息 并且不会更新基本类型
     * @param user
     * @return
     */
    @ZSetCacheEvict(key = RedisInterface.USER_REDIS_USERNAME_KEY,score = "#user.id",unless = "#result < 1")
    @Caching(
            evict = {
                    @CacheEvict(value = RedisInterface.USER_REDIS_ID_KEY,key = "#user.id",condition = "#result > 0"),
                    @CacheEvict(value = RedisInterface.COMBO_DAY_LIST_UPC_ID_KEY,allEntries = true,condition = "#result > 0")
            }
    )
    @Override
    public Integer updateByPrimaryKeySelectiveExcludeBase(User user) {
        if(user.getPassword() != null) {
            user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
        }
        return userMapper.updateByPrimaryKeySelectiveExcludeBase(user);
    }

    @ZSetCacheEvict(key = RedisInterface.USER_REDIS_USERNAME_KEY,score = "#id",unless = "#result < 1")
    @Caching(
            evict = {
                    @CacheEvict(value = RedisInterface.USER_REDIS_ID_KEY,key = "#id"),
                    @CacheEvict(value = RedisInterface.COMBO_DAY_LIST_UPC_ID_KEY,allEntries = true)
            }
    )
    @Override
    public Integer delete(int id) {
        return userMapper.deleteByPrimaryKey(id);
    }

    @Override
    public Integer checkExistByUsrename(String username) {
        return userMapper.checkExistByUsername(username);
    }

    @Override
    public Integer checkExistByTel(String tel) {
        return userMapper.checkExistByTel(tel);
    }

    @Caching(
            evict = {
                    @CacheEvict(value = RedisInterface.USER_REDIS_ID_KEY,key = "#id",condition = "#result > 0"),
                    @CacheEvict(value = RedisInterface.COMBO_DAY_LIST_UPC_ID_KEY,allEntries = true,condition = "#result > 0")
            }
    )
    @Override
    public Integer updateBinding(int id, int isBinding) {
        return userMapper.updateBinding(id,isBinding);
    }

    @ZSetCacheable(key = RedisInterface.USER_REDIS_USERNAME_KEY,value = "#username",unless = "#result == null")
    @Override
    public Integer getIdByUsername(String username) {
        User user = userMapper.selectByUsername(username);
        if(user!=null) {
            return user.getId();
        }else
            return null;
    }


    @Override
    public Integer updatePasswordByTel(String tel, String password) {
        return userMapper.updatePasswordByTel(tel,password);
    }

    @Override
    public Map<Integer, User> getByIds(List<Integer> ids) {
        return userMapper.getByIds(ids);
    }

    @Override
    public List<User> list() {
        return userMapper.list();
    }

    @Override
    public PageInfo<User> listByPage(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        return new PageInfo<>(this.list());
    }

    @Override
    public PageInfo<User> listBySearch(Integer status, String tel,Integer sort, int pageNum, int pageSize) {
        //排序处理，按照创建时间
        String orderBy = "created_at ";
        if (sort == 1) {
            orderBy = orderBy + "asc";
        } else {
            orderBy = orderBy + "desc";
        }

        PageHelper.startPage(pageNum,pageSize,orderBy);
        List<User> users = userMapper.listBySearch(status,tel);
        return new PageInfo<>(users);
    }

    //同步缓存
    @Caching(
            evict = {
                    @CacheEvict(value = RedisInterface.USER_REDIS_ID_KEY,key = "#id"),
                    @CacheEvict(value = RedisInterface.COMBO_DAY_LIST_UPC_ID_KEY,allEntries = true)
            }
    )
    @Override
    public void evictUser(int id) {
        System.out.println("update caceh userId"+id);
    }

    @Override
    public Integer checkExistById(int id) {
        return userMapper.checkExistById(id);
    }

    @Caching(
            evict = {
                    @CacheEvict(value = RedisInterface.USER_REDIS_ID_KEY,key = "#id",condition = "#result > 0"),
                    @CacheEvict(value = RedisInterface.COMBO_DAY_LIST_UPC_ID_KEY,allEntries = true)
            }
    )
    @Override
    public Integer updateStatusById(Integer id,Integer status) {
        User user = new User();
        user.setId(id);
        user.setStatus(status);
        return userMapper.updateByPrimaryKeySelective(user);
    }
}
