package cn.bosenkeji.mapper;

import cn.bosenkeji.vo.tradeplatform.TradePlatform;

import java.util.List;

/**
 * @Author CAJR
 * @create 2019/7/15 15:43
 */

public interface TradePlatformMapper {
    int deleteByPrimaryKey(int id);

    int insert(TradePlatform record);

    int insertSelective(TradePlatform record);

    TradePlatform selectByPrimaryKey(int id);

    TradePlatform selectByName(String name);

    int updateByPrimaryKeySelective(TradePlatform record);

    int updateByPrimaryKey(TradePlatform record);

    List<TradePlatform> findAll();

    Integer checkExistByName(String name);

}