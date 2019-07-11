package cn.bosenkeji.service;

import cn.bosenkeji.vo.CoinPair;

import java.util.List;

/**
 * @Author CAJR
 * @create 2019/7/10 18:08
 */
public interface CoinPairService {
    List<CoinPair> list();

    CoinPair get(int id);

    boolean add(CoinPair coinPair);

    boolean update(CoinPair coinPair);

    boolean delete(int id);
}