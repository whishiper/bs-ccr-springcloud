package cn.bosenkeji.service;

import cn.bosenkeji.vo.tradeplatform.TradePlatform;
import com.github.pagehelper.PageInfo;

import java.util.List;
import java.util.Optional;

/**
 * @Author CAJR
 * @create 2019/7/15 18:02
 */
public interface TradePlatformService {

    List<TradePlatform> list();

    /**
     * @param pageNum
     * @param pageSize
     * @return PageInfo
     */
    PageInfo listByPage(int pageNum, int pageSize);

    TradePlatform get(int id);

    Optional<Integer> add(TradePlatform tradePlatform);

    Optional<Integer> update(TradePlatform tradePlatform);

    Optional<Integer> delete(int id);

    Optional<Integer> checkExistByName(String name);

    TradePlatform getByName(String name);
}
