package cn.bosenkeji.controller;

import cn.bosenkeji.service.IProductClientService;
import cn.bosenkeji.vo.product.Product;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * @author xivin
 * @create 2019-07-12 13:39
 */
@RestController
@RequestMapping("/product")
@Api(tags = "Product 产品相关接口",value = "提供产品相关的 Rest API")
@PreAuthorize("hasAnyAuthority('ADMIN')")
@Validated
public class ConsumerProductController {

    @Resource
    private IProductClientService iProductClientService;


    @ApiOperation(value="获取产品列表api接口",httpMethod = "GET",nickname = "getProductListWithPage")
    @GetMapping("/")
    public Object listProduct(@RequestParam(value="pageNum",defaultValue="1") @Min(1) int pageNum,
                              @RequestParam(value="pageSize",defaultValue="15") @Min(1) int pageSize) {
        return this.iProductClientService.listProduct(pageNum,pageSize);
    }


    @ApiOperation(value="获取产品详情api接口",httpMethod = "GET",nickname = "getOneProduct")
    @GetMapping("/{id}")
    public Object getProduct(@PathVariable("id") @ApiParam(value = "产品ID",required = true,type = "integer",example = "1") @Min(1) int id) {
        return this.iProductClientService.getProduct(id);
    }

    @ApiOperation(value="添加产品api接口",httpMethod = "POST",nickname = "addProduct")
    @PostMapping("/")
    public Object addProduct(@RequestBody @ApiParam(value = "产品实体",required = true,type = "string") @NotNull @Valid Product product) {
        return this.iProductClientService.addProduct(product);
    }

    @ApiOperation(value="更新产品api接口",httpMethod = "PUT",nickname = "updateProduct")
    @PutMapping("/")
    public Object updateProduct(@RequestBody @ApiParam(value = "产品实体",required = true,type = "string") @NotNull Product product) {
        return this.iProductClientService.updateProduct(product);
    }

    @ApiOperation(value="启用、关闭产品api接口",httpMethod = "PUT",nickname = "updateProductStatus")
    @RequestMapping(value="/{id}",method = RequestMethod.PUT)
    public Object updateProductStatus(@PathVariable("id") @ApiParam(value = "产品ID",required = true,type = "integer",example = "1") @Min(1) int id,
                                                 @RequestParam("status") @ApiParam(value = "产品状态",required = true,type = "integer",example = "1") int status) {

        return this.iProductClientService.updateProductStatus(id,status);
    }

    @ApiOperation(value="启用产品接口",httpMethod = "PUT",nickname = "openProductStatus")
    @RequestMapping(value="/open/{id}",method = RequestMethod.PUT)
    public Object openProduct(@PathVariable("id") @ApiParam(value = "产品ID",required = true,type = "integer",example = "1") @Min(1) int id) {

        int status=1;
        return this.iProductClientService.updateProductStatus(id,status);
    }

    @ApiOperation(value="关闭产品接口",httpMethod = "PUT",nickname = "closeProductStatus")
    @RequestMapping(value="/close/{id}",method = RequestMethod.PUT)
    public Object closeProduct(@PathVariable("id") @ApiParam(value = "产品ID",required = true,type = "integer",example = "1") @Min(1) int id) {

        int status=2;
        return this.iProductClientService.updateProductStatus(id,status);
    }

    @ApiOperation(value="根据id删除产品api接口",httpMethod = "DELETE",nickname = "deleteOneProduct")
    @DeleteMapping("/{id}")
    public Object deleteProduct(@PathVariable("id") @ApiParam(value = "产品ID",required = true,type = "integer",example = "1") @Min(1) int id) {
        return  this.iProductClientService.deleteProduct(id);
    }

    @ApiOperation(value="获取启用的产品列表",httpMethod = "GET",nickname = "getProductListByOpenWithPage")
    @RequestMapping(value="/list_by_open",method = RequestMethod.GET)
    public PageInfo listByOpen(
                                 @RequestParam(value="pageNum",defaultValue="1") @Min(1) int pageNum,
                                 @RequestParam(value="pageSize",defaultValue="15") @Min(1) int pageSize)
    {
        int status=1;
        return this.iProductClientService.listByStatus(status,pageNum,pageSize);
    }

    @ApiOperation(value="获取关闭的产品列表",httpMethod = "GET",nickname = "getProductListByCloseWithPage")
    @RequestMapping(value="/list_by_close",method = RequestMethod.GET)
    public PageInfo listByClose(
            @RequestParam(value="pageNum",defaultValue="1") @Min(1) int pageNum,
            @RequestParam(value="pageSize",defaultValue="15") @Min(1) int pageSize)
    {
        int status=2;
        return this.iProductClientService.listByStatus(status,pageNum,pageSize);
    }

}
