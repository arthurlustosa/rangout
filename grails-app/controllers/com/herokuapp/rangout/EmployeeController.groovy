package com.herokuapp.rangout

import grails.rest.RestfulController
import grails.transaction.*

import grails.converters.JSON

@Transactional(readOnly = true)
class EmployeeController extends RestfulController<Employee> {

    static allowedMethods = [save: "POST", list: "GET"]

    EmployeeController() {
        super(Employee)
    }

    def list() {
        String establishment_id = params?.establishment_id
        def establishment = Establishment.findById(Long.parseLong(establishment_id))

        def employees = Employee.findAllByEstablishment(establishment)
        JSON.use("employeeList") {
            render(contentType: 'application/json') {[
                employees:  employees,
                status:     employees.isEmpty() ? "Nothing present" : "OK"
            ]}
        }
    }

    def save() {
        String name = params?.name
        String username = params?.username
        String password = params?.password
        String establishment_id = params?.establishment_id

        Establishment establishment = Establishment.findById(Long.parseLong(establishment_id))
        def employee =
                new Employee(name: name, username: username, password: password, establishment: establishment)

        String message;
        if(employee.validate()) {
            employee.save()
            response.status = 201
            message = "Employee " + employee.name +
                    " registered with success in the establishment " + establishment.name
        } else {
            response.status = 400
            message = "Employee not registered, please check the constraints to register a employee"
        }
        JSON.use("employeeSave") {
            render(contentType: 'application/json') {[
                employee:   employee.hasErrors() ? []      : employee,
                status:     employee.hasErrors() ? "error" : "OK",
                message:    message
            ]}
        }
    }
}
