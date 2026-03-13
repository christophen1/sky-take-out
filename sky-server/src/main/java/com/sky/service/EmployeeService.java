package com.sky.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.result.PageResult;

public interface EmployeeService extends IService<Employee> {

    /**
     * 员工登录
     * @param employeeLoginDTO
     * @return Employee
     */
    Employee login(EmployeeLoginDTO employeeLoginDTO);

    /**
     * @description: 新增员工
     * @param: employeeDTO
     * @return: void
     */
    void addEmployee(EmployeeDTO employeeDTO);

    /**
     * @param employeePageQueryDTO
     * @description: 员工信息分页查询
     * @param: employeePageQueryDTO
     * @return: com.sky.result.PageResult
     */
    PageResult pageQuery(EmployeePageQueryDTO employeePageQueryDTO);

    /**
     * @description: 启动或禁用员工
     * @param:  * @param status 参考{@link Integer}
     * @param id 参考{@link Long}
     * @return: void
     */
    void startOrStop(Integer status, Long id);
        /**
         * @description: 更新员工信息
         * @param:  * @param employeeDTO 参考{@link EmployeeDTO}
         * @return: void
         */
    void update(EmployeeDTO employeeDTO);
}
