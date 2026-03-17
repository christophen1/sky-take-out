package com.sky.controller.admin;

import com.sky.constant.JwtClaimsConstant;
import com.sky.constant.PasswordConstant;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.dto.PasswordEditDTO;
import com.sky.entity.Employee;
import com.sky.properties.JwtProperties;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.EmployeeService;
import com.sky.utils.JwtUtil;
import com.sky.vo.EmployeeLoginVO;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 员工管理
 */
@RestController
@RequestMapping("/admin/employee")
@Slf4j
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private JwtProperties jwtProperties;

    /**
     * 登录
     *
     * @param employeeLoginDTO
     * @return
     */
    @PostMapping("/login")
    public Result<EmployeeLoginVO> login(@RequestBody EmployeeLoginDTO employeeLoginDTO) {
        log.info("员工登录：{}", employeeLoginDTO);

        Employee employee = employeeService.login(employeeLoginDTO);

        //登录成功后，生成jwt令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.EMP_ID, employee.getId());
        String token = JwtUtil.createJWT(
                jwtProperties.getAdminSecretKey(),
                jwtProperties.getAdminTtl(),
                claims);

        EmployeeLoginVO employeeLoginVO = EmployeeLoginVO.builder()
                .id(employee.getId())
                .userName(employee.getUsername())
                .name(employee.getName())
                .token(token)
                .build();

        return Result.success(employeeLoginVO);
    }

    /**
     * 退出
     *
     * @return
     */

    @PostMapping("/logout")
    public Result<String> logout() {
        return Result.success();
    }

    /**
     * @description: 新增员工
     * @param: employeeDTO
     * @return: com.sky.result.Result
     * @author 22410
     * @date: 2026/3/12 15:33
     */
    @PostMapping
    @ApiOperation("新增员工")
    public Result addEmployee(@RequestBody EmployeeDTO employeeDTO){
        log.info("新增员工：{}",employeeDTO);
        employeeService.addEmployee(employeeDTO);
        return Result.success();
    }

    /**
     * @description: 员工信息分页查询
     * @param: employeePageQueryDTO
     * @return: com.sky.result.Result<com.sky.result.PageResult>
     */
    @GetMapping("/page")
    @ApiOperation("员工信息分页查询")
    public Result<PageResult> page(@ModelAttribute EmployeePageQueryDTO employeePageQueryDTO){
        log.info("员工信息分页查询：{}",employeePageQueryDTO);
        PageResult pageResult = employeeService.pageQuery(employeePageQueryDTO);
        return Result.success(pageResult);
    }
    
    /** 
     * @description: 
     * @param:  * @param status 参考{@link Integer} 
     * @param id 参考{@link Long}  
     * @return: com.sky.result.Result
     */
    @PostMapping("/status/{status}")
    @ApiOperation("启动或禁用员工")
    public Result startOrStop(@PathVariable Integer status, Long id){
        log.info("启用或禁用员工账号，{},{}",status,id);
        employeeService.startOrStop(status,id);
        return Result.success();
    }
    /**
     * @description: 根据id查询员工信息
     * @param:  * @param id 参考{@link Long}
     * @return: com.sky.result.Result<com.sky.entity.Employee>
     */
    @GetMapping("/{id}")
    @ApiOperation("根据id查询员工信息")
    public Result<Employee> getById(@PathVariable Long id){
        log.info("根据id查询员工信息：{}",id);
        Employee employee = employeeService.getById(id);
        return Result.success(employee);
    }
    /**
     * @description: 根据id修改员工信息
     * @param:  * @param employeeDTO 参考{@link EmployeeDTO}
     * @return: com.sky.result.Result
     */
    @PutMapping
    @ApiOperation("根据id修改员工信息")
    public Result update(@RequestBody EmployeeDTO employeeDTO){
        log.info("员工信息修改：{}",employeeDTO);
        employeeService.update(employeeDTO);
        return Result.success();
    }

    /**
     * @description: 修改密码
     * @param:  * @param passwordEditDTO 参考{@link PasswordEditDTO}
     * @return: com.sky.result.Result
     */
    @PutMapping("/editPassword")
    @ApiOperation("修改密码")
    public Result modifyPassword(@RequestBody PasswordEditDTO passwordEditDTO){
        log.info("修改密码：{}",passwordEditDTO);
        employeeService.modifyPassword(passwordEditDTO);
        return Result.success();
    }
}
