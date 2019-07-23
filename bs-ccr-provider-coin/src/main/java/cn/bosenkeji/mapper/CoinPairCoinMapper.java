package cn.bosenkeji.mapper;

import cn.bosenkeji.vo.coin.CoinPairCoin;

import java.util.List;

/**
 * @Author CAJR
 * @create 2019/7/9 16:27
 */

public interface CoinPairCoinMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(CoinPairCoin record);

    int insertSelective(CoinPairCoin record);

    CoinPairCoin selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(CoinPairCoin record);

    int updateByPrimaryKey(CoinPairCoin record);

    List<CoinPairCoin> findAll();
}