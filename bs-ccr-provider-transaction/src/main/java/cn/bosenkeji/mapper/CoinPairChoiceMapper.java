package cn.bosenkeji.mapper;

import cn.bosenkeji.vo.transaction.CoinPairChoice;
import cn.bosenkeji.vo.transaction.CoinPairChoiceJoinCoinPair;
import org.apache.ibatis.annotations.Param;

import java.sql.Timestamp;
import java.util.List;

/**
 * @Author CAJR
 * @create 2019/7/17 10:09
 */

public interface CoinPairChoiceMapper {
    int deleteByPrimaryKey(int id);

    int logicDelete(int id, Timestamp updatedAt,int status);

    int batchDelete(List<Integer> id);

    int batchLogicDelete(@Param("list")List<Integer> id, Timestamp updatedAt);

    int insert(CoinPairChoice record);

    int insertSelective(CoinPairChoice record);

    CoinPairChoice selectByPrimaryKey(int id);

    CoinPairChoice selectByDisregardStatus(int id);

    Integer selectIdByCoinPartnerIdAndRobotIdAndStatus(int coinPartnerId,int tradePlatformApiBindProductComboId);

    int updateByPrimaryKeySelective(CoinPairChoice record);

    int updateByPrimaryKey(CoinPairChoice record);

    Integer checkExistByCoinPartnerIdAndRobotIdAndStatus(int coinPartnerId,int tradePlatformApiBindProductComboId);


    List<CoinPairChoice> findAllByTradePlatformApiBindProductComboId (int tradePlatformApiBindProductComboId);

    List<CoinPairChoice> findByTradePlatformApiBindProductComboIdAndStatus (int tradePlatformApiBindProductComboId);

    List<CoinPairChoice> findAll();

    List<Integer> findAllCoinPairChoiceId();

    List<CoinPairChoice> findByTradePlatformApiBindProductComboIdAndIsStart(@Param("tradePlatformApiBindProductComboId") int tradePlatformApiBindProductComboId, @Param("isStart") int isStart);
    int updateByBindId(@Param("originalBindId") int originalBindId, @Param("newBindId") int newBindId);
}