package cn.bosenkeji.service;

import cn.bosenkeji.config.FeignClientConfig;
import cn.bosenkeji.service.fallback.ICoinClientServiceFallbackFactory;
import cn.bosenkeji.service.fallback.IProdcutClientServiceFallbackFactory;
import cn.bosenkeji.vo.Coin;
import cn.bosenkeji.vo.Product;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @ClassName IProductClientService
 * @Description TODO
 * @Author Xivin
 * @Versio V1.0
 **/
@FeignClient(name = "bs-ccr-provider-product",configuration = FeignClientConfig.class,fallbackFactory = IProdcutClientServiceFallbackFactory.class)
public interface IProductClientService {

    @Resource
    @GetMapping("/product/{id}")
    public Product getProduct(@PathVariable("id") int id);

    @GetMapping("/product/")
    public List<Product> listProduct() ;

    @PostMapping("/product/")
    public boolean addProduct(@RequestBody Product product) ;

    @PutMapping("/product/")
    public boolean updateProduct(@RequestBody Product product);
    @DeleteMapping("/product/{id}")
    public boolean deleteProduct(@PathVariable("id") int id);
}