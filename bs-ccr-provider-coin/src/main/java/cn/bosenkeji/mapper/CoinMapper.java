package cn.bosenkeji.mapper;

import cn.bosenkeji.vo.Coin;

import java.util.List;

/**
 * @Author CAJR
 * @create 2019/7/9 16:27
 */

public interface CoinMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Coin record);

    int insertSelective(Coin record);

    Coin selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Coin record);

    int updateByPrimaryKey(Coin record);

    List<Coin> findAll();
}