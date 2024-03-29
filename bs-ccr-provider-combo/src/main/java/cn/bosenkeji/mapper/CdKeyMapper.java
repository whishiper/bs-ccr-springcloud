package cn.bosenkeji.mapper;

import cn.bosenkeji.vo.cdKey.CdKey;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CdKeyMapper {

    int insert(CdKey cdKey);

    int insertSelective(CdKey cdKey);

    int insertCdKeyByBatch(List<CdKey> cdKeys);

    int updateUserIdAndStatusById(@Param("id") Integer id, @Param("userId") int userId, @Param("status") Integer status, @Param("userProductComboId") int userProductComboId);

    int deleteByIds(@Param("ids") List<Integer> ids);

    List<CdKey> getByIds(@Param("ids") List<Integer> ids);

    CdKey getById(@Param("id") Integer id);

    List<CdKey> get();

    List<CdKey> getBySearch(CdKey cdKey);

    CdKey getByKeyAndStatus(@Param("key") String key,@Param("status") int status);
}