package cn.bosenkeji.service.Impl;

import cn.bosenkeji.mapper.CoinSortMapper;
import cn.bosenkeji.service.CoinSortService;
import cn.bosenkeji.vo.coin.CoinSort;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;

/**
 * @Author CAJR
 * @create 2019/7/10 18:21
 */
@Service
public class CoinSortServiceImpl implements CoinSortService {
    @Resource
    CoinSortMapper coinSortMapper;

    @Override
    public List<CoinSort> list() {
        return coinSortMapper.findAll();
    }

    @Override
    public PageInfo listByPage(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        return new PageInfo(list());
    }

    @Override
    public Optional<CoinSort> get(int id) {
        return Optional.ofNullable(coinSortMapper.selectByPrimaryKey(id));
    }

    @Override
    public boolean add(CoinSort coinSort) {
        return coinSortMapper.insertSelective(coinSort) == 1;
    }

    @Override
    public boolean update(CoinSort coinSort) {
        return coinSortMapper.updateByPrimaryKeySelective(coinSort) == 1;
    }

    @Override
    public boolean delete(int id) {
        return coinSortMapper.deleteByPrimaryKey(id) == 1;
    }
}
