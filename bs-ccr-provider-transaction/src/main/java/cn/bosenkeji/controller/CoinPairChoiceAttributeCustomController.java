package cn.bosenkeji.controller;

import cn.bosenkeji.exception.AddException;
import cn.bosenkeji.exception.DeleteException;
import cn.bosenkeji.exception.NotFoundException;
import cn.bosenkeji.exception.UpdateException;
import cn.bosenkeji.exception.enums.CoinPairChoiceAttributeCustomEnum;
import cn.bosenkeji.service.CoinPairChoiceAttributeCustomService;
import cn.bosenkeji.util.Result;
import cn.bosenkeji.vo.transaction.CoinPairChoiceAttributeCustom;
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
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * add cache by xivin
 * 单表
 */

/**
 * @Author CAJR
 * @create 2019/7/18 17:35
 */
@RestController
@RequestMapping("/coin_pair_choice_attribute_custom")
@Validated
@Api(tags = "CoinPairChoiceAttributeCustom 自选货币自定义属性接口",value = "自选货币自定义属性相关的REST接口")
public class CoinPairChoiceAttributeCustomController {
    @Resource
    CoinPairChoiceAttributeCustomService coinPairChoiceAttributeCustomService;

    @Resource
    DiscoveryClient client;

    @ApiOperation(value = "获取交易参数接口",httpMethod = "GET" ,nickname = "getOneCoinPairChoiceAttributeCustomByCoinPairChoiceID")
    @GetMapping("/{coinPairChoiceId}")
    public CoinPairChoiceAttributeCustom getByCoinPairChoiceID(@PathVariable("coinPairChoiceId") @ApiParam(value = "自选币ID", required = true, type = "integer",example = "1") int coinPairChoiceId){
        return this.coinPairChoiceAttributeCustomService.getByCoinPartnerChoiceId(coinPairChoiceId);
    }

    @ApiOperation(value = "设置交易参数接口",httpMethod = "POST",nickname = "settingParameters")
    @PostMapping("/")
    public Result settingParameters(@RequestBody @Valid @ApiParam(value = "自选币自定义属性实体", required = true, type = "string") CoinPairChoiceAttributeCustom coinPairChoiceAttributeCustom){

        if (this.coinPairChoiceAttributeCustomService.checkByCoinPartnerChoiceId(coinPairChoiceAttributeCustom.getCoinPartnerChoiceId()).get() >=1){
            return new Result<>(null,"自选币自定义属性已存在");
        }

        coinPairChoiceAttributeCustom.setStatus(1);
        coinPairChoiceAttributeCustom.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
        coinPairChoiceAttributeCustom.setUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));
        return new Result<>(this.coinPairChoiceAttributeCustomService.add(coinPairChoiceAttributeCustom));
    }

    @ApiOperation(value = "更新自选货币自定义属性接口",httpMethod = "PUT",nickname = "editCoinPairChoiceAttributeCustom")
    @PutMapping("/")
    public Result update(@RequestBody  @ApiParam(value = "自选币自定义属性实体", required = true, type = "string") CoinPairChoiceAttributeCustom coinPairChoiceAttributeCustom){
        if (this.coinPairChoiceAttributeCustomService.get(coinPairChoiceAttributeCustom.getId()) == null){
            return new Result<>(null,"自选币自定义属性不存在");
        }

        return new Result<>(this.coinPairChoiceAttributeCustomService.update(coinPairChoiceAttributeCustom));
    }

    @ApiOperation(value = "删除自选货币自定义属性接口",httpMethod = "DELETE",nickname = "deleteOneCoinPairChoiceAttributeCustomByCoinPartnerChoiceId")
    @DeleteMapping("/{coinPartnerChoiceId}")
    public Result delete(@PathVariable("coinPartnerChoiceId") @Min(1) @ApiParam(value = "自选币ID", required = true, type = "integer",example = "1") int coinPartnerChoiceId){
        if (this.coinPairChoiceAttributeCustomService.checkByCoinPartnerChoiceId(coinPartnerChoiceId).get() < 1){
            return new Result<>(null,"自选币自定义属性不存在");
        }

        return new Result<>(this.coinPairChoiceAttributeCustomService.delete(coinPartnerChoiceId));
    }


    @ApiOperation(value = "发现服务")
    @RequestMapping("/discover")
    @ApiIgnore
    public Object discover() { // 直接返回发现服务信息
        return this.client ;
    }
}
