package cn.bosenkeji.service;

import cn.bosenkeji.vo.combo.ProductCombo;
import com.github.pagehelper.PageInfo;

import java.util.List;

/**
 * @author xivin
 * @create 2019-07-11 11:39
 */
public interface IProductComboService {
    PageInfo<ProductCombo> list(int pageNum, int pageSize);
    PageInfo<ProductCombo> listByProductId(int pageNum, int pageSize, int productId);
    PageInfo<ProductCombo> listByProductIdAndStatusWithPage(int pageNum, int pageSize, int productId, int status);
    List<ProductCombo> listByProductIdAndStatus(int productId,int status);
    PageInfo<ProductCombo> listByStatus(int pageNum, int pageSize, int status);
    ProductCombo get(int id);
    int add(ProductCombo productCombo);
    int addBySelective(ProductCombo productCombo);
    int update(ProductCombo productCombo);
    int delete(int id);
    int checkExistByNameAndProductId(String name, int productId);

    List<ProductCombo> getByIds(List<Integer> ids);

    int checkExistByProductId(int productId);
}
