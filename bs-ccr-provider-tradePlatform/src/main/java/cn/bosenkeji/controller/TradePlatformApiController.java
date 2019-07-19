package cn.bosenkeji.controller;

import cn.bosenkeji.exception.NotFoundException;
import cn.bosenkeji.exception.enums.TradePlatformApiEnum;
import cn.bosenkeji.service.TradePlatformApiService;
import cn.bosenkeji.vo.TradePlatformApi;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @Author CAJR
 * @create 2019/7/15 15:06
 */
@RestController
@RequestMapping("/tradeplatformapis")
@Validated
@Api(value = "交易平台api接口")
public class TradePlatformApiController {
    @Resource
    TradePlatformApiService tradePlatformApiService;

    @Resource
    private DiscoveryClient client ;


    @ApiOperation(value = "获取交易平台api列表接口",notes = "交易平台api列表",httpMethod = "GET")
    @GetMapping("/")
    public PageInfo list(@RequestParam( value="pageNum",defaultValue="1") int pageNum,
                         @RequestParam(value = "pageSizeCommon",defaultValue = "10") int pageSizeCommon){
        return this.tradePlatformApiService.listByPage(pageNum,pageSizeCommon);
    }

    @ApiOperation(value = "获取交易平台api单个信息接口",notes = "交易平台api单个信息接口",httpMethod = "GET")
    @GetMapping("/{id}")
    public TradePlatformApi get(@PathVariable("id") @Min(1) int id){
        return this.tradePlatformApiService.get(id).orElseThrow(()-> new NotFoundException(TradePlatformApiEnum.NAME));
    }

    @ApiOperation(value = "添加交易平台api单个信息接口",notes = "添加交易平台api单个信息接口",httpMethod = "POST")
    @PostMapping("/")
    public boolean add(@RequestBody @NotNull TradePlatformApi tradePlatformApi){
        tradePlatformApi.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
        tradePlatformApi.setUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));
        return this.tradePlatformApiService.add(tradePlatformApi);
    }

    @ApiOperation(value = "更新交易平台api接口",notes = "更新交易平台api接口",httpMethod = "PUT")
    @PutMapping("/")
    public boolean update(@RequestBody @NotNull TradePlatformApi tradePlatformApi){
        tradePlatformApi.setUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));
        return this.tradePlatformApiService.update(tradePlatformApi);
    }

    @ApiOperation(value = "删除交易平台api接口",notes = "删除平台api接口",httpMethod = "DELETE")
    @DeleteMapping("/{id}")
    public boolean delete(@PathVariable("id") @Min(1) int id){
        return this.tradePlatformApiService.delete(id);
    }

    @RequestMapping("/discover")
    public Object discover() { // 直接返回发现服务信息
        return this.client ;
    }
}