package cn.bosenkeji.controller;

import cn.bosenkeji.exception.NotFoundException;
import cn.bosenkeji.exception.enums.TradePlatformEnum;
import cn.bosenkeji.service.TradePlatformService;
import cn.bosenkeji.vo.tradeplateform.TradePlatform;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;
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


    @ApiOperation(value = "获取交易平台分页信息",httpMethod = "GET",nickname = "getListTradePlatformWithPage")
    @GetMapping("/")
    public PageInfo list(@RequestParam( value="pageNum",defaultValue="1") int pageNum,
                         @RequestParam(value = "pageSizeCommon",defaultValue = "10") int pageSizeCommon){

        return this.tradePlatformService.listByPage(pageNum,pageSizeCommon);
    }


    @ApiOperation(value = "获取交易平台单个信息接口",httpMethod = "GET" ,nickname = "getOneTradePlatform")
    @GetMapping("/{id}")
    public TradePlatform get(@PathVariable("id") @Min(1) @ApiParam(value = "交易平台ID", required = true, type = "integer",example = "1") int id){
        return this.tradePlatformService.get(id).orElseThrow(()-> new NotFoundException(TradePlatformEnum.NAME));
    }

    @ApiOperation(value = "添加交易平台单个信息接口",httpMethod = "POST",nickname = "addOneTradePlatform")
    @PostMapping("/")
    public boolean add(@RequestBody @NotNull @ApiParam(value = "交易平台实体", required = true, type = "string") TradePlatform tradePlatform){
        tradePlatform.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
        tradePlatform.setUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));
        return this.tradePlatformService.add(tradePlatform);
    }

    @ApiOperation(value = "更新交易平台接口",httpMethod = "PUT",nickname = "updateTradePlatform")
    @PutMapping("/")
    public boolean update(@RequestBody @NotNull @ApiParam(value = "交易平台实体", required = true, type = "string") TradePlatform tradePlatform){
        tradePlatform.setUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));
        return this.tradePlatformService.update(tradePlatform);
    }

    @ApiOperation(value = "删除交易平台接口",httpMethod = "DELETE",nickname = "deleteOneTradePlatform")
    @DeleteMapping("/{id}")
    public boolean delete(@PathVariable("id") @Min(1) @ApiParam(value = "交易平台ID", required = true, type = "integer",example = "1") int id){
        return this.tradePlatformService.delete(id);
    }

    @ApiOperation(value = "发现服务")
    @RequestMapping("/discover")
    @ApiIgnore
    public Object discover() { // 直接返回发现服务信息
        return this.client ;
    }
}
