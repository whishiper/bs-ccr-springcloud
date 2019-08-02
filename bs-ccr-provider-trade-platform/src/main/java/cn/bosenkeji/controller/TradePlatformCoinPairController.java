package cn.bosenkeji.controller;

import cn.bosenkeji.exception.AddException;
import cn.bosenkeji.exception.DeleteException;
import cn.bosenkeji.exception.NotFoundException;
import cn.bosenkeji.exception.UpdateException;
import cn.bosenkeji.exception.enums.TradePlatformCoinPairEnum;
import cn.bosenkeji.service.TradePlatformCoinPairService;
import cn.bosenkeji.util.Result;
import cn.bosenkeji.vo.tradeplatform.TradePlatformCoinPair;
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
 * @create 2019/7/16 14:55
 */
@RestController
@RequestMapping("/trade_platform_coin_pairs")
@Validated
@Api(tags = "tradePlatformCoinPair 交易平台货币对接口",value = "提供交易平台货币对相关功能 Rest接口")
public class TradePlatformCoinPairController {

    @Resource
    TradePlatformCoinPairService tradePlatformCoinPairService;

    @Resource
    DiscoveryClient client;


    @ApiOperation(value = "获取平台货币对列表接口",httpMethod = "GET",nickname = "getTradePlatformCoinPairWithPage")
    @GetMapping("/")
    public PageInfo list(@RequestParam( value="pageNum",defaultValue="1") int pageNum,
                         @RequestParam(value = "pageSizeCommon",defaultValue = "10") int pageSizeCommon){
        return this.tradePlatformCoinPairService.listByPage(pageNum,pageSizeCommon);
    }

    @ApiOperation(value = "获取平台货币对单个信息接口",httpMethod = "GET",nickname = "getOneTradePlatformCoinPair")
    @GetMapping("/{id}")
    public TradePlatformCoinPair get(@PathVariable("id") @Min(1) @ApiParam(value = "交易平台货币对ID", required = true, type = "integer",example = "1") int id){
        return this.tradePlatformCoinPairService.get(id).orElseThrow(()-> new NotFoundException(TradePlatformCoinPairEnum.NAME));
    }

    @ApiOperation(value = "添加平台货币对单个信息接口",httpMethod = "POST",nickname = "addOneTradePlatformCoinPair")
    @PostMapping("/")
    public Result add(@RequestBody @NotNull @Valid @ApiParam(value = "交易平台货币对实体", required = true, type = "string") TradePlatformCoinPair tradePlatformCoinPair){

        this.tradePlatformCoinPairService.checkByTradePlatformIdAndCoinPairId(tradePlatformCoinPair.getTradePlatformId(),tradePlatformCoinPair.getCoinPairId())
                .filter((value)->value==0)
                .orElseThrow(()->new AddException(TradePlatformCoinPairEnum.NAME));

        tradePlatformCoinPair.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
        tradePlatformCoinPair.setUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));
        return new Result<>(this.tradePlatformCoinPairService.add(tradePlatformCoinPair)
                .filter((value)->value>=1)
                .orElseThrow(()->new AddException(TradePlatformCoinPairEnum.NAME)));
    }

    @ApiOperation(value = "更新单个平台货币对接口",httpMethod = "PUT",nickname = "updateTradePlatformCoinPair")
    @PutMapping("/")
    public Result update(@RequestBody @NotNull @ApiParam(value = "交易平台货币对实体", required = true, type = "string") TradePlatformCoinPair tradePlatformCoinPair){
        tradePlatformCoinPair.setUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));
        return new Result<>(this.tradePlatformCoinPairService.update(tradePlatformCoinPair)
                .filter((value)->value>=1)
                .orElseThrow(()->new UpdateException(TradePlatformCoinPairEnum.NAME)));
    }

    @ApiOperation(value = "删除单个平台货币对接口",httpMethod = "DELETE",nickname = "deleteOneTradePlatformCoinPair")
    @DeleteMapping("/")
    public Result delete(@RequestParam("tradePlatformId") @Min(1) @ApiParam(value = "交易平台ID", required = true, type = "integer",example = "1") int tradePlatformId,
            @RequestParam("coinPairId") @Min(1) @ApiParam(value = "交易平台货币对ID", required = true, type = "integer",example = "1") int coinPairId){
        return new Result<>(this.tradePlatformCoinPairService.delete(tradePlatformId,coinPairId)
                .filter((value)->value>=1)
                .orElseThrow(()->new DeleteException(TradePlatformCoinPairEnum.NAME)));
    }

    @ApiOperation(value = "发现服务")
    @RequestMapping("/discover")
    @ApiIgnore
    public Object discover() { // 直接返回发现服务信息
        return this.client ;
    }

}