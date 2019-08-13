package cn.bosenkeji.service;

import cn.bosenkeji.vo.product.Product;
import com.github.pagehelper.PageInfo;

/**
 * @author xivin
 * @create 2019-07-11 11:37
 */
public interface IProductService {

    PageInfo<Product> list(int pageNum,int pageSize);
    Product get(int id);

    int add(Product product);

    int delete(int id);

    int update(Product product);

    int updateStatus(Product product);
    int checkExistByName(String name);


}