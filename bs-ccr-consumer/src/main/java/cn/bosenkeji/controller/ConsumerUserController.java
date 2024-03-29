package cn.bosenkeji.controller;

import cn.bosenkeji.annotation.TokenUser;
import cn.bosenkeji.enums.exception.user.UserEnum;
import cn.bosenkeji.exception.UpdateException;
import cn.bosenkeji.service.IUserClientService;
import cn.bosenkeji.service.impl.CustomUserDetailsImpl;
import cn.bosenkeji.util.Result;
import cn.bosenkeji.vo.User;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.parameters.P;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;
import javax.validation.constraints.*;

/**
 * @author xivin
 * @ClassName cn.bosenkeji.controller
 * @Version V1.0
 * @create 2019-07-31 19:26
 */
@RestController
@RequestMapping("/user")
@Api(tags = "User 用户相关接口",value = "提供用户相关的 Rest API接口")
@Validated
public class ConsumerUserController {

    @Resource
    private IUserClientService iUserClientService;

    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    @GetMapping("/{id}")
    @ApiOperation(value = "获取单个用户接口", httpMethod = "GET", nickname = "getOneUser")
    public User get(@PathVariable("id") @ApiParam(value = "用户ID", required = true, type = "integer", example = "1") @Min(1) int id) {

        return this.iUserClientService.getOneUser(id);

    }

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @ApiOperation(value = "获取用户列表接口 ",httpMethod = "GET",nickname = "getUserListWithPage")
    @GetMapping("/")
    public PageInfo listByPage(@RequestParam(value = "pageNum",required = false,defaultValue = "1") @Min(1) Integer pageNum,
                               @RequestParam(value = "pageSize",required = false,defaultValue = "10") @Min(1)  Integer pageSize)
    {
        return this.iUserClientService.listByPage(pageNum,pageSize);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @ApiOperation(value = "按查询条件获取用户列表接口 ",httpMethod = "GET",nickname = "listBySearch")
    @GetMapping("/search")
    public PageInfo listBySearch(@RequestParam(value = "status",required = false) Integer status,
                                 @RequestParam(value = "tel",required = false) String tel,
                                 @RequestParam(value = "sort",required = false,defaultValue = "0") Integer sort,
                                 @RequestParam(value = "pageNum",required = false,defaultValue = "1") int pageNum,
                                 @RequestParam(value = "pageSize",required = false,defaultValue = "10") int pageSize) {
        return this.iUserClientService.listBySearch(status, tel, sort, pageNum, pageSize);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    @GetMapping("/get_by_username")
    @ApiOperation(value = "通过用户名获取单个用户接口", httpMethod = "GET", nickname = "getOneUserByUsername")
    public User getByUsername(@RequestParam("username") @ApiParam(value = "用户名", required = true, type = "string", example = "zhangsan") @NotBlank String username) {

        return this.iUserClientService.getOneUserByUsername(username);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    @GetMapping("/get_by_tel")
    @ApiOperation(value = "通过用户电话获取单个用户接口", httpMethod = "GET", nickname = "getOneUserByTel")
    public User getByTel(@RequestParam("tel")  @ApiParam(value = "用户电话", required = true, type = "string", example = "13556559840") @Size(min = 11,max = 11) @NotEmpty String tel) {

        return this.iUserClientService.getOneUserByTel(tel);
    }

    @PostMapping("/")
    @ApiOperation(value = "添加单个用户接口", httpMethod = "POST", nickname = "addOneUser")
    public Object add(@RequestBody @ApiParam(value = "用户实体", required = true, type = "string") @Validated User user) {

        return this.iUserClientService.addOneUser(user);
    }

    //@PreAuthorize("principal.username.equals(#user.username)")
    @PreAuthorize("hasAnyAuthority('USER','ADMIN')")
    @PutMapping("/")
    @ApiOperation(value = "更新用户接口", httpMethod = "PUT", nickname = "updateUser")
    public Object update(@RequestBody @ApiParam(value = "用户实体", required = true, type = "string") User user) {

        CustomUserDetailsImpl currentUser = this.getCurrentUser();
        if (currentUser != null) {
            user.setId(currentUser.getId());
        }

        return this.iUserClientService.updateUser(user);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    @DeleteMapping("/{id}")
    @ApiOperation(value = "删除单个用户接口", httpMethod = "DELETE", nickname = "deleteOneUser")
    public Object delete(@PathVariable("id") @ApiParam(value = "用户IID", required = true, type = "integer", example = "1") int id) {

        int currentId=this.getCurrentUser().getId();
        return this.iUserClientService.deleteOneUser(currentId);
    }

    @PreAuthorize("hasAuthority('USER')")
    @PutMapping("/update_password/{id}")
    @ApiOperation(value = "修改用户密码",httpMethod = "PUT",nickname = "updatePassword")
    public Object updatePassword(@PathVariable("id")
                                 @ApiParam(value = "用户ID",required = true,type = "integer",example = "1") int id,
                                 @RequestParam("password") @ApiParam(value = "用户密码",required = true,type = "string",example = "123456") @NotEmpty String password) {

        int currentId=this.getCurrentUser().getId();
        return this.iUserClientService.updateUserPassword(currentId,password);
    }

    @PreAuthorize("hasAuthority('USER')")
    @PutMapping("/update_username/{id}")
    @ApiOperation(value = "修改用户名",httpMethod = "PUT",nickname = "updateUsername")
    public Object updateUsername(@PathVariable("id")
                                 @ApiParam(value = "用户ID",required = true,type = "integer",example = "1") int id,
                                 @RequestParam("username") @NotNull @ApiParam(value = "用户名",required = true,type = "string",example = "zhangsan") @NotBlank String username) {

        int currentId=this.getCurrentUser().getId();
        return this.iUserClientService.updateUserUsername(currentId,username);
    }

    @PreAuthorize("hasAuthority('USER')")
    @PutMapping("/update_tel/{id}")
    @ApiOperation(value = "修改电话号码",httpMethod = "PUT",nickname = "updateTel")
    public Object updateTel(@PathVariable("id")
                            @ApiParam(value = "用户ID",required = true,type = "integer",example = "1") int id,
                            @RequestParam("tel") @ApiParam(value = "用户电话",required = true,type = "string",example = "12345678") @NotBlank @Size(min = 11,max = 11) String tel) {

        int currentId=this.getCurrentUser().getId();
        return this.iUserClientService.updateUserTel(currentId,tel);
    }

    @PreAuthorize("hasAnyAuthority('USER','ADMIN')")
    @ApiOperation(value = "用户绑定谷歌验证接口",httpMethod = "PUT",nickname = "updateUserBinding")
    @PutMapping("/update_binding")
    public Result updateBinding(@RequestParam(value = "id",required = false) @ApiParam(value = "用户ID",required = true,type = "integer",example = "1") Integer id,
                                @RequestParam(value = "isBinding",required = false,defaultValue = "1") Integer isBinding) {

        CustomUserDetailsImpl currentUser = this.getCurrentUser();
        if (currentUser != null) {
            id = currentUser.getId();
        }
        return iUserClientService.updateBinding(id,isBinding);

    }


    @ApiOperation(value = "忘记密码接口",httpMethod = "PUT",nickname = "forgetPassword")
    @PutMapping("/forget_password")
    public Result updatePasswordByTel(@RequestParam("tel") @ApiParam(value = "用户电话",required = true,type = "string",example = "123456") @NotEmpty String tel,
                                      @RequestParam("password") @ApiParam(value = "密码",required = true,type = "string",example = "123456") @NotEmpty String password) {
        return iUserClientService.updatePasswordByTel(tel,password);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @ApiOperation(value = "更改用户状态",httpMethod = "PUT",nickname = "updateStatusById")
    @PutMapping("/status")
    public Result updateStatusById(@RequestParam("id") @Min(1) @ApiParam(value = "用户id",required = true, type = "integer", example = "1") Integer id,
                                   @RequestParam("status") @ApiParam(value = "用户状态",required = true, type = "integer", example = "1") Integer status) {
        return iUserClientService.updateStatusById(id,status);
    }

    @PreAuthorize("hasAuthority('USER')")
    @ApiOperation(value = "获取当前登录用户接口",httpMethod = "GET",nickname = "getCurrentUser")
    @GetMapping("/current_user")
    public CustomUserDetailsImpl getCurrentUser() {
        CustomUserDetailsImpl principal = null;

        Object obj = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (obj instanceof CustomUserDetailsImpl) {
            principal = (CustomUserDetailsImpl) obj;
        }
        return principal;
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    @GetMapping("/get_test")
    @ApiOperation(value = "通过用户电话获取单个用户接口", httpMethod = "GET", nickname = "getTest")
    public String getTest( @ApiIgnore @TokenUser @Min(1) int userId) {

        try {

            String testStr = "测试 401 字符串 当前登录用户："+userId;

            System.out.println("Threed 前 执行");
            Thread.sleep(4000L);
            System.out.println("Thread 4秒后执行");
            System.out.println("正常返回结果 testStr = " + testStr+ "userId:"+userId);
            return testStr;
        }catch (Exception e) {
            System.out.println("测试 出异常");
            return e.getMessage();
        }
    }

}