package cn.bosenkeji.controller;

import cn.bosenkeji.service.IUserClientService;
import cn.bosenkeji.vo.User;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author xivin
 * @ClassName cn.bosenkeji.controller
 * @Version V1.0
 * @create 2019-07-31 19:26
 */
@RestController
@RequestMapping("/user")
@Api(tags = "User 用户相关接口",value = "提供用户相关的 Rest API接口")
public class ConsumerUserController {

    @Resource
    private IUserClientService iUserClientService;

    @GetMapping("/{id}")
    @ApiOperation(value = "获取单个用户接口", httpMethod = "GET", nickname = "getOneUser")
    public Object get(@PathVariable("id") @ApiParam(value = "用户IID", required = true, type = "integer", example = "1") int id) {

        return this.iUserClientService.getOneUser(id);

    }

    @GetMapping("/get_by_username")
    @ApiOperation(value = "通过用户名获取单个用户接口", httpMethod = "GET", nickname = "getOneUserByUsername")
    public Object getByUsername(@RequestParam("username") @ApiParam(value = "用户名", required = true, type = "string", example = "zhangsan") String username) {

        return this.iUserClientService.getOneUserByUsername(username);
    }

    @PostMapping("/")
    @ApiOperation(value = "添加单个用户接口", httpMethod = "POST", nickname = "addOneUser")
    public Object add(@RequestBody @ApiParam(value = "用户实体", required = true, type = "string") User user) {

        return this.iUserClientService.addOneUser(user);
    }

    @PutMapping("/")
    @ApiOperation(value = "更新用户接口", httpMethod = "PUT", nickname = "updateUser")
    public Object update(@RequestBody @ApiParam(value = "用户实体", required = true, type = "string") User user) {

        return this.iUserClientService.updateUser(user);
    }

    @DeleteMapping("/{id}")
    @ApiOperation(value = "删除单个用户接口", httpMethod = "DELETE", nickname = "deleteOneUser")
    public Object delete(@PathVariable("id") @ApiParam(value = "用户IID", required = true, type = "integer", example = "1") int id) {

        return this.iUserClientService.deleteOneUser(id);
    }
}