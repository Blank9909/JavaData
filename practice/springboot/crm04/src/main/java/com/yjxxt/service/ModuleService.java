package com.yjxxt.service;

import com.yjxxt.base.BaseService;
import com.yjxxt.dto.TreeDto;
import com.yjxxt.mapper.ModuleMapper;
import com.yjxxt.mapper.PermissionMapper;
import com.yjxxt.mapper.mapper.ModuleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ModuleService extends BaseService<Module,Integer> {

    @Autowired(required = false)
    private ModuleMapper moduleMapper;

    private PermissionMapper permissionMapper;
    /*查询所有的treeDto*/
    public List<TreeDto> queryAllModules(){
        return moduleMapper.selectModules();
    }

    public List<TreeDto> queryAllModules02(Integer roleId){
        //查询所有的treeDto
        List<TreeDto> treeDtos = moduleMapper.selectModules();
        //根据角色Id 查询角色用户的菜单id
        List<String> roleHaMids = permissionMapper.selectUserHasRolePermission(roleId);
        if(roleHaMids!=null&&roleHaMids.size()>0){
            treeDtos.forEach(treeDto -> {
                if(roleHaMids.contains(treeDto.getId())){
                    //说明当前角色分配了该菜单
                    treeDto.setChecked(true);
                }
            });
        }

        return treeDtos;

    }
}
