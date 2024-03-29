package cn.bosenkeji.controller;

import cn.bosenkeji.service.TradePlatformService;
import cn.bosenkeji.util.Result;
import cn.bosenkeji.vo.tradeplatform.TradePlatform;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;
import java.time.LocalDateTime;



/**
 * @Author CAJR
 * @create 2019/7/15 18:01
 */
@RestController
@RequestMapping("/trade_platform")
@Validated
@Api(tags = "tradePlatform 交易平台接口",value = "提供交易平台相关功能 Rest接口")
public class TradePlatformController {

    @Resource
    TradePlatformService tradePlatformService;

    @Resource
    private DiscoveryClient client ;

    /**
     * 单表查询
     * @param pageNum
     * @param pageSizeCommon
     * @return
     */
    @ApiOperation(value = "获取交易平台分页信息",httpMethod = "GET",nickname = "getListTradePlatformWithPage")
    @GetMapping("/")
    public PageInfo listAll(@RequestParam( value="pageNum",defaultValue="1") int pageNum,
                         @RequestParam(value = "pageSizeCommon",defaultValue = "10") int pageSizeCommon){

        return this.tradePlatformService.listByPage(pageNum,pageSizeCommon);
    }


    @ApiOperation(value = "获取交易平台单个信息接口",httpMethod = "GET" ,nickname = "getOneTradePlatform")
    @GetMapping("/{id}")
    public TradePlatform get(@PathVariable("id") @Min(1) @ApiParam(value = "交易平台ID", required = true, type = "integer",example = "1") int id){
        return this.tradePlatformService.get(id);
    }


    @ApiOperation(value = "添加交易平台单个信息接口",httpMethod = "POST",nickname = "addOneTradePlatform")
    @PostMapping("/")
    public Result add(@RequestBody @Valid @NotNull @ApiParam(value = "交易平台实体", required = true, type = "string") TradePlatform tradePlatform){
        if (this.tradePlatformService.checkExistByName(tradePlatform.getName()).get() >= 1){
            return new Result<>(null,"交易平台已存在");
        }

        tradePlatform.setStatus(1);
        tradePlatform.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
        tradePlatform.setUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));
        return new Result<>(this.tradePlatformService.add(tradePlatform));
    }


    @ApiOperation(value = "更新交易平台接口",httpMethod = "PUT",nickname = "updateTradePlatform")
    @PutMapping("/")
    public Result update(@RequestBody @NotNull @ApiParam(value = "交易平台实体", required = true, type = "string") TradePlatform tradePlatform){

        if (this.tradePlatformService.get(tradePlatform.getId()) == null){
            return new Result<>(null,"交易平台不存在");
        }

        tradePlatform.setUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));
        return new Result<>(this.tradePlatformService.update(tradePlatform));
    }


    @ApiOperation(value = "删除交易平台接口",httpMethod = "DELETE",nickname = "deleteOneTradePlatform")
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable("id") @Min(1) @ApiParam(value = "交易平台ID", required = true, type = "integer",example = "1") int id){

        if (this.tradePlatformService.get(id) == null){
            return new Result<>(null,"交易平台不存在");
        }
        return new Result<>(this.tradePlatformService.delete(id));
    }

    @ApiOperation(value = "发现服务")
    @RequestMapping("/discover")
    @ApiIgnore
    public Object discover() { // 直接返回发现服务信息
        return this.client ;
    }


    @GetMapping("/by_name")
    public TradePlatform getTradePlatformByName(@RequestParam("name") String name){
        return this.tradePlatformService.getByName(name);
    }
}
